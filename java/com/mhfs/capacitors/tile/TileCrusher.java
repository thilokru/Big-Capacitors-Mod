package com.mhfs.capacitors.tile;

import com.google.common.collect.ImmutableMap;
import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

public class TileCrusher extends TileEntity {

	private IAnimationStateMachine asm;
	private final TimeValues.VariableValue cycleLength = new TimeValues.VariableValue(4);
	private final TimeValues.VariableValue clickTime = new TimeValues.VariableValue(Float.NEGATIVE_INFINITY);

	public TileCrusher(){
		ResourceLocation asmLocation = new ResourceLocation(BigCapacitorsMod.modid, "asms/block/blockCrusher.json");
		ImmutableMap<String, ITimeValue> asmParams = ImmutableMap.of("cycle_length", (ITimeValue) cycleLength, "click_time", (ITimeValue) clickTime);
		asm = ModelLoaderRegistry.loadASM(asmLocation, asmParams);
		this.setActive(true);
	}

	public void setActive(boolean active) {
		if (active) {
			if (asm.currentState().equals("default")) {
				clickTime.setValue(Animation.getWorldTime(getWorld(), Animation.getPartialTickTime()));
				asm.transition("starting");
			}
		} else {
			if (!asm.currentState().equals("moving")) {
				clickTime.setValue(Animation.getWorldTime(getWorld(), Animation.getPartialTickTime()));
				asm.transition("stopping");
			}
		}
	}
	
	@Override
	public boolean hasFastRenderer() {
		return true;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing side) {
		if (capability == CapabilityAnimation.ANIMATION_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing side) {
		if (capability == CapabilityAnimation.ANIMATION_CAPABILITY) {
			return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
		}
		return super.getCapability(capability, side);
	}
}
