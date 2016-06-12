package com.mhfs.capacitors.misc;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;

public class Helper {
	public static ResourceLocation getTextureFromFluid(Fluid fluid){
		ResourceLocation still = fluid.getStill();
		return new ResourceLocation(still.getResourceDomain(), "textures/" + still.getResourcePath() + ".png");
	}
	
	public static void playPageSound(SoundHandler sh){
		sh.playSound(PositionedSoundRecord.getMasterRecord(new SoundEvent(new ResourceLocation("big_capacitors:pageTurn")), 1.0F));
	}
}
