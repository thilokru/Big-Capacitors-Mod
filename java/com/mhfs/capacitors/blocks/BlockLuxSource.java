package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.lux.TileSource;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLuxSource extends BlockContainer{

	public BlockLuxSource(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileSource();
	}
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileSource source = (TileSource) world.getTileEntity(x, y, z);
		source.onDestroy();
		super.breakBlock(world, x, y, z, block, meta);
	}

}
