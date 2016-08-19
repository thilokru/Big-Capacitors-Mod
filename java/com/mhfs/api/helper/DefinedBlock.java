package com.mhfs.api.helper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class DefinedBlock extends BlockPos {
	
	private Block block;
	private int metadata;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param metadata -1 means irrelevant
	 */
	public DefinedBlock(int x, int y, int z, Block block, int metadata) {
		super(x, y, z);
		this.block = block;
		this.metadata = metadata;
	}
	
	public DefinedBlock(Vec3i offset, Block blockType, int metadata) {
		this(offset.getX(), offset.getY(), offset.getZ(), blockType, metadata);
	}

	public Block getBlockType(){
		return block;
	}
	
	public int getMetadata(){
		return metadata;
	}

	public boolean check(BlockPos init, World world) {
		BlockPos pos = this.add(init);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		boolean blockMatch = this.block.equals(block);
		boolean metaMatch = (block.getMetaFromState(state) == metadata || metadata == -1);
		return blockMatch && metaMatch;
	}

}
