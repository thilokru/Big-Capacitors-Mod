package com.mhfs.capacitors.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IActivatable {

	public boolean onBlockActivated(EntityPlayer player, ItemStack heldItem);
	
}
