package com.mhfs.capacitors.render;

import static org.lwjgl.opengl.GL11.*;

import com.mhfs.capacitors.tile.destillery.TileDistillery;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class RendererDestillery extends TileEntitySpecialRenderer {
	
	private final static IModelCustom renderer = AdvancedModelLoader.loadModel(new ResourceLocation("big_capacitors:models/Destillery.obj"));
	private final static ResourceLocation texture = new ResourceLocation("big_capacitors:textures/models/destillery.png");

	@Override
	public void renderTileEntityAt(TileEntity entity, double relX, double relY,
			double relZ, float partialTickTime) {
		TileDistillery destillery = (TileDistillery)entity;
		ForgeDirection orientation = destillery.getOrientation();
		glPushMatrix();
		
		glTranslated(relX, relY, relZ);
		
		glTranslated(0.5, 0.5, 0.5);
		if(orientation == EAST){
			glRotated(180, 0, 1.0, 0);
		}else if(orientation == SOUTH){
			glRotated(90, 0, 1.0, 0);
		}else if(orientation == NORTH){
			glRotated(270, 0, 1.0, 0);
		}
		glTranslated(0, -0.5, 0);
		
		bindTexture(texture);
		renderer.renderAll();
		
		glPopMatrix();
	}

}
