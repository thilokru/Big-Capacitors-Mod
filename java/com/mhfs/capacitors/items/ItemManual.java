package com.mhfs.capacitors.items;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemManual extends Item {
	
	public final static String name = "itemManual";
	
	public ItemManual(){
		setUnlocalizedName(name);
		setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		setRegistryName(BigCapacitorsMod.modid, name);
		GameRegistry.register(this);
	}

	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			if (!player.isSneaking()) {
				RayTraceResult rtr = Minecraft.getMinecraft().objectMouseOver;
				BlockPos pos = rtr.getBlockPos();
				player.openGui(BigCapacitorsMod.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}else{
				player.openGui(BigCapacitorsMod.instance, 0, world, 0, 0, 0);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}
}
