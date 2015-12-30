package com.mhfs.capacitors.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RendererLuxRouter extends RendererOBJ {

	public RendererLuxRouter(ResourceLocation model, ResourceLocation texture) {
		super(model, texture);
	}

	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partialTick) {
		super.renderTileEntityAt(entity, x, y, z, partialTick);
		//TODO:Render LightBolt
	}

}
