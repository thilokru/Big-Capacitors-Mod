package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.block.Block;
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

	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileLuxRouter router = (TileLuxRouter) world.getTileEntity(x, y, z);
		router.onDestroy();
		super.breakBlock(world, x, y, z, block, meta);
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
