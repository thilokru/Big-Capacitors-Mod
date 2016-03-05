package com.mhfs.capacitors.items;

import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMany extends Item {

	private ItemData[] data;

	public ItemMany(ItemData[] data) {
		super();
		this.data = data;
		setHasSubtypes(true);
	}

	public ItemData[] getData() {
		return data;
	}

	public String getUnlocalizedName(ItemStack stack) {
		return "item." + data[stack.getItemDamage()].getName();
	}

	public boolean hasEffect(ItemStack stack) {
		return data[stack.getItemDamage()].isSpecial();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < data.length; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}
}
