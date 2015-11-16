package com.mhfs.capacitors.items;

import com.mhfs.capacitors.Blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMany extends ItemBlock {

	public ItemBlockMany(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return Blocks.blockMany.getUnlocalizedSubName(itemstack.getItemDamage());
	}

}
