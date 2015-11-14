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
import com.mhfs.capacitors.gui.GuiOverlayHandler;
import com.mhfs.capacitors.gui.ManualOverlayHandler;
import com.mhfs.capacitors.gui.MultitoolOverlayHandler;
import com.mhfs.capacitors.handlers.GuiHandler;
import com.mhfs.capacitors.knowledge.SimpleReloadableKnowledgeRegistry;
import com.mhfs.capacitors.render.RendererCapacitor;
import com.mhfs.capacitors.render.RendererOBJ;
import com.mhfs.capacitors.tile.TileBarrel;
import com.mhfs.capacitors.tile.destillery.TileDistillery;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void init(FMLInitializationEvent event, BigCapacitorsMod mod) {
		super.init(event, mod);
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

		GuiOverlayHandler handler = new GuiOverlayHandler();
		handler.registerHandler(Items.itemMultitool, new MultitoolOverlayHandler());
		handler.registerHandler(Items.itemManual, new ManualOverlayHandler());
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
}
