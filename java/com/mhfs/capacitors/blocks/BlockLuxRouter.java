package com.mhfs.capacitors.blocks;

import java.util.Random;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.render.RendererLux;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockLuxRouter extends BlockContainer {

	public final static String name = "blockLuxRouter";
	public BlockLuxRouter(Material mat) {
		super(mat);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
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

	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		RendererLux.createParticles(world, pos);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 3;
	}

}
