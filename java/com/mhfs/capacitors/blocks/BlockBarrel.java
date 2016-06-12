package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.TileBarrel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockBarrel extends BlockAdvContainer {
	
	public final static String name = "blockBarrel";

	public BlockBarrel(Material material) {
		super(material, name);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileBarrel();
		te.setWorldObj(world);
		return te;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		((TileBarrel)world.getTileEntity(pos)).onRightClick(world, player, hand);
		return true;
	}
}
