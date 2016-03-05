package com.mhfs.capacitors.items;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemManual extends Item {
	
	public final static String name = "itemManual";
	
	public ItemManual(){
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(name);
		setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			if (!player.isSneaking()) {
				MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
				BlockPos pos = mop.getBlockPos();
				player.openGui(BigCapacitorsMod.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
				return stack;
			}else{
				player.openGui(BigCapacitorsMod.instance, 0, world, 0, 0, 0);
			}
		}
		return stack;
	}
}
