package com.mhfs.capacitors.misc;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class DefinedBlock extends BlockPos {
	
	private Block block;
	private int metadata;

	public DefinedBlock(int x, int y, int z, Block block, int metadata) {
		super(x, y, z);
		this.block = block;
		this.metadata = metadata;
	}
	
	public Block getBlockType(){
		return block;
	}
	
	public int getMetadata(){
		return metadata;
	}

	public boolean check(BlockPos init, World world) {
		BlockPos pos = this.translate(init);
		return block.equals(pos.getBlock(world)) && pos.getMetadata(world) == metadata;
	}

}
