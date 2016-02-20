package com.mhfs.capacitors.blocks;

import java.util.Random;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
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
		return new TileLuxRouter();
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileLuxRouter router = (TileLuxRouter) world.getTileEntity(pos);
		router.onDestroy();
		super.breakBlock(world, pos, state);
	}

	public void randomDisplayTick(World world, BlockPos pos, Random random) {
		TileLuxRouter tile = (TileLuxRouter) world.getTileEntity(pos);
		BlockPos local = new BlockPos(pos);
		if (tile == null)
			return;

		for (BlockPos towards : tile.getPoweredConnections()) {
			BlockPos vektor = towards.subtract(local);
			double length = Math.sqrt(Math.pow(vektor.getX(), 2) + Math.pow(vektor.getY(), 2) + Math.pow(vektor.getZ(), 2)) * 20;
			double xMotion = vektor.getX() / length;
			double yMotion = vektor.getY() / length;
			double zMotion = vektor.getZ() / length;
			world.spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xMotion, yMotion, zMotion);
		}
		tile.resetPoweredState();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 3;
	}

}
