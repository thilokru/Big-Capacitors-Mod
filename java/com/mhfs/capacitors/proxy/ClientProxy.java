package com.mhfs.capacitors.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Items;
import com.mhfs.capacitors.gui.GuiOverlayHandler;
import com.mhfs.capacitors.gui.ManualOverlayHandler;
import com.mhfs.capacitors.gui.MultitoolOverlayHandler;
import com.mhfs.capacitors.handlers.GuiHandler;
import com.mhfs.capacitors.knowledge.SimpleReloadableKnowledgeRegistry;
import com.mhfs.capacitors.render.ItemRenderHelper;
import com.mhfs.capacitors.render.RendererLux;
import com.mhfs.capacitors.render.RendererTileMultiblockModel;
import com.mhfs.capacitors.tile.TileCrusher;
import com.mhfs.capacitors.tile.TileMultiblockRender;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event, BigCapacitorsMod mod) {
		super.preInit(event, mod);
		OBJLoader.INSTANCE.addDomain(BigCapacitorsMod.modid);
		ItemRenderHelper.setupItemModels(mod);
	}

	@Override
	public void init(FMLInitializationEvent event, BigCapacitorsMod mod) {
		super.init(event, mod);
		
		RendererLux<TileLuxRouter> rendererRouter = new RendererLux<TileLuxRouter>();
		ClientRegistry.bindTileEntitySpecialRenderer(TileLuxRouter.class, rendererRouter);
		
		RendererLux<TileEnergyTransciever> rendererTransciever = new RendererLux<TileEnergyTransciever>();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyTransciever.class, rendererTransciever);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrusher.class, new AnimationTESR<TileCrusher>());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileMultiblockRender.class, new RendererTileMultiblockModel());

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
			((SimpleReloadableResourceManager) irm).registerReloadListener(reg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
