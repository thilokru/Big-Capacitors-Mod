package com.mhfs.capacitors.render;

import static org.lwjgl.opengl.GL11.*;

import com.mhfs.capacitors.misc.IRotatable;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class RendererOBJ extends TileEntitySpecialRenderer {
	
	private IModelCustom renderer;
	private ResourceLocation texture;
	
	public RendererOBJ(ResourceLocation model, ResourceLocation texture){
		this.renderer = AdvancedModelLoader.loadModel(model);
		this.texture = texture;
	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double relX, double relY,
			double relZ, float partialTickTime) {
		glPushMatrix();
		
		glTranslated(relX, relY, relZ);
		
		glTranslated(0.5, 0.5, 0.5);
		if(entity instanceof IRotatable){
			IRotatable rotatable = (IRotatable)entity;
			ForgeDirection orientation = rotatable.getRotation();
		
			if(orientation == EAST){
				glRotated(180, 0, 1.0, 0);
			}else if(orientation == SOUTH){
				glRotated(90, 0, 1.0, 0);
			}else if(orientation == NORTH){
				glRotated(270, 0, 1.0, 0);
			}
		}
		glTranslated(0, -0.5, 0);
		
		bindTexture(texture);
		renderer.renderAll();
		
		glPopMatrix();
	}

}
