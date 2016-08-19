package com.mhfs.capacitors;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;

import com.mhfs.api.manual.knowledge.IManual;
import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.misc.Multiblock;
import com.mhfs.capacitors.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = BigCapacitorsMod.modid, useMetadata = true, guiFactory = BigCapacitorsMod.guiFactory, version = BigCapacitorsMod.version)
public class BigCapacitorsMod {
	
	static{
		FluidRegistry.enableUniversalBucket();
	}
	
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
	public IManual knowledge;
	
	public Multiblock fusionReactorMulti;
	public Multiblock crusherMulti;

	public SimpleNetworkWrapper network;
	
	public HashMap<String, Double> dielectricitiesFromConfig;
	public HashMap<String, Double> dielectricities;
	
	/**
	 * UNIT: MV/m
	 */
	public HashMap<String, Double> voltagesFromConfig;
	public HashMap<String, Double> voltages;
	
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
		return Blocks.OBSIDIAN;
	}
}
