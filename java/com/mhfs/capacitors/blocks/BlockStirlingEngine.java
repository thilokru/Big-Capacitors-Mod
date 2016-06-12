package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.TileStirlingEngine;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStirlingEngine extends BlockAdvContainer {
	
	public final static String name = "blockStirlingEngine";
	
	public static final PropertyDirection ORIENTATION = PropertyDirection.create("orientation", EnumFacing.Plane.HORIZONTAL);

	public BlockStirlingEngine(Material mat) {
		super(mat, name);
		
		this.setDefaultState(this.blockState.getBaseState().withProperty(ORIENTATION, EnumFacing.NORTH));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileStirlingEngine();
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(ORIENTATION, placer.getHorizontalFacing().getOpposite());
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
