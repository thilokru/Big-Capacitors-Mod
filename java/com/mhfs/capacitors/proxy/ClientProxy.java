package com.mhfs.capacitors.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Items;
import com.mhfs.capacitors.gui.DebugOverlayHandler;
import com.mhfs.capacitors.gui.GuiOverlayHandler;
import com.mhfs.capacitors.gui.ManualOverlayHandler;
import com.mhfs.capacitors.gui.MultitoolOverlayHandler;
import com.mhfs.capacitors.handlers.GuiHandler;
import com.mhfs.capacitors.knowledge.SimpleReloadableKnowledgeRegistry;
import com.mhfs.capacitors.render.RendererCapacitor;
import com.mhfs.capacitors.render.RendererLuxRouter;
import com.mhfs.capacitors.render.RendererOBJ;
import com.mhfs.capacitors.tile.TileBarrel;
import com.mhfs.capacitors.tile.TileFuelCell;
import com.mhfs.capacitors.tile.destillery.TileDistillery;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void init(FMLInitializationEvent event, BigCapacitorsMod mod) {
		super.init(event, mod);
		
		setupTextureNames();
		
		int id = RenderingRegistry.getNextAvailableRenderId();
		BigCapacitorsMod.capacitorRenderer = new RendererCapacitor(id);
		RenderingRegistry.registerBlockHandler(BigCapacitorsMod.capacitorRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.capacitorIron), (IItemRenderer) BigCapacitorsMod.capacitorRenderer);

		ResourceLocation model = new ResourceLocation(BigCapacitorsMod.modid, "models/Destillery.obj");
		ResourceLocation texture = new ResourceLocation(BigCapacitorsMod.modid, "textures/models/destillery.png");
		TileEntitySpecialRenderer destilleryRenderer = new RendererOBJ(model, texture);
		ClientRegistry.bindTileEntitySpecialRenderer(TileDistillery.class, destilleryRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockDestillery), (IItemRenderer) destilleryRenderer);
		
		model = new ResourceLocation(BigCapacitorsMod.modid, "models/Barrel.obj");
		texture = new ResourceLocation(BigCapacitorsMod.modid, "textures/models/barrel.png");
		TileEntitySpecialRenderer barrelRenderer = new RendererOBJ(model, texture);
		ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.class, barrelRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockBarrel), (IItemRenderer) barrelRenderer);
		
		model = new ResourceLocation(BigCapacitorsMod.modid, "models/fuel_cell.obj");
		texture = new ResourceLocation(BigCapacitorsMod.modid, "textures/models/fuel_cell.png");
		TileEntitySpecialRenderer fuelCellRenderer = new RendererOBJ(model, texture);
		ClientRegistry.bindTileEntitySpecialRenderer(TileFuelCell.class, fuelCellRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockFuelCell), (IItemRenderer) fuelCellRenderer);
		
		model = new ResourceLocation(BigCapacitorsMod.modid, "models/LuxRouter.obj");
		TileEntitySpecialRenderer luxRouterRenderer = new RendererLuxRouter(model, texture);
		ClientRegistry.bindTileEntitySpecialRenderer(TileLuxRouter.class, luxRouterRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockLuxRouter), (IItemRenderer) luxRouterRenderer);
		
		model = new ResourceLocation(BigCapacitorsMod.modid, "models/LuxTransformer.obj");
		TileEntitySpecialRenderer luxTransformerRenderer = new RendererOBJ(model, texture);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyTransciever.class, luxTransformerRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockEnergyTransfer), (IItemRenderer) luxTransformerRenderer);

		GuiOverlayHandler handler = new GuiOverlayHandler();
		handler.registerHandler(Items.itemMultitool, new MultitoolOverlayHandler());
		handler.registerHandler(Items.itemManual, new ManualOverlayHandler());
		handler.registerHandler(Items.itemLuxRouter, new DebugOverlayHandler());
		MinecraftForge.EVENT_BUS.register(handler);
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, new GuiHandler());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event, BigCapacitorsMod mod) {
		super.postInit(event, mod);
		loadKnowledge(mod);
	}

	private void loadKnowledge(BigCapacitorsMod mod) {
		try {
			IResourceManager irm = Minecraft.getMinecraft().getResourceManager();
			SimpleReloadableKnowledgeRegistry reg = new SimpleReloadableKnowledgeRegistry("manual.loc");
			mod.knowledge = reg;
			((SimpleReloadableResourceManager)irm).registerReloadListener(reg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupTextureNames() {
		Items.itemMultitool.setTextureName("big_capacitors:multitool");
		Items.itemManual.setTextureName("big_capacitors:manual");
		Items.itemBucketDestilledWater.setTextureName("minecraft:bucket_water");
		Items.itemBucketEthanol.setTextureName("big_capacitors:bucket_ethanol");
		Items.itemBucketWine.setTextureName("big_capacitors:bucket_wine");
		Items.itemBucketHydrogen.setTextureName("big_capacitors:bucket_gas");
		
		Blocks.capacitorIron.setBlockTextureName("big_capacitors:capacitorIron");
		Blocks.blockDestillery.setBlockTextureName("big_capacitors:destillery");
		Blocks.blockBarrel.setBlockTextureName("big_capacitors:barrel");
		Blocks.blockTomahawk.setBlockTextureName("big_capacitors:tomahawk");
	}
}
