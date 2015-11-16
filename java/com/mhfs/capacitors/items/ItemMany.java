package com.mhfs.capacitors.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemMany extends Item {

	private ItemData[] data;

	public ItemMany(ItemData[] data) {
		super();
		this.data = data;
		setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		for (int i = 0; i < data.length; i++) {
			data[i].loadIcon(register);
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return data[meta].getIcon();
	}

	public String getUnlocalizedName(ItemStack stack) {
		return "item." + data[stack.getItemDamage()].getName();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for(int i = 0; i < data.length; i++){
			list.add(new ItemStack(this, 1, i));
		}
	}

	public void injectSubItems() {
		for (int i = 0; i < data.length; i++) {
			GameRegistry.registerCustomItemStack(data[i].getName(), new ItemStack(this, 1, i));
		}
	}
}
