package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.fuelcell.TileFuelCell;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFuelCell extends BlockAdvContainer implements IOrientedBlock {

	public static final PropertyDirection ORIENTATION = PropertyDirection.create("orientation", EnumFacing.Plane.HORIZONTAL);
	
	public final static String name = "blockFuelCell";

	public BlockFuelCell(Material mat) {
		super(mat, name);
	
		this.setDefaultState(this.blockState.getBaseState().withProperty(ORIENTATION, EnumFacing.NORTH));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileFuelCell();
		te.setWorldObj(world);
		return te;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			return ((TileFuelCell) world.getTileEntity(pos)).onBlockActivated(player, heldItem);
		return true;
	}

	@Override
	public EnumFacing getOrientation(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getValue(ORIENTATION);
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		if (!(axis == EnumFacing.UP || axis == EnumFacing.DOWN)) {
			axis = EnumFacing.UP;
		}
		EnumFacing dir = getOrientation(world, pos);
		System.out.println(dir);
		dir = dir.rotateAround(axis.getAxis());
		world.setBlockState(pos, world.getBlockState(pos).withProperty(ORIENTATION, dir));
		return true;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(ORIENTATION, placer.getHorizontalFacing());
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
