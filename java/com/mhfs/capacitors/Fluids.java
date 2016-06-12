package com.mhfs.capacitors;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Fluids {

	public static Fluid fluidDestilledWater;
	public static Fluid fluidEthanol;
	public static Fluid fluidWine;
	
	public static Fluid gasHydrogen;
	
	public static BlockFluidClassic blockDestilledWater;
	public static BlockFluidClassic blockEthanol;
	public static BlockFluidClassic blockWine;
	
	public static BlockFluidClassic blockHydrogen;
	
	public static void setup(CreativeTabs tab) {
		
		Fluids.fluidDestilledWater = regFluid("destilledwater", "minecraft", "blocks/water_still", "blocks/water_flow", 1000, 1000, false);
		Fluids.blockDestilledWater = regFluidBlock(tab, fluidDestilledWater, "blockDestilledWater");
		
		Fluids.fluidEthanol = regFluid("ethanol", "blocks/ethanol_still", "blocks/ethanol_flow", 789, 1190, false);
		Fluids.blockEthanol = regFluidBlock(tab, fluidEthanol, "blockEthanol");
		
		Fluids.fluidWine = regFluid("wine", "blocks/wine_still", "blocks/wine_flow", 900, 1050, false);
		Fluids.blockWine = regFluidBlock(tab, fluidWine, "blockWine");
		
		Fluids.gasHydrogen = regFluid("hydrogen", "blocks/blank", "blocks/blank", 1, 1, true);
		Fluids.blockHydrogen = regFluidBlock(tab, gasHydrogen, "blockHydrogen");
	}
	
	private static BlockFluidClassic regFluidBlock(CreativeTabs tab, Fluid fluid, String name){
		BlockFluidClassic block = new BlockFluidClassic(fluid, Material.WATER);
		block.setCreativeTab(tab);
		block.setUnlocalizedName(name);
		GameRegistry.registerBlock(block, name);
		return block;
	}
	
	private static Fluid regFluid(String name, String still, String flowing, int density, int viscosity, boolean gaseous){
		return regFluid(name, BigCapacitorsMod.modid, still, flowing, density, viscosity, gaseous);
	}
	
	private static Fluid regFluid(String name, String modid, String still, String flowing, int density, int viscosity, boolean gaseous){
		ResourceLocation resourceStill = new ResourceLocation(modid, still);
		ResourceLocation resourceFlowing = new ResourceLocation(modid, flowing);
		Fluid fluid = new Fluid(name, resourceStill, resourceFlowing);
		fluid.setDensity(density);
		fluid.setViscosity(viscosity);
		fluid.setGaseous(gaseous);
		FluidRegistry.registerFluid(fluid);
		return fluid;
	}
}
