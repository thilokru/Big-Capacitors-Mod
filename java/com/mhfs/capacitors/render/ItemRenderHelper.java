package com.mhfs.capacitors.render;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.Items;
import com.mhfs.capacitors.blocks.BlockData;
import com.mhfs.capacitors.items.ItemData;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;

public class ItemRenderHelper {
	
	private final static String fluidStateModel = "fluidState";

	public static void setupItemModels(BigCapacitorsMod mod) {
		registerFluidBlock(Fluids.blockDestilledWater, "destilledWater");
		registerFluidBlock(Fluids.blockEthanol, "ethanol");
		registerFluidBlock(Fluids.blockHydrogen, "hydrogen");
		registerFluidBlock(Fluids.blockWine, "wine");
		
		registerMesher(Blocks.capacitorIron);
		registerMesher(Blocks.blockBarrel);
		registerMesher(Blocks.blockFuelCell);
		registerMesher(Blocks.blockLuxRouter);
		registerMesher(Blocks.blockEnergyTransfer);
		registerMesher(Blocks.blockTokamak);
		registerMesher(Blocks.blockBoiler);
		registerMesher(Blocks.blockTower);
		registerMesher(Blocks.blockStirlingEngine);
		registerMesher(Blocks.blockCrusher);

		registerMesher(Items.itemManual);
		registerMesher(Items.itemMultitool);
		
		registerSubItems();
		registerSubBlocks();
	}
	
	private static void registerSubBlocks(){
		BlockData[] blockData = Blocks.blockMany.getData();
		ResourceLocation loc;
		Item item = Item.getItemFromBlock(Blocks.blockMany);
		for(int i = 0; i < blockData.length; i++){
			loc = new ResourceLocation(BigCapacitorsMod.modid, blockData[i].getName());
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(loc, "inventory"));
		}
	}
	
	private static void registerSubItems(){
		ItemData[] itemData = Items.itemMany.getData();
		ResourceLocation loc;
		for(int i = 0; i < itemData.length; i++){
			loc = new ResourceLocation(BigCapacitorsMod.modid, itemData[i].getName());
			ModelLoader.setCustomModelResourceLocation(Items.itemMany, i, new ModelResourceLocation(loc, "inventory"));
		}
	}

	private static void registerFluidBlock(BlockFluidClassic block, String identifier) {
		final ModelResourceLocation mrl = new ModelResourceLocation(new ResourceLocation(BigCapacitorsMod.modid, fluidStateModel), identifier);
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return mrl;
			}
		});
	}

	private static void registerMesher(Block block) {
		registerMesher(Item.getItemFromBlock(block));
	}

	private static void registerMesher(Item item) {
		registerMesher(item, 0);
	}

	private static void registerMesher(Item item, int meta) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
