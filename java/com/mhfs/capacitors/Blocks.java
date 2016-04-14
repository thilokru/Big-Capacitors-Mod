package com.mhfs.capacitors;

import java.util.ArrayList;

import com.mhfs.capacitors.blocks.BlockBarrel;
import com.mhfs.capacitors.blocks.BlockBoiler;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockData;
import com.mhfs.capacitors.blocks.BlockDestillationTower;
import com.mhfs.capacitors.blocks.BlockEnergyTransfer;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.blocks.BlockLuxRouter;
import com.mhfs.capacitors.blocks.BlockMany;
import com.mhfs.capacitors.blocks.BlockStirlingEngine;
import com.mhfs.capacitors.blocks.BlockTokamak;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Blocks extends net.minecraft.init.Blocks{

	public static BlockCapacitor capacitorIron;
	public static BlockBarrel blockBarrel;
	public static BlockMany blockMany;
	public static BlockTokamak blockTokamak;
	public static BlockFuelCell blockFuelCell;
	public static BlockLuxRouter blockLuxRouter;
	public static BlockEnergyTransfer blockEnergyTransfer;
	public static BlockBoiler blockBoiler;
	public static BlockDestillationTower blockTower;
	public static BlockStirlingEngine blockStirlingEngine;
	
	public static void setup(BigCapacitorsMod mod){
		Blocks.capacitorIron = new BlockCapacitor(Material.iron, 0);
		Blocks.capacitorIron.setHardness(1.5F);
		Blocks.capacitorIron.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockBarrel = new BlockBarrel(Material.wood);
		Blocks.blockBarrel.setHardness(1F);
		Blocks.blockBarrel.setHarvestLevel("axe", 2);
		
		Blocks.blockTokamak = new BlockTokamak(Material.rock);
		Blocks.blockTokamak.setHardness(0.5F);
		Blocks.blockTokamak.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockFuelCell = new BlockFuelCell(Material.rock);
		Blocks.blockFuelCell.setHardness(0.25F);
		Blocks.blockFuelCell.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockLuxRouter = new BlockLuxRouter(Material.rock);
		Blocks.blockLuxRouter.setHardness(0.25F);
		Blocks.blockLuxRouter.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockEnergyTransfer = new BlockEnergyTransfer(Material.rock);
		Blocks.blockEnergyTransfer.setHardness(0.25F);
		Blocks.blockEnergyTransfer.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockBoiler = new BlockBoiler(Material.rock);
		Blocks.blockBoiler.setHardness(1.5F);
		Blocks.blockBoiler.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockTower = new BlockDestillationTower(Material.rock);
		Blocks.blockTower.setHardness(1.5F);
		Blocks.blockTower.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockStirlingEngine = new BlockStirlingEngine(Material.rock);
		Blocks.blockStirlingEngine.setHardness(1.5F);
		Blocks.blockStirlingEngine.setHarvestLevel("pickaxe", 2);
		
		ArrayList<BlockData> blockData = new ArrayList<BlockData>();
		blockData.add(new BlockData("blockReactorShield", "pickaxe", 2, 3.0F));//0
		
		blockData.add(new BlockData("blockCeramic", "pickaxe", 2, 0.5F, "Ores"));//1
		
		blockData.add(new BlockData("blockCoil", "pickaxe", 2, 3.0F));//2
		
		blockData.add(new BlockData("oreRutil", "pickaxe", 2, 1F, "Ores"));//3
		
		blockData.add(new BlockData("oreWitherite", "pickaxe", 2, 1F, "Ores"));//4
		
		blockData.add(new BlockData("blockCoilEmpty", "pickaxe", 2, 3.0F));//5
		
		Blocks.blockMany = new BlockMany(blockData.toArray(new BlockData[0]));
		
		OreDictionary.registerOre("oreTitandioxid", new ItemStack(Blocks.blockMany, 1, 3));
		OreDictionary.registerOre("oreBariumCarbonate", new ItemStack(Blocks.blockMany, 1, 4));
	}
}
