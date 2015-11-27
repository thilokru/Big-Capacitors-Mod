package com.mhfs.capacitors.village;

import java.util.Random;

import com.mhfs.capacitors.Items;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class TradeHandler implements IVillageTradeHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 44 + random.nextInt(20)), new ItemStack(Items.itemMany, 1, 4)));
	}

}
