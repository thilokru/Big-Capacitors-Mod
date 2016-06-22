package com.mhfs.capacitors.items;

import java.util.List;

import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.LuxAPI;
import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMultitool extends Item {

	public final static String name = "itemMultitool";
	
	public ItemMultitool(){
		setRegistryName(BigCapacitorsMod.modid, name);
		setUnlocalizedName(name);
		setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		GameRegistry.register(this);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return EnumActionResult.PASS;
		TileEntity entity = world.getTileEntity(pos);
		if (!player.isSneaking()) {
			if (entity instanceof TileCapacitor) {
				TileCapacitor cap = (TileCapacitor) entity;
				if (cap.getEntityCapacitor() == null)
					return EnumActionResult.FAIL;
				cap.getEntityCapacitor().onGroundSwitch(world);
				return EnumActionResult.SUCCESS;
			} else if (entity.hasCapability(LuxAPI.ROUTING_CAPABILITY, null)) {
				IRouting router1 = entity.getCapability(LuxAPI.ROUTING_CAPABILITY, null);
				if (stack.getTagCompound() == null) {
					stack.setTagCompound(new NBTTagCompound());
				}
				if (hasLocation(stack)) {
					BlockPos loc = getSavedLocation(stack);
					TileEntity tile2 = world.getTileEntity(loc);
					if(tile2.hasCapability(LuxAPI.ROUTING_CAPABILITY, null)){
						IRouting router2 = tile2.getCapability(LuxAPI.ROUTING_CAPABILITY, null);
						router1.onConnect(router2);
						router2.onConnect(router1);
					}
					removeLocation(stack);
				} else {
					saveLocation(stack, new BlockPos(pos));
				}
				return EnumActionResult.SUCCESS;
			}
		} else {
			if(entity instanceof TileEnergyTransciever){
				((TileEnergyTransciever)entity).switchMode();
			}
		}
		return EnumActionResult.FAIL;
	}

	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.setTagCompound(new NBTTagCompound());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean par4) {
		if (hasLocation(stack)) {
			String text = String.format("%s%s+LOC%s", ChatFormatting.ITALIC, ChatFormatting.DARK_PURPLE, ChatFormatting.RESET);
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
