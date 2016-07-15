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
	
	public static ItemStack itemStackWire;
	public static ItemStack itemStackHeater;
	public static ItemStack itemStackDustRutil;
	public static ItemStack itemStackDustWitherite;
	public static ItemStack itemStackFusionProcessor;
	public static ItemStack itemStackCopper;
	
	public static void setup(BigCapacitorsMod mod){
		Items.itemMultitool = new ItemMultitool().setMaxStackSize(1);
		
		Items.itemManual = new ItemManual().setMaxStackSize(1);
		
		List<ItemData> data = new ArrayList<ItemData>();
		
		data.add(new ItemData("itemWire", false));//0
		data.add(new ItemData("itemHeater", false));//1
		data.add(new ItemData("itemDustRutil", false));//2, TiO2
		data.add(new ItemData("itemDustWitherite", false));//3, BaCO3
		data.add(new ItemData("itemFPGA", true));//4
		data.add(new ItemData("ingotCopper", false));//5
		
		Items.itemMany = new ItemMany(data.toArray(new ItemData[0]));
		Items.itemMany.setCreativeTab(mod.creativeTab);
		Items.itemMany.setRegistryName(BigCapacitorsMod.modid, "itemMany");
		GameRegistry.register(Items.itemMany);
		
		itemStackWire = new ItemStack(itemMany, 1, 0);
		itemStackHeater = new ItemStack(itemMany, 1, 1);
		itemStackDustRutil = new ItemStack(itemMany, 1, 2);
		itemStackDustWitherite = new ItemStack(itemMany, 1, 3);
		itemStackFusionProcessor = new ItemStack(itemMany, 1, 4);
		itemStackCopper = new ItemStack(itemMany, 1, 5);
		
		OreDictionary.registerOre("dustTitandioxid", new ItemStack(Items.itemMany, 1, 2));
		OreDictionary.registerOre("dustBariumCarbonate",  new ItemStack(Items.itemMany, 1, 3));
		OreDictionary.registerOre("ingotCopper", new ItemStack(Items.itemMany, 1, 5));
	}
}
