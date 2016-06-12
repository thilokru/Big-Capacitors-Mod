package com.mhfs.capacitors.blocks;

import java.util.Random;

import com.mhfs.capacitors.render.RendererLux;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockEnergyTransfer extends BlockAdvContainer implements IOrientedBlock {

	public static final PropertyDirection ORIENTATION = PropertyDirection.create("orientation");
	
	public final static String name = "blockEnergyTransfer";

	public BlockEnergyTransfer(Material mat) {
		super(mat, name);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ORIENTATION, EnumFacing.DOWN));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileEnergyTransciever();
		te.setWorldObj(world);
		return te;
	}

	@Override
	public EnumFacing getOrientation(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getValue(ORIENTATION);
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		EnumFacing dir = getOrientation(world, pos);
		dir = dir.rotateAround(axis.getAxis());
		world.setBlockState(pos, this.getDefaultState().withProperty(ORIENTATION, dir));
		return true;
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEnergyTransciever target = (TileEnergyTransciever) world.getTileEntity(pos);
		target.onDestroy();
		super.breakBlock(world, pos, state);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(ORIENTATION, facing.getOpposite());
	}
	
	@Override
	public void randomDisplayTick(IBlockState stae, World world, BlockPos pos, Random rand) {
		RendererLux.createParticles(world, pos);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ORIENTATION, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ORIENTATION).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ORIENTATION });
	}

}
