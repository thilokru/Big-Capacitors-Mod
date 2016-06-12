package com.mhfs.capacitors.items;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemCustomBuckets extends ItemBucket {

	public ItemCustomBuckets(Block fluidBlock, String unloc) {
		super(fluidBlock);
		this.setUnlocalizedName(unloc).setContainerItem(Items.BUCKET);
		GameRegistry.registerItem(this, unloc);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}

}
