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
		this.setRegistryName(BigCapacitorsMod.modid, unloc);
		GameRegistry.register(this);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}

}
