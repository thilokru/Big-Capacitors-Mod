package com.mhfs.capacitors;

import java.util.ArrayList;
import java.util.List;

import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.items.ItemCustomBuckets;
import com.mhfs.capacitors.items.ItemData;
import com.mhfs.capacitors.items.ItemManual;
import com.mhfs.capacitors.items.ItemMany;
import com.mhfs.capacitors.items.ItemMultitool;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class Items extends net.minecraft.init.Items{

	public static Item itemMultitool;
	public static Item itemManual;
	public static Item itemBucketDestilledWater;
	public static Item itemBucketEthanol;
	public static Item itemBucketWine;
	public static Item itemBucketHydrogen;
	public static ItemMany itemMany;
	
	public static void setup(BigCapacitorsMod mod){
		Items.itemMultitool = new ItemMultitool().setMaxStackSize(1);
		
		Items.itemManual = new ItemManual().setMaxStackSize(1);
		
		List<ItemData> data = new ArrayList<ItemData>();
		
		data.add(new ItemData("itemWire", false));//0
		data.add(new ItemData("itemHeater", false));//1
		data.add(new ItemData("itemDustRutil", false));//2, TiO2
		data.add(new ItemData("itemDustWitherite", false));//3, BaCO3
		data.add(new ItemData("itemFusionProcessor", true));//4
		data.add(new ItemData("ingotCopper", false));//5
		
		Items.itemMany = new ItemMany(data.toArray(new ItemData[0]));
		Items.itemMany.setCreativeTab(mod.creativeTab);
		GameRegistry.registerItem(Items.itemMany, "itemMany");
		
		OreDictionary.registerOre("dustTitandioxid", new ItemStack(Items.itemMany, 1, 2));
		OreDictionary.registerOre("dustBariumCarbonate",  new ItemStack(Items.itemMany, 1, 3));
		OreDictionary.registerOre("ingotCopper", new ItemStack(Items.itemMany, 1, 5));
	
		Items.itemBucketDestilledWater = createBucket(Fluids.fluidDestilledWater, Fluids.blockDestilledWater, "bucketDestilledWater");
		Items.itemBucketEthanol = createBucket(Fluids.fluidEthanol, Fluids.blockEthanol, "bucketEthanol");
		Items.itemBucketWine = createBucket(Fluids.fluidWine, Fluids.blockWine, "bucketWine");
		Items.itemBucketHydrogen = createBucket(Fluids.gasHydrogen, Fluids.blockHydrogen, "bucketHydrogen");
	}
	
	private static Item createBucket(Fluid fluid, Block fluidBlock, String unloc){
		Item bucket = new ItemCustomBuckets(fluidBlock, unloc);
		FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.BUCKET));
		BucketHandler.FLUID_BLOCK_TO_BUCKET.put(fluidBlock, bucket);
		return bucket;
	}
}
