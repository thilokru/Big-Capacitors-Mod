package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.TileTokamak;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockTokamak extends BlockAdvContainer{
	
	public final static String name = "blockTokamak";

	public BlockTokamak(Material material) {
		super(material, name, true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileTokamak();
		te.setWorldObj(world);
		return te;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			return ((TileTokamak) world.getTileEntity(pos)).onBlockActivated(player, heldItem);
		return true;
	}
}
