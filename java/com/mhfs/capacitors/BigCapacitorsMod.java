package com.mhfs.capacitors;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.knowledge.IKnowledgeRegistry;
import com.mhfs.capacitors.proxy.CommonProxy;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = BigCapacitorsMod.modid, useMetadata = true, guiFactory = BigCapacitorsMod.guiFactory, version = BigCapacitorsMod.version)
public class BigCapacitorsMod {
	
	public final static String modid = "big_capacitors";
	public final static String guiFactory = "com.mhfs.capacitors.gui.GuiFactory";
	public final static String version = "1.0Alpha";
	
	@Instance("big_capacitors")
	public static BigCapacitorsMod instance;
	
	@SidedProxy(serverSide = "com.mhfs.capacitors.proxy.CommonProxy", clientSide = "com.mhfs.capacitors.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public Configuration config;
	
	public BucketHandler bucketHandler;
	
	public CreativeTabs creativeTab;	
	
	public DamageSource damageElectric;
	public IKnowledgeRegistry knowledge;
	
	public static ISimpleBlockRenderingHandler capacitorRenderer;

	public SimpleNetworkWrapper network;
	
	public HashMap<Block, Double> dielectricitiesFromConfig;
	public HashMap<Block, Double> dielectricities;
	
	/**
	 * UNIT: MV/m
	 */
	public HashMap<Block, Double> voltagesFromConfig;
	public HashMap<Block, Double> voltages;
	
	public final static double energyConstant = 8.85418781762 * Math.pow(10, -12);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.preInit(event, this);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event, this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event, this);
	}
	
	@EventHandler
	public void onIMC(IMCEvent event){
		proxy.onIMC(event, this);
	}

	public static Block getInsulator() {
		return Blocks.obsidian;
	}
}
