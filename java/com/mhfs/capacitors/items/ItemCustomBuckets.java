package com.mhfs.capacitors.items;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;

public class ItemCustomBuckets extends ItemBucket {

	public ItemCustomBuckets(Block fluidBlock) {
		super(fluidBlock);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}

}
