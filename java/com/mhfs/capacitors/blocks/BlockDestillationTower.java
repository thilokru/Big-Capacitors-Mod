package com.mhfs.capacitors.blocks;

import java.util.Random;

import com.mhfs.capacitors.tile.TileTower;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDestillationTower extends BlockAdvContainer {

	public final static String name = "blockDestillationTower";

	public BlockDestillationTower(Material mat) {
		super(mat, name);
	}

	@Override
	public void randomDisplayTick(IBlockState stae, World world, BlockPos pos, Random rand) {
		TileTower tower = ((TileTower) world.getTileEntity(pos));
		if (tower.isReleasingSteam()) {
			world.spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.3, 0);
		}
		tower.resetSteamState();
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			return ((TileTower) world.getTileEntity(pos)).onBlockActivated(player);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTower();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double sixteenth = 1D / 16;
		return new AxisAlignedBB(sixteenth, 0, sixteenth, 1 - sixteenth, 1, 1 - sixteenth);
	}

}
