package com.mhfs.capacitors.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mhfs.api.helper.DefinedBlock;
import com.mhfs.api.helper.Helper;

import net.minecraft.block.Block;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Multiblock {

	private final static BiMap<ResourceLocation, Multiblock> INSTANCES;

	static {
		INSTANCES = HashBiMap.<ResourceLocation, Multiblock>create();
	}
	
	public static Multiblock getMultiblock(ResourceLocation rl, IResourceManager manager) {
		if(INSTANCES.get(rl) == null) {
			INSTANCES.put(rl, new Multiblock(rl, manager));
		}
		return INSTANCES.get(rl);
	}
	
	public static ResourceLocation getRegistryName(Multiblock mb) {
		return INSTANCES.inverse().get(mb);
	}

	protected List<DefinedBlock> blocks;
	private ResourceLocation location;

	private Multiblock(ResourceLocation location, IResourceManager manager) {
		this.location = location;
		load(manager);
	}

	protected void load(IResourceManager manager) {
		try {
			blocks = new ArrayList<DefinedBlock>();

			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#") || line.trim().equals(""))
					continue;
				String[] split = line.split(";");
				String[] coordSplit = split[0].split(" ");
				int x = Integer.parseInt(coordSplit[0]);
				int y = Integer.parseInt(coordSplit[1]);
				int z = Integer.parseInt(coordSplit[2]);

				String[] blockSplit = split[1].split(" ");
				Block block = Block.getBlockFromName(blockSplit[0]);
				int meta = blockSplit.length == 2 ? Integer.parseInt(blockSplit[1]) : -1;

				blocks.add(new DefinedBlock(x, y, z, block, meta));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream getInputStream(ResourceLocation loc, IResourceManager resourceManager) throws IOException {
		IResource resource = resourceManager.getResource(loc);
		return resource.getInputStream();
	}

	public boolean complete(BlockPos init, World world) {
		boolean complete = true;
		for (DefinedBlock block : blocks) {
			complete = complete && block.check(init, world);
		}
		return complete;
	}

	public EnumFacing getCompletedRotation(BlockPos init, World world) {
		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
			if (complete(init, world, facing))
				return facing;
		}
		return null;
	}

	/**
	 * Assuming default facing is north, allows only horizontal rotation.
	 */
	public boolean complete(BlockPos init, World world, EnumFacing facing) {
		boolean complete = true;
		for (DefinedBlock block : blocks) {
			Vec3i offset = Helper.rotateVector(block, facing);
			DefinedBlock actualBlock = new DefinedBlock(offset, block.getBlockType(), block.getMetadata());
			complete = complete && actualBlock.check(init, world);
		}
		return complete;
	}

	public Set<DefinedBlock> getBlocks(BlockPos pos, EnumFacing facing) {
		Set<DefinedBlock> retValue = new HashSet<DefinedBlock>();
		for (DefinedBlock block : blocks) {
			Vec3i offset = Helper.rotateVector(block, facing);
			DefinedBlock actualBlock = new DefinedBlock(offset, block.getBlockType(), block.getMetadata());
			actualBlock.add(pos);
			retValue.add(actualBlock);
		}
		return retValue;
	}
}
