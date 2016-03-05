package com.mhfs.capacitors.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.Items;
import com.mhfs.capacitors.blocks.BlockBarrel;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockData;
import com.mhfs.capacitors.blocks.BlockDestillery;
import com.mhfs.capacitors.blocks.BlockEnergyTransfer;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.blocks.BlockLuxRouter;
import com.mhfs.capacitors.blocks.BlockTokamak;
import com.mhfs.capacitors.gui.GuiOverlayHandler;
import com.mhfs.capacitors.gui.ManualOverlayHandler;
import com.mhfs.capacitors.gui.MultitoolOverlayHandler;
import com.mhfs.capacitors.handlers.GuiHandler;
import com.mhfs.capacitors.items.ItemData;
import com.mhfs.capacitors.items.ItemManual;
import com.mhfs.capacitors.items.ItemMultitool;
import com.mhfs.capacitors.knowledge.SimpleReloadableKnowledgeRegistry;
import com.mhfs.capacitors.render.RendererLux;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

	private final static String fluidStateModel = "fluidState";

	@Override
	public void preInit(FMLPreInitializationEvent event, BigCapacitorsMod mod) {
		super.preInit(event, mod);
		// FluidHandling
		OBJLoader.instance.addDomain(BigCapacitorsMod.modid);
		registerFluidBlock(Fluids.blockDestilledWater, "destilledWater");
		registerFluidBlock(Fluids.blockEthanol, "ethanol");
		registerFluidBlock(Fluids.blockHydrogen, "hydrogen");
		registerFluidBlock(Fluids.blockWine, "wine");
	}

	@Override
	public void init(FMLInitializationEvent event, BigCapacitorsMod mod) {
		super.init(event, mod);

		setupModels(event, mod);
		
		RendererLux<TileLuxRouter> rendererRouter = new RendererLux<TileLuxRouter>();
		ClientRegistry.bindTileEntitySpecialRenderer(TileLuxRouter.class, rendererRouter);
		
		RendererLux<TileEnergyTransciever> rendererTransciever = new RendererLux<TileEnergyTransciever>();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyTransciever.class, rendererTransciever);

		GuiOverlayHandler handler = new GuiOverlayHandler();
		handler.registerHandler(Items.itemMultitool, new MultitoolOverlayHandler());
		handler.registerHandler(Items.itemManual, new ManualOverlayHandler());
		MinecraftForge.EVENT_BUS.register(handler);
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, new GuiHandler());
	}

	private void setupModels(FMLInitializationEvent event, BigCapacitorsMod mod) {
		registerMesher(Blocks.capacitorIron, BlockCapacitor.name);
		registerMesher(Blocks.blockDestillery, BlockDestillery.name);
		registerMesher(Blocks.blockBarrel, BlockBarrel.name);
		registerMesher(Blocks.blockFuelCell, BlockFuelCell.name);
		registerMesher(Blocks.blockLuxRouter, BlockLuxRouter.name);
		registerMesher(Blocks.blockEnergyTransfer, BlockEnergyTransfer.name);
		registerMesher(Blocks.blockTokamak, BlockTokamak.name);

		registerMesher(Items.itemBucketDestilledWater, "bucketDestilledWater");
		registerMesher(Items.itemBucketEthanol, "bucketEthanol");
		registerMesher(Items.itemBucketHydrogen, "bucketHydrogen");
		registerMesher(Items.itemBucketWine, "bucketWine");
		registerMesher(Items.itemManual, ItemManual.name);
		registerMesher(Items.itemMultitool, ItemMultitool.name);
		
		registerSubItems();
		registerSubBlocks();
	}
	
	private void registerSubBlocks(){
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		BlockData[] itemData = Blocks.blockMany.getData();
		ResourceLocation[] locs = new ResourceLocation[itemData.length];
		Item item = Item.getItemFromBlock(Blocks.blockMany);
		for(int i = 0; i < itemData.length; i++){
			locs[i] = new ResourceLocation(BigCapacitorsMod.modid, itemData[i].getName());
			mesher.register(item, i, new ModelResourceLocation(locs[i], "inventory"));
		}
		ModelBakery.registerItemVariants(item, locs);
	}
	
	private void registerSubItems(){
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		ItemData[] itemData = Items.itemMany.getData();
		ResourceLocation[] locs = new ResourceLocation[itemData.length];
		for(int i = 0; i < itemData.length; i++){
			locs[i] = new ResourceLocation(BigCapacitorsMod.modid, itemData[i].getName());
			mesher.register(Items.itemMany, i, new ModelResourceLocation(locs[i], "inventory"));
		}
		ModelBakery.registerItemVariants(Items.itemMany, locs);
	}

	private void registerFluidBlock(BlockFluidClassic block, String identifier) {
		Item item = Item.getItemFromBlock(block);
		final ModelResourceLocation mrl = new ModelResourceLocation(new ResourceLocation(BigCapacitorsMod.modid, fluidStateModel), identifier);
		ModelBakery.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return mrl;
			}
		});
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return mrl;
			}
		});
	}

	private void registerMesher(Block block, String name) {
		registerMesher(Item.getItemFromBlock(block), name);
	}

	private void registerMesher(Item item, String name) {
		registerMesher(item, 0, name);
	}

	private void registerMesher(Item item, int meta, String name) {
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		ResourceLocation loc = new ResourceLocation(BigCapacitorsMod.modid, name);
		mesher.register(item, meta, new ModelResourceLocation(loc, "inventory"));
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
