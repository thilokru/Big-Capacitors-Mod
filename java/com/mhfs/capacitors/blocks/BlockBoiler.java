package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.destillery.TileBoiler;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockBoiler extends BlockAdvContainer {

	public final static String name = "blockBoiler";

	public BlockBoiler(Material mat) {
		super(mat, name, true);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBoiler();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			return ((TileBoiler) world.getTileEntity(pos)).onBlockActivated(player, heldItem);
		return true;
	}
}
