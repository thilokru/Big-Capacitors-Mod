package com.mhfs.capacitors;

import java.util.ArrayList;
import java.util.List;

import com.mhfs.capacitors.items.ItemData;
import com.mhfs.capacitors.items.ItemManual;
import com.mhfs.capacitors.items.ItemMany;
import com.mhfs.capacitors.items.ItemMultitool;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Items extends net.minecraft.init.Items{

	public static Item itemMultitool;
	public static Item itemManual;
	public static ItemMany itemMany;
	public static ItemMany itemDust;
	
	public static ItemStack itemStackWire;
	public static ItemStack itemStackHeater;
	public static ItemStack itemStackFPGA;
	public static ItemStack itemStackCopper;
	
	public static ItemStack itemStackDustRutil;
	public static ItemStack itemStackDustWitherite;
	public static ItemStack itemStackDustIron;
	public static ItemStack itemStackDustGold;
	public static ItemStack itemStackDustWitheriteRutilComposit;
	
	public static void setup(BigCapacitorsMod mod){
		Items.itemMultitool = new ItemMultitool().setMaxStackSize(1);
		
		Items.itemManual = new ItemManual().setMaxStackSize(1);
		
		
		List<ItemData> data = new ArrayList<ItemData>();
		
		data.add(new ItemData("itemWire", false));//0
		data.add(new ItemData("itemHeater", false));//1
		data.add(new ItemData("itemFPGA", true));//2
		data.add(new ItemData("ingotCopper", false));//3
		data.add(new ItemData("itemDustWitheriteRutilComposit", false));//4
		
		Items.itemMany = new ItemMany(data.toArray(new ItemData[0]));
		Items.itemMany.setCreativeTab(mod.creativeTab);
		Items.itemMany.setRegistryName(BigCapacitorsMod.modid, "itemMany");
		GameRegistry.register(Items.itemMany);
		
		
		List<ItemData> dustData = new ArrayList<ItemData>();
		
		dustData.add(new ItemData("itemDustRutil", false));//0, TiO2
		dustData.add(new ItemData("itemDustWitherite", false));//1, BaCO3
		dustData.add(new ItemData("itemDustIron", false));//2
		dustData.add(new ItemData("itemDustGold", false));//3
		
		Items.itemDust = new ItemMany(dustData.toArray(new ItemData[0]));
		Items.itemDust.setCreativeTab(mod.creativeTab);
		Items.itemDust.setRegistryName(BigCapacitorsMod.modid, "itemDust");
		GameRegistry.register(Items.itemDust);
		
		
		itemStackWire = new ItemStack(itemMany, 1, 0);
		itemStackHeater = new ItemStack(itemMany, 1, 1);
		itemStackFPGA = new ItemStack(itemMany, 1, 2);
		itemStackCopper = new ItemStack(itemMany, 1, 3);
		itemStackDustWitheriteRutilComposit = new ItemStack(itemMany, 1, 4);
		
		itemStackDustRutil = new ItemStack(itemDust, 1, 0);
		itemStackDustWitherite = new ItemStack(itemDust, 1, 1);
		itemStackDustIron = new ItemStack(itemDust, 1, 2);
		itemStackDustGold = new ItemStack(itemDust, 1, 3);
		
		OreDictionary.registerOre("dustTitandioxid", itemStackDustRutil);
		OreDictionary.registerOre("dustBariumCarbonate",  itemStackDustWitherite);
		OreDictionary.registerOre("dustIron", itemStackDustIron);
		OreDictionary.registerOre("dustGold", itemStackDustGold);
		OreDictionary.registerOre("ingotCopper", itemStackCopper);
	}
}
