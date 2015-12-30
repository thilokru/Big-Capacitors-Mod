package com.mhfs.capacitors;

import java.util.ArrayList;
import java.util.List;

import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.items.ItemCustomBuckets;
import com.mhfs.capacitors.items.ItemData;
import com.mhfs.capacitors.items.ItemLuxRouter;
import com.mhfs.capacitors.items.ItemManual;
import com.mhfs.capacitors.items.ItemMany;
import com.mhfs.capacitors.items.ItemMultitool;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class Items extends net.minecraft.init.Items{

	public static Item itemMultitool;
	public static Item itemLuxRouter;
	public static Item itemManual;
	public static Item itemBucketDestilledWater;
	public static Item itemBucketEthanol;
	public static Item itemBucketWine;
	public static Item itemBucketHydrogen;
	public static ItemMany itemMany;
	
	public static void setup(BigCapacitorsMod mod){
		Items.itemMultitool = new ItemMultitool().setUnlocalizedName("multitool").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(Items.itemMultitool, "multitool");
		
		Items.itemManual = new ItemManual().setUnlocalizedName("manual").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(Items.itemManual, "manual");
		
		Items.itemLuxRouter = new ItemLuxRouter().setUnlocalizedName("router").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(Items.itemLuxRouter, "router");
		
		List<ItemData> data = new ArrayList<ItemData>();
		
		data.add(new ItemData("wire", "big_capacitors:wire", false));
		data.add(new ItemData("heater", "big_capacitors:heater", false));
		data.add(new ItemData("dustRutil", "big_capacitors:dustRutil", false));//2, TiO2
		data.add(new ItemData("dustWitherite", "big_capacitors:dustWitherite", false));//3, BaCO3
		data.add(new ItemData("fusionProcessor", "big_capacitors:fusionProcessor", true));//4
		
		Items.itemMany = new ItemMany(data.toArray(new ItemData[0]));
		Items.itemMany.setCreativeTab(mod.creativeTab);
		Items.itemMany.injectSubItems();
		GameRegistry.registerItem(Items.itemMany, "itemMany");
		
		OreDictionary.registerOre("dustTitandioxid", new ItemStack(Items.itemMany, 1, 2));
		OreDictionary.registerOre("dustBariumCarbonate",  new ItemStack(Items.itemMany, 1, 3));
	
		Items.itemBucketDestilledWater = createBucket(Fluids.fluidDestilledWater, Fluids.blockDestilledWater, "bucketDestilledWater");
		Items.itemBucketEthanol = createBucket(Fluids.fluidEthanol, Fluids.blockEthanol, "bucketEthanol");
		Items.itemBucketWine = createBucket(Fluids.fluidWine, Fluids.blockWine, "bucketWine");
		Items.itemBucketHydrogen = createBucket(Fluids.gasHydrogen, Fluids.blockHydrogen, "bucketHydrogen");
	}
	
	private static Item createBucket(Fluid fluid, Block fluidBlock, String unloc){
		Item bucket = new ItemCustomBuckets(fluidBlock);
		bucket.setUnlocalizedName(unloc).setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucket, unloc);
		FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.bucket));
		BucketHandler.FLUID_BLOCK_TO_BUCKET.put(fluidBlock, bucket);
		return bucket;
	}
}
