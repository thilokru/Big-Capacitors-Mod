package com.mhfs.capacitors.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.gui.GuiOverlayHandler;
import com.mhfs.capacitors.gui.ManualOverlayHandler;
import com.mhfs.capacitors.gui.MultitoolOverlayHandler;
import com.mhfs.capacitors.handlers.GuiHandler;
import com.mhfs.capacitors.knowledge.SimpleReloadableKnowledgeRegistry;
import com.mhfs.capacitors.render.RendererCapacitor;
import com.mhfs.capacitors.render.RendererDestillery;
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
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod.capacitorIron), (IItemRenderer) BigCapacitorsMod.capacitorRenderer);

		ClientRegistry.bindTileEntitySpecialRenderer(TileDistillery.class, new RendererDestillery());

		GuiOverlayHandler handler = new GuiOverlayHandler();
		handler.registerHandler(mod.itemMultitool, new MultitoolOverlayHandler());
		handler.registerHandler(mod.itemManual, new ManualOverlayHandler());
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
