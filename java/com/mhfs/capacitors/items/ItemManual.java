package com.mhfs.capacitors.items;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemManual extends Item {
	
	public final static String name = "itemManual";
	
	public ItemManual(){
		setUnlocalizedName(name);
		setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		GameRegistry.registerItem(this, name);
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			if (!player.isSneaking()) {
				RayTraceResult rtr = Minecraft.getMinecraft().objectMouseOver;
				BlockPos pos = rtr.getBlockPos();
				player.openGui(BigCapacitorsMod.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
				return stack;
			}else{
				player.openGui(BigCapacitorsMod.instance, 0, world, 0, 0, 0);
			}
		}
		return stack;
	}
}
