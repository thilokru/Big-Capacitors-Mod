package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockEnergyTransfer extends BlockContainer implements IOrientedBlock {

	public static final PropertyDirection ORIENTATION = PropertyDirection.create("orientation");
	
	public final static String name = "blockEnergyTransfer";

	public BlockEnergyTransfer(Material mat) {
		super(mat);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ORIENTATION, EnumFacing.DOWN));
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEnergyTransciever();
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
		return this.getDefaultState().withProperty(ORIENTATION, facing);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
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

	public int getRenderType() {
		return 3;
	}

}
