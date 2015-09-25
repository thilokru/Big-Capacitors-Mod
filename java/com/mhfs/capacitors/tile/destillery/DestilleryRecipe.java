package com.mhfs.capacitors.tile.destillery;

import net.minecraftforge.fluids.FluidStack;

public class DestilleryRecipe {

	private FluidStack input, output;
	private int rfCost;
	
	public DestilleryRecipe(FluidStack input, FluidStack output, int rfCost){
		this.input = input;
		this.output = output;
		this.rfCost = rfCost;
	}
	
	public FluidStack getInput(){
		return input;
	}
	
	public FluidStack getOutput(){
		return output;
	}
	
	public int getRFCost(){
		return rfCost;
	}
}
