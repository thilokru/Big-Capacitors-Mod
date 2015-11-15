package com.mhfs.capacitors.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Multiblock {

	private List<DefinedBlock> blocks;
	
	public Multiblock(ResourceLocation location, IResourceManager manager){
		try {
			blocks = new ArrayList<DefinedBlock>();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			String line;
			while((line = br.readLine()) != null){
				if(line.startsWith("#") || line.trim().equals(""))continue;
				String[] split = line.split(";");
				String[] coordSplit = split[0].split(" ");
				int x = Integer.parseInt(coordSplit[0]);
				int y = Integer.parseInt(coordSplit[1]);
				int z = Integer.parseInt(coordSplit[2]);
				
				String[] blockSplit = split[1].split(" ");
				Block block = Block.getBlockFromName(blockSplit[0]);
				int meta = blockSplit.length == 2?Integer.parseInt(blockSplit[1]):0;
				
				blocks.add(new DefinedBlock(x, y, z, block, meta));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private InputStream getInputStream(ResourceLocation loc, IResourceManager resourceManager) throws IOException{
		IResource resource = resourceManager.getResource(loc);
		return resource.getInputStream();
	}
	
	public boolean complete(BlockPos init, World world){
		boolean complete = true;
		for(DefinedBlock block:blocks){
			complete = complete && block.check(init, world);
		}
		return complete;
	}
}
