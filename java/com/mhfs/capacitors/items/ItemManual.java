package com.mhfs.capacitors.items;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemManual extends Item {

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			if (!player.isSneaking()) {
				MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
				player.openGui(BigCapacitorsMod.instance, 1, world, mop.blockX, mop.blockY, mop.blockZ);
				return stack;
			}else{
				player.openGui(BigCapacitorsMod.instance, 0, world, 0, 0, 0);
			}
		}
		return stack;
	}
}
