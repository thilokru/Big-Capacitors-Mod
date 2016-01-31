package com.mhfs.capacitors;

import java.util.ArrayList;

import com.mhfs.capacitors.blocks.BlockBarrel;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.blocks.BlockData;
import com.mhfs.capacitors.blocks.BlockDestillery;
import com.mhfs.capacitors.blocks.BlockFluidBase;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.blocks.BlockLuxDrain;
import com.mhfs.capacitors.blocks.BlockLuxRouter;
import com.mhfs.capacitors.blocks.BlockMany;
import com.mhfs.capacitors.blocks.BlockTomahawk;
import com.mhfs.capacitors.items.ItemBlockMany;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class Blocks extends net.minecraft.init.Blocks{

	public static BlockCapacitor capacitorIron;
	public static BlockDestillery blockDestillery;
	public static BlockBarrel blockBarrel;
	public static BlockMany blockMany;
	public static BlockTomahawk blockTomahawk;
	public static BlockFuelCell blockFuelCell;
	public static BlockLuxRouter blockLuxRouter;
	public static BlockLuxDrain blockLuxDrain;
	
	public static void setup(BigCapacitorsMod mod){
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
		
		Blocks.blockFuelCell = new BlockFuelCell(Material.rock);
		Blocks.blockFuelCell.setBlockName("blockFuelCell");
		Blocks.blockFuelCell.setCreativeTab(mod.creativeTab);
		Blocks.blockFuelCell.setHardness(0.25F);
		Blocks.blockFuelCell.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(Blocks.blockFuelCell, "blockFuelCell");
		
		Blocks.blockLuxRouter = new BlockLuxRouter(Material.rock);
		Blocks.blockLuxRouter.setBlockName("blockLuxRouter");
		Blocks.blockLuxRouter.setCreativeTab(mod.creativeTab);
		Blocks.blockLuxRouter.setHardness(0.25F);
		Blocks.blockLuxRouter.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(Blocks.blockLuxRouter, "blockLuxRouter");
		
		Blocks.blockLuxDrain = new BlockLuxDrain(Material.rock);
		Blocks.blockLuxDrain.setBlockName("blockLuxDrain");
		Blocks.blockLuxDrain.setCreativeTab(mod.creativeTab);
		Blocks.blockLuxDrain.setHardness(0.25F);
		Blocks.blockLuxDrain.setHarvestLevel("pickaxe", 2);
		GameRegistry.registerBlock(Blocks.blockLuxDrain, "blockLuxDrain");
		
		ArrayList<BlockData> blockData = new ArrayList<BlockData>();
		blockData.add(new BlockData("reactorShield", "big_capacitors:reactorShield", "pickaxe", 2, 3.0F));//0
		
		blockData.add(new BlockData("blockCeramic", "big_capacitors:ceramic", "pickaxe", 2, 0.5F, "Ores"));//1
		
		BlockData coil = new BlockData("coil", "big_capacitors:coil", "pickaxe", 2, 3.0F);//2
		coil.setSpecialTexture(ForgeDirection.UP, "big_capacitors:coilTop");
		coil.setSpecialTexture(ForgeDirection.DOWN, "big_capacitors:coilTop");
		blockData.add(coil);
		
		blockData.add(new BlockData("oreRutil", "big_capacitors:oreRutil", "pickaxe", 2, 1F, "Ores"));//3
		
		blockData.add(new BlockData("oreWitherite", "big_capacitors:oreWitherite", "pickaxe", 2, 1F, "Ores"));//4
		
		BlockData coilEmpty = new BlockData("coilEmpty", "big_capacitors:coil", "pickaxe", 2, 3.0F);//5
		coilEmpty.setSpecialTexture(ForgeDirection.UP, "big_capacitors:coilTopEmpty");
		coilEmpty.setSpecialTexture(ForgeDirection.DOWN, "big_capacitors:coilTopEmpty");
		blockData.add(coilEmpty);
		
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
		
		
		Fluids.blockEthanol = new BlockFluidBase(Fluids.fluidEthanol, Material.water, "big_capacitors:ethanol_still", "big_capacitors:ethanol_flow");
		Fluids.blockEthanol.setCreativeTab(mod.creativeTab);
		Fluids.blockEthanol.setBlockName("blockEthanol");
		GameRegistry.registerBlock(Fluids.blockEthanol, "blockEthanol");
		
		
		Fluids.blockWine = new BlockFluidBase(Fluids.fluidWine, Material.water, "big_capacitors:wine_still", "big_capacitors:wine_flow");
		Fluids.blockWine.setCreativeTab(mod.creativeTab);
		Fluids.blockWine.setBlockName("blockWine");
		GameRegistry.registerBlock(Fluids.blockWine, "blockWine");
		
		Fluids.blockHydrogen = new BlockFluidBase(Fluids.gasHydrogen, Material.water, "big_capacitors:blank", "big_capacitors:blank");
		Fluids.blockHydrogen.setCreativeTab(mod.creativeTab);
		Fluids.blockHydrogen.setBlockName("blockHydrogen");
		GameRegistry.registerBlock(Fluids.blockHydrogen, "blockHydrogen");
	}

}
