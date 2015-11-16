package com.mhfs.capacitors.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.Items;
import com.mhfs.capacitors.blocks.BlockBarrel;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockData;
import com.mhfs.capacitors.blocks.BlockDestillery;
import com.mhfs.capacitors.blocks.BlockFluidBase;
import com.mhfs.capacitors.blocks.BlockMany;
import com.mhfs.capacitors.blocks.BlockTomahawk;
import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.handlers.EventHandler;
import com.mhfs.capacitors.items.ItemCustomBuckets;
import com.mhfs.capacitors.items.ItemData;
import com.mhfs.capacitors.items.ItemManual;
import com.mhfs.capacitors.items.ItemMany;
import com.mhfs.capacitors.items.ItemBlockMany;
import com.mhfs.capacitors.items.ItemMultitool;
import com.mhfs.capacitors.misc.Lo;
import com.mhfs.capacitors.misc.Multiblock;
import com.mhfs.capacitors.network.ConfigUpdateMessage;
import com.mhfs.capacitors.network.WallUpdateMessage;
import com.mhfs.capacitors.oregen.OreGen;
import com.mhfs.capacitors.tile.TileBarrel;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.TileTomahawk;
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
				return Item.getItemFromBlock(Blocks.capacitorIron);
			}
		};
	}
	
	private void setupFluids() {
		Fluids.fluidDestilledWater = new Fluid("destilledWater");
		Fluids.fluidDestilledWater.setDensity(1000);
		Fluids.fluidDestilledWater.setGaseous(false);
		Fluids.fluidDestilledWater.setViscosity(1000);
		FluidRegistry.registerFluid(Fluids.fluidDestilledWater);
		
		Fluids.fluidEthanol = new Fluid("ethanol");
		Fluids.fluidEthanol.setDensity(789);
		Fluids.fluidEthanol.setGaseous(false);
		Fluids.fluidEthanol.setViscosity(1190);
		FluidRegistry.registerFluid(Fluids.fluidEthanol);
		
		Fluids.fluidWine = new Fluid("wine");
		Fluids.fluidWine.setDensity(900);
		Fluids.fluidWine.setGaseous(false);
		Fluids.fluidWine.setLuminosity(0);
		Fluids.fluidWine.setViscosity(1050);
		FluidRegistry.registerFluid(Fluids.fluidWine);
		
		Fluids.gasHydrogen = new Fluid("hydrogen");
		Fluids.gasHydrogen.setDensity(1);
		Fluids.gasHydrogen.setGaseous(true);
		Fluids.gasHydrogen.setViscosity(1);
		FluidRegistry.registerFluid(Fluids.gasHydrogen);
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
		
		PulverizerManager.addOreNameToDustRecipe(80, "oreTitandioxid", new ItemStack(Items.itemMany, 2, 3), null, 0);
		PulverizerManager.addOreNameToDustRecipe(80, "oreBariumCarbonate", new ItemStack(Items.itemMany, 2, 4), null, 0);
		
		SmelterManager.addAlloyRecipe(80, "dustTitandioxid", 1, "dustBariumCarbonate", 1, new ItemStack(Blocks.blockMany, 1, 1));
		
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(FluidRegistry.WATER, 1), new FluidStack(Fluids.fluidDestilledWater, 1), 10);
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(Fluids.fluidWine, 10), new FluidStack(Fluids.fluidEthanol, 1), 8);
	}

	private void setupConfig(FMLPreInitializationEvent event, BigCapacitorsMod mod) {
		mod.config = new Configuration(event.getSuggestedConfigurationFile());
		mod.config.load();
	}

	private void setupBlocks(BigCapacitorsMod mod){
		Blocks.capacitorIron = new BlockCapacitor(Material.iron, 0);
		Blocks.capacitorIron.setBlockName("capacitorIron");
		Blocks.capacitorIron.setCreativeTab(mod.creativeTab);
		Blocks.capacitorIron.setHardness(1.5F);
		Blocks.capacitorIron.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(Blocks.capacitorIron, "capacitorIron");
		
		Blocks.blockDestillery = new BlockDestillery(Material.rock);
		Blocks.blockDestillery.setBlockName("blockDestillery");
		Blocks.blockDestillery.setCreativeTab(mod.creativeTab);
		Blocks.blockDestillery.setHardness(1.5F);
		Blocks.blockDestillery.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(Blocks.blockDestillery, "blockDestillery");
		
		Blocks.blockBarrel = new BlockBarrel(Material.wood);
		Blocks.blockBarrel.setBlockName("blockBarrel");
		Blocks.blockBarrel.setCreativeTab(mod.creativeTab);
		Blocks.blockBarrel.setHardness(1F);
		Blocks.blockBarrel.setHarvestLevel("axe", 2);
		GameRegistry.registerBlock(Blocks.blockBarrel, "blockBarrel");
		
		Blocks.blockTomahawk = new BlockTomahawk(Material.rock);
		Blocks.blockTomahawk.setBlockName("blockTomahawk");
		Blocks.blockTomahawk.setCreativeTab(mod.creativeTab);
		Blocks.blockTomahawk.setHardness(0.5F);
		Blocks.blockTomahawk.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(Blocks.blockTomahawk, "blockTomahawk");
		
		ArrayList<BlockData> blockData = new ArrayList<BlockData>();
		blockData.add(new BlockData("reactorShield", "big_capacitors:reactorShield", "pickaxe", 2, 3.0F));//0
		
		blockData.add(new BlockData("blockCeramic", "big_capacitors:ceramic", "pickaxe", 2, 0.5F));//1
		
		BlockData coil = new BlockData("coil", "big_capacitors:coil", "pickaxe", 2, 3.0F);//2
		coil.setSpecialTexture(ForgeDirection.UP, "big_capacitors:coilTop");
		coil.setSpecialTexture(ForgeDirection.DOWN, "big_capacitors:coilTop");
		blockData.add(coil);
		
		blockData.add(new BlockData("oreRutil", "big_capacitors:oreRutil", "pickaxe", 2, 1F));//3
		
		blockData.add(new BlockData("oreWitherite", "big_capacitors:oreWitherite", "pickaxe", 2, 1F));//4
		
		Blocks.blockMany = new BlockMany(blockData.toArray(new BlockData[0]));
		Blocks.blockMany.setCreativeTab(mod.creativeTab);
		GameRegistry.registerBlock(Blocks.blockMany, ItemBlockMany.class, "blockMany");
		Blocks.blockMany.injectSubStacks();
		
		OreDictionary.registerOre("oreTitandioxid", new ItemStack(Blocks.blockMany, 1, 3));
		OreDictionary.registerOre("oreBariumCarbonate", new ItemStack(Blocks.blockMany, 1, 4));
		
		Fluids.blockDestilledWater = new BlockFluidBase(Fluids.fluidDestilledWater, Material.water, "water_still", "water_flow");
		Fluids.blockDestilledWater.setCreativeTab(mod.creativeTab);
		Fluids.blockDestilledWater.setBlockName("blockDestilledWater");
		GameRegistry.registerBlock(Fluids.blockDestilledWater, "blockDestilledWater");
		Items.itemBucketDestilledWater = createBucket(Fluids.fluidDestilledWater, Fluids.blockDestilledWater, "bucketDestilledWater");
		
		Fluids.blockEthanol = new BlockFluidBase(Fluids.fluidEthanol, Material.water, "big_capacitors:ethanol_still", "big_capacitors:ethanol_flow");
		Fluids.blockEthanol.setCreativeTab(mod.creativeTab);
		Fluids.blockEthanol.setBlockName("blockEthanol");
		GameRegistry.registerBlock(Fluids.blockEthanol, "blockEthanol");
		Items.itemBucketEthanol = createBucket(Fluids.fluidEthanol, Fluids.blockEthanol, "bucketEthanol");
		
		Fluids.blockWine = new BlockFluidBase(Fluids.fluidWine, Material.water, "big_capacitors:wine_still", "big_capacitors:wine_flow");
		Fluids.blockWine.setCreativeTab(mod.creativeTab);
		Fluids.blockWine.setBlockName("blockWine");
		GameRegistry.registerBlock(Fluids.blockWine, "blockWine");
		Items.itemBucketWine = createBucket(Fluids.fluidWine, Fluids.blockWine, "bucketWine");
		
		Fluids.blockHydrogen = new BlockFluidBase(Fluids.gasHydrogen, Material.air, "big_capacitors:blank", "big_capacitors:blank");
		Fluids.blockHydrogen.setCreativeTab(mod.creativeTab);
		Fluids.blockHydrogen.setBlockName("blockHydrogen");
		GameRegistry.registerBlock(Fluids.blockHydrogen, "blockHydrogen");
		Items.itemBucketHydrogen = createBucket(Fluids.gasHydrogen, Fluids.blockHydrogen, "bucketHydrogen");
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
		Items.itemMultitool = new ItemMultitool().setUnlocalizedName("multitool").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(Items.itemMultitool, "multitool");
		
		Items.itemManual = new ItemManual().setUnlocalizedName("manual").setCreativeTab(mod.creativeTab).setMaxStackSize(1);
		GameRegistry.registerItem(Items.itemManual, "manual");
		
		List<ItemData> data = new ArrayList<ItemData>();
		
		data.add(new ItemData("wire", "big_capacitors:wire", false));
		data.add(new ItemData("heater", "big_capacitors:heater", false));
		data.add(new ItemData("dustRutil", "big_capacitors:dustRutil", false));//2, TiO2
		data.add(new ItemData("dustWitherite", "big_capacitors:dustWitherite", false));//3, BaCO3
		
		Items.itemMany = new ItemMany(data.toArray(new ItemData[0]));
		Items.itemMany.setCreativeTab(mod.creativeTab);
		Items.itemMany.injectSubItems();
		GameRegistry.registerItem(Items.itemMany, "itemMany");
		
		OreDictionary.registerOre("dustTitandioxid", new ItemStack(Items.itemMany, 1, 2));
		OreDictionary.registerOre("dustBariumCarbonate",  new ItemStack(Items.itemMany, 1, 3));
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
		setupFluids();
		setupBlocks(mod);
		setupItems(mod);
		setupTextureNames();
		
		mod.damageElectric = new DamageSource("electric").setDamageBypassesArmor();
		
		Lo.g.info("Loading Tomahawk Multiblock...");
		ResourceLocation rl = new ResourceLocation("big_capacitors:multiblock/fusion.txt");
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		mod.fusionReactorMulti = new Multiblock(rl, manager);
		
		GameRegistry.registerTileEntity(TileCapacitor.class, "tileCapacitor");
		GameRegistry.registerTileEntity(TileDistillery.class, "tileDistillery");
		GameRegistry.registerTileEntity(TileBarrel.class, "tileBarrel");
		GameRegistry.registerTileEntity(TileTomahawk.class, "tileTomahawk");
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
