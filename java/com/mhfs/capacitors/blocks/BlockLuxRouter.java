package com.mhfs.capacitors.blocks;

import java.util.Random;

import com.mhfs.capacitors.render.RendererLux;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLuxRouter extends BlockAdvContainer {

	public final static String name = "blockLuxRouter";
	public BlockLuxRouter(Material mat) {
		super(mat, name);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileLuxRouter();
		te.setWorldObj(world);
		return te;
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileLuxRouter router = (TileLuxRouter) world.getTileEntity(pos);
		router.onDestroy();
		super.breakBlock(world, pos, state);
	}

	@Override
	public void randomDisplayTick(IBlockState stae, World world, BlockPos pos, Random rand) {
		RendererLux.createParticles(world, pos);
	}

}
