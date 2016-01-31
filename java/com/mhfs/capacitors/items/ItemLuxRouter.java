package com.mhfs.capacitors.items;

import java.util.List;

import com.mhfs.capacitors.tile.lux.LuxHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemLuxRouter extends Item {

	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.stackTagCompound = new NBTTagCompound();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean par4) {
		if(hasLocation(stack)){
			String text = String.format("%s%s+LOC%s", EnumChatFormatting.ITALIC, EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.RESET);
			info.add(text);
		}
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float aimX, float aimY, float aimZ) {
		if (world.isRemote) {
			return false;
		}
		TileEntity entity = world.getTileEntity(x, y, z);
		if (entity instanceof LuxHandler) {
			LuxHandler router = (LuxHandler) entity;
			if (stack.getTagCompound() == null) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if (hasLocation(stack)) {
				int[] loc = getSavedLocation(stack);
				router.connect(loc[0], loc[1], loc[2]);
				removeLocation(stack);
			} else {
				saveLocation(stack, x, y, z);
			}
			return true;
		}
		return false;
	}

	private int[] getSavedLocation(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		return new int[] { x, y, z };
	}

	private void saveLocation(ItemStack stack, int x, int y, int z) {
		NBTTagCompound tag = stack.getTagCompound();
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
	}

	private void removeLocation(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)return;
		tag.removeTag("x");
		tag.removeTag("y");
		tag.removeTag("z");
	}

	private boolean hasLocation(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)return false;
		return tag.hasKey("x");
	}

}
