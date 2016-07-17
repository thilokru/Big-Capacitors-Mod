package com.mhfs.capacitors;

import java.util.ArrayList;

import com.mhfs.capacitors.blocks.BlockBarrel;
import com.mhfs.capacitors.blocks.BlockBoiler;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockCrusher;
import com.mhfs.capacitors.blocks.BlockData;
import com.mhfs.capacitors.blocks.BlockDestillationTower;
import com.mhfs.capacitors.blocks.BlockEnergyTransfer;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.blocks.BlockLuxRouter;
import com.mhfs.capacitors.blocks.BlockMany;
import com.mhfs.capacitors.blocks.BlockStirlingEngine;
import com.mhfs.capacitors.blocks.BlockMachineController;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Blocks extends net.minecraft.init.Blocks{

	public static BlockCapacitor capacitorIron;
	public static BlockBarrel blockBarrel;
	public static BlockMany blockMany;
	public static BlockMachineController blockMachineController;
	public static BlockFuelCell blockFuelCell;
	public static BlockLuxRouter blockLuxRouter;
	public static BlockEnergyTransfer blockEnergyTransfer;
	public static BlockBoiler blockBoiler;
	public static BlockDestillationTower blockTower;
	public static BlockStirlingEngine blockStirlingEngine;
	public static BlockCrusher blockCrusher;
	
	public static void setup(BigCapacitorsMod mod){
		Blocks.capacitorIron = new BlockCapacitor(Material.IRON, 0);
		Blocks.capacitorIron.setHardness(1.5F);
		Blocks.capacitorIron.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockBarrel = new BlockBarrel(Material.WOOD);
		Blocks.blockBarrel.setHardness(1F);
		Blocks.blockBarrel.setHarvestLevel("axe", 2);
		
		Blocks.blockMachineController = new BlockMachineController(Material.ROCK);
		Blocks.blockMachineController.setHardness(0.5F);
		Blocks.blockMachineController.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockFuelCell = new BlockFuelCell(Material.ROCK);
		Blocks.blockFuelCell.setHardness(0.25F);
		Blocks.blockFuelCell.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockLuxRouter = new BlockLuxRouter(Material.ROCK);
		Blocks.blockLuxRouter.setHardness(0.25F);
		Blocks.blockLuxRouter.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockEnergyTransfer = new BlockEnergyTransfer(Material.ROCK);
		Blocks.blockEnergyTransfer.setHardness(0.25F);
		Blocks.blockEnergyTransfer.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockBoiler = new BlockBoiler(Material.ROCK);
		Blocks.blockBoiler.setHardness(1.5F);
		Blocks.blockBoiler.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockTower = new BlockDestillationTower(Material.ROCK);
		Blocks.blockTower.setHardness(1.5F);
		Blocks.blockTower.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockStirlingEngine = new BlockStirlingEngine(Material.ROCK);
		Blocks.blockStirlingEngine.setHardness(1.5F);
		Blocks.blockStirlingEngine.setHarvestLevel("pickaxe", 2);
		
		Blocks.blockCrusher = new BlockCrusher(Material.ROCK);
		Blocks.blockCrusher.setHardness(4F);
		Blocks.blockCrusher.setHarvestLevel("pickaxe", 2);
		
		ArrayList<BlockData> blockData = new ArrayList<BlockData>();
		blockData.add(new BlockData("blockReactorShield", "pickaxe", 2, 3.0F));//0
		
		blockData.add(new BlockData("blockCeramic", "pickaxe", 2, 0.5F, "Ores"));//1
		
		blockData.add(new BlockData("blockCoil", "pickaxe", 2, 3.0F));//2
		
		blockData.add(new BlockData("oreRutil", "pickaxe", 2, 1F, "Ores"));//3
		
		blockData.add(new BlockData("oreWitherite", "pickaxe", 2, 1F, "Ores"));//4
		
		blockData.add(new BlockData("blockCoilEmpty", "pickaxe", 2, 3.0F));//5
		
		blockData.add(new BlockData("blockMachineChasis", "pickaxe", 2, 2.0F));//6
		
		Blocks.blockMany = new BlockMany(blockData.toArray(new BlockData[0]));
		
		OreDictionary.registerOre("oreTitandioxid", new ItemStack(Blocks.blockMany, 1, 3));
		OreDictionary.registerOre("oreBariumCarbonate", new ItemStack(Blocks.blockMany, 1, 4));
	}
}
