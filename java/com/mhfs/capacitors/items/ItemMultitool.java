package com.mhfs.capacitors.items;

import java.util.List;

import com.mhfs.api.lux.LuxHandler;
import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMultitool extends Item {

	public final static String name = "itemMutiltool";
	
	public ItemMultitool(){
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(BigCapacitorsMod.modid + "_" + name);
		setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, net.minecraft.util.BlockPos pos, EnumFacing side, float aimX, float aimY, float aimZ) {
		if (world.isRemote)
			return false;
		TileEntity entity = world.getTileEntity(pos);
		if (!player.isSneaking()) {
			if (entity instanceof TileCapacitor) {
				TileCapacitor cap = (TileCapacitor) entity;
				if (cap.getEntityCapacitor() == null)
					return false;
				cap.getEntityCapacitor().onGroundSwitch(world);
				return true;
			} else if (entity instanceof LuxHandler) {
				LuxHandler router = (LuxHandler) entity;
				if (stack.getTagCompound() == null) {
					stack.setTagCompound(new NBTTagCompound());
				}
				if (hasLocation(stack)) {
					BlockPos loc = getSavedLocation(stack);
					router.connect(loc);
					removeLocation(stack);
				} else {
					saveLocation(stack, new BlockPos(pos));
				}
				return true;
			}
		} else {
			if(entity instanceof TileEnergyTransciever){
				((TileEnergyTransciever)entity).switchMode();
			}
		}
		return false;
	}

	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.setTagCompound(new NBTTagCompound());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean par4) {
		if (hasLocation(stack)) {
			String text = String.format("%s%s+LOC%s", EnumChatFormatting.ITALIC, EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.RESET);
			info.add(text);
		}
	}

	private BlockPos getSavedLocation(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return BlockPos.fromLong(tag.getLong("loc"));
	}

	private void saveLocation(ItemStack stack, BlockPos pos) {
		NBTTagCompound tag = stack.getTagCompound();
		tag.setLong("loc", pos.toLong());
	}

	private void removeLocation(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)return;
		tag.removeTag("loc");
	}

	private boolean hasLocation(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)return false;
		return tag.hasKey("loc");
	}
}
