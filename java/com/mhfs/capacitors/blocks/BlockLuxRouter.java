package com.mhfs.capacitors.blocks;

import java.util.Random;

import com.mhfs.capacitors.misc.BlockPos;
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

	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		TileLuxRouter tile = (TileLuxRouter) world.getTileEntity(x, y, z);
		BlockPos local = new BlockPos(x,y,z);
		if(tile == null)return;
		
		for(BlockPos towards:tile.getPoweredConnections()){
			BlockPos vektor = local.getVektor(towards);
			double length = vektor.getLength() * 20;
			double xMotion = vektor.x / length;
			double yMotion = vektor.y / length;
			double zMotion = vektor.z / length;
			world.spawnParticle("cloud", x + 0.5, y + 0.5, z + 0.5, xMotion, yMotion, zMotion);
		}
		tile.resetPoweredState();
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
