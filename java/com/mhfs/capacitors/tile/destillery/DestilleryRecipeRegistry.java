package com.mhfs.capacitors.tile.destillery;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class DestilleryRecipeRegistry {

	private static Map<Fluid, DestilleryRecipe> registry = new HashMap<Fluid, DestilleryRecipe>();
	
	public static DestilleryRecipe forInput(Fluid input){
		return registry.get(input);
	}
	
	public static void registerRecipe(DestilleryRecipe recipe){
		registry.put(recipe.getInput().getFluid(), recipe);
	}
	
	public static void registerRecipe(FluidStack in, FluidStack out, int rfCost){
		registerRecipe(new DestilleryRecipe(in, out, rfCost));
	}
}
