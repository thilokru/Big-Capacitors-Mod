package com.mhfs.capacitors.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
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
import com.mhfs.capacitors.tile.TileCrusher;
import com.mhfs.capacitors.tile.TileCrusherController;
import com.mhfs.capacitors.tile.TileStirlingEngine;
import com.mhfs.capacitors.tile.TileTokamak;
import com.mhfs.capacitors.tile.TileTower;
import com.mhfs.capacitors.tile.destillery.DestilleryRecipeRegistry;
import com.mhfs.capacitors.tile.destillery.TileBoiler;
import com.mhfs.capacitors.tile.fuelcell.TileFuelCell;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;

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
		
		MinecraftForge.EVENT_BUS.register(mod.bucketHandler = new BucketHandler());
		Lo.g.info("Setting up blocks, fluids and items...");
		Fluids.setup(mod.creativeTab);
		Blocks.setup(mod);
		Items.setup(mod);
	}

	private void setupRecipies() {		
		ItemStack capacitorStack = new ItemStack(Blocks.capacitorIron, 4);
		ItemStack obsidianStack = new ItemStack(Blocks.OBSIDIAN);
		ItemStack ironBlockStack = new ItemStack(Blocks.IRON_BLOCK);
		GameRegistry.addShapedRecipe(capacitorStack, "OI", 'O', obsidianStack, 'I', ironBlockStack);
		
		ItemStack manualStack = new ItemStack(Items.itemManual, 1);
		ItemStack bookStack = new ItemStack(Items.BOOK);
		ItemStack ironIngotStack = new ItemStack(Items.IRON_INGOT);
		GameRegistry.addShapelessRecipe(manualStack, bookStack, ironIngotStack);
			
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.itemStackWire, true, " S ", "CSC", " S ", 'S', Items.STICK, 'C', "ingotCopper"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.itemMultitool, true, "ICC", "RCC", "IW ", 'I', "ingotIron", 'C', "ingotCopper", 'R', "blockRedstone", 'W', Items.itemStackWire));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.itemStackHeater, true, "CWC", "CCC", 'W', Item.getItemFromBlock(Blocks.WOOL), 'C',  Items.itemStackWire));
		
		Item barrelItem = Item.getItemFromBlock(Blocks.blockBarrel);
		GameRegistry.addRecipe(new ShapedOreRecipe(barrelItem, true, "WSW", "W W", "WSW", 'S', "slabWood", 'W', "plankWood"));
		
		Item fuelCellItem = Item.getItemFromBlock(Blocks.blockFuelCell);
		GameRegistry.addRecipe(new ShapedOreRecipe(fuelCellItem, true, " I ", "IBI", " I ", 'I', "ingotIron", 'B', Items.BUCKET));

		ItemStack reactorShieldItem = new ItemStack(Blocks.blockMany, 1, 0);
		GameRegistry.addRecipe(new ShapedOreRecipe(reactorShieldItem, true, "IOI", "OGO", "IOI", 'I', "ingotIron", 'O', Blocks.OBSIDIAN, 'G', Blocks.GRAVEL));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.blockMachineController, true, "SWS", "WPW", "SWS", 'S', reactorShieldItem, 'P', Items.itemStackFPGA, 'W', Items.itemStackWire));
		
		ItemStack coilEmptyItem = new ItemStack(Blocks.blockMany, 1, 5);
		GameRegistry.addRecipe(new ShapedOreRecipe(coilEmptyItem, true, "OWO", "OWO", "OWO", 'O', Blocks.OBSIDIAN, 'W', Items.itemStackWire));
		
		//PulverizerManager.addOreNameToDustRecipe(80, "oreTitandioxid", new ItemStack(Items.itemMany, 2, 3), null, 0);
		//PulverizerManager.addOreNameToDustRecipe(80, "oreBariumCarbonate", new ItemStack(Items.itemMany, 2, 4), null, 0);
		
		//SmelterManager.addAlloyRecipe(80, "dustTitandioxid", 1, "dustBariumCarbonate", 1, new ItemStack(Blocks.blockMany, 1, 1));
		
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(FluidRegistry.WATER, 1), new FluidStack(Fluids.fluidDestilledWater, 1), 10);
		DestilleryRecipeRegistry.registerRecipe(new FluidStack(Fluids.fluidWine, 10), new FluidStack(Fluids.fluidEthanol, 1), 8);
		
		//ThermalExpansionHelper.addTransposerFill(80, coilEmptyItem, new ItemStack(Blocks.blockMany, 1, 2), new FluidStack(FluidRegistry.getFluid("cryotheum"), 100), false);
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
		
		VillagerProfession librarian = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:librarian"));
		VillagerCareer informatician = new VillagerCareer(librarian, "informatician");
		try{
			Method[] methods = VillagerCareer.class.getDeclaredMethods();
			Method init = null;
			for (Method method : methods){
				if(method.getName() == "init"){
					init = method;
					break;
				}
			}
			init.setAccessible(true);
			EntityVillager.ITradeList[][] arg = new EntityVillager.ITradeList[][]{{new EntityVillager.ListItemForEmeralds(Items.itemStackFPGA, new PriceInfo(32, 48))}};
			init.invoke(informatician, new Object[]{arg});
		}catch (Exception e){
			e.printStackTrace();
		}
		
		mod.damageElectric = new DamageSource("electric").setDamageBypassesArmor();
		
		Lo.g.info("Loading Multiblocks...");
		ResourceLocation rl = new ResourceLocation("big_capacitors:multiblock/fusion.txt");
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		mod.fusionReactorMulti = new Multiblock(rl, manager);
		
		rl = new ResourceLocation("big_capacitors:multiblock/crusher.txt");
		mod.crusherMulti = new Multiblock(rl, manager);
		
		GameRegistry.registerTileEntity(TileCapacitor.class, "tileCapacitor");
		GameRegistry.registerTileEntity(TileBarrel.class, "tileBarrel");
		GameRegistry.registerTileEntity(TileTokamak.class, "tileTomahawk");
		GameRegistry.registerTileEntity(TileFuelCell.class, "tileFuelCell");
		GameRegistry.registerTileEntity(TileLuxRouter.class, "tileLuxRouter");
		GameRegistry.registerTileEntity(TileEnergyTransciever.class, "tileEnergyTransciever");
		GameRegistry.registerTileEntity(TileBoiler.class, "tileBoiler");
		GameRegistry.registerTileEntity(TileTower.class, "tileDestillationTower");
		GameRegistry.registerTileEntity(TileStirlingEngine.class, "tileStirlingEngine");
		GameRegistry.registerTileEntity(TileCrusher.class, "tileCrusher");
		GameRegistry.registerTileEntity(TileCrusherController.class, "tileCrusherController");
		setupRecipies();
		GameRegistry.registerWorldGenerator(new OreGen(), 1000);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void postInit(FMLPostInitializationEvent event,
			BigCapacitorsMod mod) {
		Blocks.capacitorIron.setMetal(Blocks.IRON_BLOCK);
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
