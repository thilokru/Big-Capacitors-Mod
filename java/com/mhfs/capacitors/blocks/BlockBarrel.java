package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.TileBarrel;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBarrel extends BlockContainer {

	public BlockBarrel(Material material) {
		super(material);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileBarrel();
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ) {
		((TileBarrel)world.getTileEntity(x, y, z)).onRightClick(world, player);
		return true;
	}

}
