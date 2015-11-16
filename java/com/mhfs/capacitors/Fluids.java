package com.mhfs.capacitors;

import com.mhfs.capacitors.blocks.BlockFluidBase;

import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class Fluids {

	public static Fluid fluidDestilledWater;
	public static Fluid fluidEthanol;
	public static Fluid fluidWine;
	
	public static Fluid gasHydrogen;
	
	public static BlockFluidClassic blockDestilledWater;
	public static BlockFluidClassic blockEthanol;
	public static BlockFluidBase blockWine;
	
	public static BlockFluidBase blockHydrogen;
	
	public static void setup() {
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
}
