package com.mhfs.capacitors.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.blocks.BlockBase;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockDestillery;
import com.mhfs.capacitors.blocks.BlockFluidDestilledWater;
import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.handlers.EventHandler;
import com.mhfs.capacitors.items.ItemCustomBuckets;
import com.mhfs.capacitors.items.ItemManual;
import com.mhfs.capacitors.items.ItemMultitool;
import com.mhfs.capacitors.misc.Lo;
import com.mhfs.capacitors.network.ConfigUpdateMessage;
import com.mhfs.capacitors.network.WallUpdateMessage;
import com.mhfs.capacitors.oregen.OreGen;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.destillery.DestilleryRecipeRegistry;
import com.mhfs.capacitors.tile.destillery.TileDistillery;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
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
				return Item.getItemFromBlock(mod.capacitorIron);
			}
		};
	}
	
	private void setupFluids(BigCapacitorsMod mod) {
		mod.fluidDestilledWater = new Fluid("destilledWater");
		mod.fluidDestilledWater.setDensity(1000);
		mod.fluidDestilledWater.setGaseous(false);
		mod.fluidDestilledWater.setLuminosity(0);
		mod.fluidDestilledWater.setViscosity(1000);
		FluidRegistry.registerFluid(mod.fluidDestilledWater);
		
		mod.fluidEthanol = new Fluid("ethanol");
		mod.fluidEthanol.setDensity(789);
		mod.fluidEthanol.setGaseous(false);
		mod.fluidEthanol.setLuminosity(0);
		mod.fluidEthanol.setViscosity(1190);
		FluidRegistry.registerFluid(mod.fluidEthanol);
	}

	private void setupTextureNames(BigCapacitorsMod mod) {
		mod.itemMultitool.setTextureName("big_capacitors:multitool");
		mod.itemWire.setTextureName("big_capacitors:wire");
		mod.itemHeater.setTextureName("big_capacitors:heater");
		mod.capacitorIron.setBlockTextureName("big_capacitors:capacitorIron");
		mod.blockRutilOre.setBlockTextureName("big_capacitors:oreRutil");
		mod.blockWitheriteOre.setBlockTextureName("big_capacitors:oreWitherite");
		mod.blockCeramic.setBlockTextureName("big_capacitors:ceramic");
		mod.blockDestillery.setBlockTextureName("big_capacitors:destillery");
		mod.itemManual.setTextureName("big_capacitors:manual");
		mod.itemWitherite.setTextureName("big_capacitors:dustWitherite");
		mod.itemRutil.setTextureName("big_capacitors:dustRutil");
		mod.itemBucketDestilledWater.setTextureName("minecraft:bucket_water");
		mod.itemBucketEthanol.setTextureName("minecraft:bucket_water");
	}

	private void setupRecipies(BigCapacitorsMod mod) {
		ItemStack capacitorStack = new ItemStack(mod.capacitorIron, 4);
		ItemStack obsidianStack = new ItemStack(Blocks.obsidian);
		ItemStack ironBlockStack = new ItemStack(Blocks.iron_block);
		GameRegistry.addShapedRecipe(capacitorStack, "OI", 'O', obsidianStack, 'I', ironBlockStack);
		
		ItemStack manualStack = new ItemStack(mod.itemManual, 1);
		ItemStack bookStack = new ItemStack(Items.book);
		ItemStack ironIngotStack = new ItemStack(Items.iron_ingot);
		GameRegistry.addShapelessRecipe(manualStack, bookStack, ironIngotStack);
			
		GameRegistry.addRecipe(new ShapedOreRecipe(mod.itemWire, true, " S ", "CSC", " S ", 'S', Items.stick, 'C', "ingotCopper"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(mod.itemMultitool, true, "ICC", "RCC", "IW ", 'I', "ingotIron", 'C', "ingotCopper", 'R', "blockRedstone", 'W', mod.itemWire));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(mod.itemHeater, true, "CWC", "CCC", 'W', Item.getItemFromBlock(Blocks.wool), 'C', mod.itemWire));
		
		Item destilleryItem = Item.getItemFromBlock(mod.blockDestillery);
		GameRegistry.addRecipe(new ShapedOreRecipe(destilleryItem, true, "II ", "B B", "H  ", 'I', "ingotIron", 'B', Items.bucket, 'H', mod.itemHeater));
		
		PulverizerManager.addOreNameToDustRecipe(80, "oreTitandioxid", new ItemStack(mod.itemRutil, 2), null, 0);
		PulverizerManager.addOreNameToDustRecipe(80, "oreBariumCarbonate", new ItemStack(mod.itemWitherite, 2), null, 0);
		
		SmelterManager.addAlloyRecipe(80, "dustTitandioxid", 1, "dustBariumCarbonate", 1, new ItemStack(mod.blockCeramic, 1));
		
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(FluidRegistry.WATER, 1), new FluidStack(mod.fluidDestilledWater, 1), 10);
	}

	private void setupConfig(FMLPreInitializationEvent event, BigCapacitorsMod mod) {
		mod.config = new Configuration(event.getSuggestedConfigurationFile());
		mod.config.load();
	}

	private void setupBlocks(BigCapacitorsMod mod){
		mod.capacitorIron = new BlockCapacitor(Material.iron, 0);
		mod.capacitorIron.setBlockName("capacitorIron");
		mod.capacitorIron.setCreativeTab(mod.creativeTab);
		mod.capacitorIron.setHardness(1.5F);
		mod.capacitorIron.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(mod.capacitorIron, "capacitorIron");
		
		mod.blockDestillery = new BlockDestillery(Material.rock);
		mod.blockDestillery.setBlockName("blockDestillery");
		mod.blockDestillery.setCreativeTab(mod.creativeTab);
		mod.blockDestillery.setHardness(1.5F);
		mod.blockDestillery.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(mod.blockDestillery, "blockDestillery");
		
		mod.blockCeramic = new BlockBase(Material.glass);
		mod.blockCeramic.setBlockName("blockCeramic");
		mod.blockCeramic.setCreativeTab(mod.creativeTab);
		mod.blockCeramic.setChapter("Ores");
		mod.blockCeramic.setHardness(0.5F);
		mod.blockCeramic.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(mod.blockCeramic, "blockCeramic");
		
		mod.blockRutilOre = new BlockBase(Material.rock);
		mod.blockRutilOre.setBlockName("oreRutil");
		mod.blockRutilOre.setCreativeTab(mod.creativeTab);
		mod.blockRutilOre.setChapter("Ores");
		mod.blockRutilOre.setHardness(1F);
		mod.blockRutilOre.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(mod.blockRutilOre, "oreRutil");
		OreDictionary.registerOre("oreTitandioxid", mod.blockRutilOre);
		
		mod.blockWitheriteOre = new BlockBase(Material.rock);
		mod.blockWitheriteOre.setBlockName("oreWitherite");
		mod.blockWitheriteOre.setCreativeTab(mod.creativeTab);
		mod.blockWitheriteOre.setChapter("Ores");
		mod.blockWitheriteOre.setHardness(1F);
		mod.blockWitheriteOre.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(mod.blockWitheriteOre, "oreWitherite");
		OreDictionary.registerOre("oreBariumCarbonate", mod.blockWitheriteOre);
		
		mod.blockDestilledWater = new BlockFluidDestilledWater(mod.fluidDestilledWater, Material.water);
		mod.blockDestilledWater.setBlockName("blockDestilledWater");
		GameRegistry.registerBlock(mod.blockDestilledWater, "blockDestilledWater");
		mod.itemBucketDestilledWater = createBucket(mod.fluidDestilledWater, mod.blockDestilledWater, "bucketDestilledWater");
		
		mod.blockEthanol = new BlockFluidDestilledWater(mod.fluidEthanol, Material.water);
		mod.blockEthanol.setBlockName("blockEthanol");
		GameRegistry.registerBlock(mod.blockEthanol, "blockEthanol");
		mod.itemBucketEthanol = createBucket(mod.fluidEthanol, mod.blockEthanol, "bucketEthanol");
	}
	
	private Item createBucket(Fluid fluid, Block fluidBlock, String unloc){
		Item bucket = new ItemCustomBuckets(fluidBlock);
		bucket.setUnlocalizedName(unloc).setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucket, unloc);
		FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.bucket));
		BucketHandler.FLUID_BLOCK_TO_BUCKET.put(fluidBlock, bucket);
		return bucket;
	}
	
	private void setupItems(BigCapacitorsMod mod){
		mod.itemMultitool = new ItemMultitool().setUnlocalizedName("multitool").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(mod.itemMultitool, "multitool");
		
		mod.itemManual = new ItemManual().setUnlocalizedName("manual").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(mod.itemManual, "manual");
		
		mod.itemWire = new Item().setUnlocalizedName("wire").setCreativeTab(mod.creativeTab);
		GameRegistry.registerItem(mod.itemWire, "wire");
		
		mod.itemHeater = new Item().setUnlocalizedName("heater").setCreativeTab(mod.creativeTab);
		GameRegistry.registerItem(mod.itemHeater, "heater");
		
		mod.itemRutil = new Item().setUnlocalizedName("dustRutil").setCreativeTab(mod.creativeTab);
		GameRegistry.registerItem(mod.itemRutil, "dustRutil");
		OreDictionary.registerOre("dustTitandioxid", mod.itemRutil);
		
		mod.itemWitherite = new Item().setUnlocalizedName("dustWitherite").setCreativeTab(mod.creativeTab);
		GameRegistry.registerItem(mod.itemWitherite, "dustWitherite");
		OreDictionary.registerOre("dustBariumCarbonate", mod.itemWitherite);
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
		setupFluids(mod);
		setupBlocks(mod);
		setupItems(mod);
		setupTextureNames(mod);
		
		mod.damageElectric = new DamageSource("electric").setDamageBypassesArmor();
		
		GameRegistry.registerTileEntity(TileCapacitor.class, "tileCapacitor");
		GameRegistry.registerTileEntity(TileDistillery.class, "tileDistillery");
		setupRecipies(mod);
		GameRegistry.registerWorldGenerator(new OreGen(), 1000);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		FMLCommonHandler.instance().bus().register(new EventHandler());
	}

	public void postInit(FMLPostInitializationEvent event,
			BigCapacitorsMod mod) {
		mod.capacitorIron.setMetal(Blocks.iron_block);
		Lo.g.info("Loading material properties...");
		loadDielectricities(mod);
		loadVoltages(mod);
		mod.config.save();
	}
	
	public void loadDielectricities(BigCapacitorsMod mod){
		mod.dielectricitiesFromConfig = new HashMap<Block, Double>();
		ConfigCategory cat = mod.config.getCategory("dielectricities");
		List<String> names = new ArrayList<String>();
		names.addAll(cat.getPropertyOrder());
		if(names.size() == 0){
			names.add("minecraft:air");
			names.add("minecraft:water");
			names.add("big_capacitors:blockCeramic");
		}
		for(String blockName:names){
			double die = mod.config.get(cat.getName(), blockName, getDefaultDielec(blockName)).getDouble();
			Block block = Block.getBlockFromName(blockName);
			mod.dielectricitiesFromConfig.put(block, die);
		}
	}
	
	public void loadVoltages(BigCapacitorsMod mod){
		mod.voltagesFromConfig = new HashMap<Block, Double>();
		ConfigCategory cat = mod.config.getCategory("voltages");
		List<String> names = new ArrayList<String>();
		names.addAll(cat.getPropertyOrder());
		if(names.size() == 0){
			names.add("minecraft:air");
			names.add("minecraft:water");
			names.add("big_capacitors:blockDestilledWater");
			names.add("big_capacitors:blockCeramic");
		}
		for(String blockName:names){
			double die = mod.config.get(cat.getName(), blockName, getDefaultVoltage(blockName)).getDouble();
			Block block = Block.getBlockFromName(blockName);
			mod.voltagesFromConfig.put(block, die);
		}
	}

	private double getDefaultDielec(String blockName) {
		if(blockName.equals("minecraft:air")){
			return 1;
		}else if(blockName.equals("minecraft:water") || blockName.equals("big_capacitors:blockDestilledWater")){
			return 81.1;
		}else if(blockName.equals("big_capacitors:blockCeramic")){
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
		}else if(blockName.equals("big_capacitors:blockCeramic")){
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
			Block block = GameRegistry.findBlock(msg.getSender(), blockName);
			double de = tag.getDouble("dielectricity");
			mod.dielectricitiesFromConfig.put(block, de);
			double volt = tag.getDouble("voltage");
			mod.voltagesFromConfig.put(block, volt);
		}
	}

}
