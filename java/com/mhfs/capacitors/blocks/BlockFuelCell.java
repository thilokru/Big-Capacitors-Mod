package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.fuelcell.TileFuelCell;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockFuelCell extends BlockContainer implements IOrientedBlock {

	public static final PropertyDirection ORIENTATION = PropertyDirection.create("orientation", EnumFacing.Plane.HORIZONTAL);
	
	public final static String name = "blockFuelCell";

	public BlockFuelCell(Material mat) {
		super(mat);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ORIENTATION, EnumFacing.NORTH));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileFuelCell();
		te.setWorldObj(world);
		return te;
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
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { ORIENTATION });
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public int getRenderType() {
		return 3;
	}

}
