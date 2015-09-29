package com.mhfs.capacitors;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import com.mhfs.capacitors.blocks.BlockBase;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockDestillery;
import com.mhfs.capacitors.handlers.BucketHandler;
import com.mhfs.capacitors.knowledge.IKnowledgeRegistry;
import com.mhfs.capacitors.proxy.CommonProxy;
import com.mhfs.capacitors.tile.EntityCapacitor;

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

@Mod(modid = BigCapacitorsMod.modid, useMetadata = true, guiFactory = BigCapacitorsMod.guiFactory)
public class BigCapacitorsMod {
	
	public final static String modid = "big_capacitors";
	public final static String guiFactory = "com.mhfs.capacitors.gui.GuiFactory";
	
	@Instance("big_capacitors")
	public static BigCapacitorsMod instance;
	
	@SidedProxy(serverSide = "com.mhfs.capacitors.proxy.CommonProxy", clientSide = "com.mhfs.capacitors.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public Configuration config;
	
	public BucketHandler bucketHandler;
	
	public CreativeTabs creativeTab;
	public BlockCapacitor capacitorIron;
	public BlockDestillery blockDestillery;
	public BlockBase blockWitheriteOre; //BaCO3
	public BlockBase blockRutilOre; //TiO2
	public BlockBase blockCeramic;
	public BlockFluidClassic blockDestilledWater;
	public BlockFluidClassic blockEthanol;
	
	public Item itemWitherite;
	public Item itemRutil;
	public Item itemMultitool;
	public Item itemWire;
	public Item itemHeater;
	public Item itemManual;
	public Item itemBucketDestilledWater;
	public Item itemBucketEthanol;
	
	public Fluid fluidDestilledWater;
	public Fluid fluidEthanol;
	
	public DamageSource damageElectric;
	public IKnowledgeRegistry knowledge;
	
	public static ISimpleBlockRenderingHandler capacitorRenderer;

	public SimpleNetworkWrapper network;
	
	public HashMap<Integer, EntityCapacitor> worldCapacitors;
	
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
