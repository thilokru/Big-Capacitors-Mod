package com.mhfs.capacitors.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.Items;
import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.handlers.EventHandler;
import com.mhfs.capacitors.misc.Lo;
import com.mhfs.capacitors.misc.Multiblock;
import com.mhfs.capacitors.network.ConfigUpdateMessage;
import com.mhfs.capacitors.network.WallUpdateMessage;
import com.mhfs.capacitors.oregen.OreGen;
import com.mhfs.capacitors.tile.TileBarrel;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.TileFuelCell;
import com.mhfs.capacitors.tile.TileTomahawk;
import com.mhfs.capacitors.tile.destillery.DestilleryRecipeRegistry;
import com.mhfs.capacitors.tile.destillery.TileDistillery;
import com.mhfs.capacitors.tile.lux.TileDrain;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;
import com.mhfs.capacitors.tile.lux.TileSource;
import com.mhfs.capacitors.village.TradeHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event,
			final BigCapacitorsMod mod) {
		Lo.g.info("Loading config...");
		setupConfig(event, mod);	
		Lo.g.info("Setting up packets...");
		setupNetwork(mod);
		mod.creativeTab = new CreativeTabs("BigCapacitors"){
			@Override
			public Item getTabIconItem() {
				return Item.getItemFromBlock(Blocks.capacitorIron);
			}
		};
	}

	private void setupRecipies() {		
		ItemStack capacitorStack = new ItemStack(Blocks.capacitorIron, 4);
		ItemStack obsidianStack = new ItemStack(Blocks.obsidian);
		ItemStack ironBlockStack = new ItemStack(Blocks.iron_block);
		GameRegistry.addShapedRecipe(capacitorStack, "OI", 'O', obsidianStack, 'I', ironBlockStack);
		
		ItemStack manualStack = new ItemStack(Items.itemManual, 1);
		ItemStack bookStack = new ItemStack(Items.book);
		ItemStack ironIngotStack = new ItemStack(Items.iron_ingot);
		GameRegistry.addShapelessRecipe(manualStack, bookStack, ironIngotStack);
			
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.itemMany, 1, 0), true, " S ", "CSC", " S ", 'S', Items.stick, 'C', "ingotCopper"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.itemMultitool, true, "ICC", "RCC", "IW ", 'I', "ingotIron", 'C', "ingotCopper", 'R', "blockRedstone", 'W', new ItemStack(Items.itemMany, 1, 0)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(Items.itemMany, 1, 1), true, "CWC", "CCC", 'W', Item.getItemFromBlock(Blocks.wool), 'C',  new ItemStack(Items.itemMany, 1, 0)));
		
		Item destilleryItem = Item.getItemFromBlock(Blocks.blockDestillery);
		GameRegistry.addRecipe(new ShapedOreRecipe(destilleryItem, true, "II ", "B B", "H  ", 'I', "ingotIron", 'B', Items.bucket, 'H',  new ItemStack(Items.itemMany, 1, 1)));
		
		Item barrelItem = Item.getItemFromBlock(Blocks.blockBarrel);
		GameRegistry.addRecipe(new ShapedOreRecipe(barrelItem, true, "WSW", "W W", "WSW", 'S', "slabWood", 'W', "plankWood"));
		
		Item fuelCellItem = Item.getItemFromBlock(Blocks.blockFuelCell);
		GameRegistry.addRecipe(new ShapedOreRecipe(fuelCellItem, true, " I ", "IBI", " I ", 'I', "ingotIron", 'B', Items.bucket));

		ItemStack reactorShieldItem = new ItemStack(Blocks.blockMany, 1, 0);
		GameRegistry.addRecipe(new ShapedOreRecipe(reactorShieldItem, true, "IOI", "OGO", "IOI", 'I', "ingotIron", 'O', Blocks.obsidian, 'G', Blocks.gravel));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.blockTomahawk, true, "SWS", "WPW", "SWS", 'S', reactorShieldItem, 'P', new ItemStack(Items.itemMany, 1, 4), 'W', new ItemStack(Items.itemMany, 1, 0)));
		
		ItemStack coilEmptyItem = new ItemStack(Blocks.blockMany, 1, 5);
		GameRegistry.addRecipe(new ShapedOreRecipe(coilEmptyItem, true, "OWO", "OWO", "OWO", 'O', Blocks.obsidian, 'W', new ItemStack(Items.itemMany, 1, 0)));
		
		PulverizerManager.addOreNameToDustRecipe(80, "oreTitandioxid", new ItemStack(Items.itemMany, 2, 3), null, 0);
		PulverizerManager.addOreNameToDustRecipe(80, "oreBariumCarbonate", new ItemStack(Items.itemMany, 2, 4), null, 0);
		
		SmelterManager.addAlloyRecipe(80, "dustTitandioxid", 1, "dustBariumCarbonate", 1, new ItemStack(Blocks.blockMany, 1, 1));
		
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(FluidRegistry.WATER, 1), new FluidStack(Fluids.fluidDestilledWater, 1), 10);
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(Fluids.fluidWine, 10), new FluidStack(Fluids.fluidEthanol, 1), 8);
		
		ThermalExpansionHelper.addTransposerFill(80, coilEmptyItem, new ItemStack(Blocks.blockMany, 1, 2), new FluidStack(FluidRegistry.getFluid("cryotheum"), 100), false);
	}

	private void setupConfig(FMLPreInitializationEvent event, BigCapacitorsMod mod) {
		mod.config = new Configuration(event.getSuggestedConfigurationFile());
		mod.config.load();
	}	
	
	private void setupNetwork(BigCapacitorsMod mod){
		mod.network = NetworkRegistry.INSTANCE.newSimpleChannel("energy_update");
		mod.network.registerMessage(WallUpdateMessage.class, WallUpdateMessage.class, 1, Side.CLIENT);
		mod.network.registerMessage(ConfigUpdateMessage.class, ConfigUpdateMessage.class, 2, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event,
			BigCapacitorsMod mod) {
		MinecraftForge.EVENT_BUS.register(mod.bucketHandler = new BucketHandler());
		Lo.g.info("Setting up ingame-stuff...");
		Fluids.setup();
		Blocks.setup(mod);
		Items.setup(mod);
		VillagerRegistry.instance().registerVillageTradeHandler(2, new TradeHandler());
		
		System.out.println(FluidRegistry.getRegisteredFluids());
		
		mod.damageElectric = new DamageSource("electric").setDamageBypassesArmor();
		
		Lo.g.info("Loading Tomahawk Multiblock...");
		ResourceLocation rl = new ResourceLocation("big_capacitors:multiblock/fusion.txt");
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		mod.fusionReactorMulti = new Multiblock(rl, manager);
		
		GameRegistry.registerTileEntity(TileCapacitor.class, "tileCapacitor");
		GameRegistry.registerTileEntity(TileDistillery.class, "tileDistillery");
		GameRegistry.registerTileEntity(TileBarrel.class, "tileBarrel");
		GameRegistry.registerTileEntity(TileTomahawk.class, "tileTomahawk");
		GameRegistry.registerTileEntity(TileFuelCell.class, "tileFuelCell");
		GameRegistry.registerTileEntity(TileLuxRouter.class, "tileLuxRouter");
		GameRegistry.registerTileEntity(TileDrain.class, "tileLuxDrain");
		GameRegistry.registerTileEntity(TileSource.class, "tileLuxSource");
		setupRecipies();
		GameRegistry.registerWorldGenerator(new OreGen(), 1000);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		FMLCommonHandler.instance().bus().register(new EventHandler());
	}

	public void postInit(FMLPostInitializationEvent event,
			BigCapacitorsMod mod) {
		Blocks.capacitorIron.setMetal(Blocks.iron_block);
		Lo.g.info("Loading material properties...");
		loadDielectricities(mod);
		loadVoltages(mod);
		mod.config.save();
	}
	
	public void loadDielectricities(BigCapacitorsMod mod){
		mod.dielectricitiesFromConfig = new HashMap<String, Double>();
		ConfigCategory cat = mod.config.getCategory("dielectricities");
		List<String> names = new ArrayList<String>();
		names.addAll(cat.getPropertyOrder());
		if(names.size() == 0){
			names.add("minecraft:air");
			names.add("minecraft:water");
			names.add("big_capacitors:blockDestilledWater");
			names.add("big_capacitors:blockMany 1");
			names.add("big_capacitors:blockEthanol");
		}
		for(String blockName:names){
			double die = mod.config.get(cat.getName(), blockName, getDefaultDielec(blockName)).getDouble();
			mod.dielectricitiesFromConfig.put(blockName, die);
		}
	}
	
	public void loadVoltages(BigCapacitorsMod mod){
		mod.voltagesFromConfig = new HashMap<String, Double>();
		ConfigCategory cat = mod.config.getCategory("voltages");
		List<String> names = new ArrayList<String>();
		names.addAll(cat.getPropertyOrder());
		if(names.size() == 0){
			names.add("minecraft:air");
			names.add("minecraft:water");
			names.add("big_capacitors:blockDestilledWater");
			names.add("big_capacitors:blockMany 1");
			names.add("big_capacitors:blockEthanol");
		}
		for(String blockName:names){
			double die = mod.config.get(cat.getName(), blockName, getDefaultVoltage(blockName)).getDouble();
			mod.voltagesFromConfig.put(blockName, die);
		}
	}

	private double getDefaultDielec(String blockName) {
		if(blockName.equals("minecraft:air")){
			return 1;
		}else if(blockName.equals("minecraft:water") || blockName.equals("big_capacitors:blockDestilledWater")){
			return 81.1;
		}else if(blockName.equals("big_capacitors:blockMany 1")){
			return 1000;
		}else if(blockName.equals("big_capacitors:blockEthanol")){
			return 25.8;
		}
		return 1;
	}
	
	private double getDefaultVoltage(String blockName) {
		if(blockName.equals("minecraft:air")){
			return 3.3;
		}else if(blockName.equals("big_capacitors:blockDestilledWater")){
			return 70;
		}else if(blockName.equals("big_capacitors:blockMany 1")){
			return 100;
		}else if(blockName.equals("minecraft:water")){
			return 0;
		}else if(blockName.equals("big_capacitors:blockEthanol")){
			return 0;
		}
		return 1;
	}

	public void onIMC(IMCEvent event, BigCapacitorsMod mod) {
		IMCMessage[] msgs = event.getMessages().toArray(new IMCMessage[0]);
		for(IMCMessage msg:msgs){
			NBTTagCompound tag = msg.getNBTValue();
			String blockName = tag.getString("block");
			double de = tag.getDouble("dielectricity");
			mod.dielectricitiesFromConfig.put(blockName, de);
			double volt = tag.getDouble("voltage");
			mod.voltagesFromConfig.put(blockName, volt);
		}
	}

}
