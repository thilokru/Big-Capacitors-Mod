package com.mhfs.capacitors.handlers;

import net.minecraft.block.BlockLiquid;
import net.minecraftforge.client.event.TextureStitchEvent;

import com.mhfs.capacitors.BigCapacitorsMod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {

	@SubscribeEvent
	public void handleTextureStitch(TextureStitchEvent.Post event){
		BigCapacitorsMod.instance.fluidDestilledWater.setIcons(BlockLiquid.getLiquidIcon("water_still"), BlockLiquid.getLiquidIcon("water_flow"));
	}
}
