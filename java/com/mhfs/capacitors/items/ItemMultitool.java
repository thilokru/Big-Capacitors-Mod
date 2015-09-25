package com.mhfs.capacitors.items;

import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemMultitool extends Item {

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float aimX, float aimY, float aimZ) {
		if(world.isRemote)return false;
		TileEntity entity = world.getTileEntity(x, y, z);
		if(entity instanceof TileCapacitor){
			TileCapacitor cap = (TileCapacitor)entity;
			if(cap.getEntityCapacitor() == null)return false;
			cap.getEntityCapacitor().onGroundSwitch(world);
			return true;
		}
		return false;
	}

}
