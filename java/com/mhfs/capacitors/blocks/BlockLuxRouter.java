package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLuxRouter extends BlockContainer {

	public BlockLuxRouter(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileLuxRouter();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}
	
	public int getRenderBlockPass() {
		return 1;
	}
	
	public int getRenderType() {
		return -1;
	}

}
