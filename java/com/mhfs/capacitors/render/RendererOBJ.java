package com.mhfs.capacitors.render;

import static org.lwjgl.opengl.GL11.*;

import com.mhfs.capacitors.misc.IRotatable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class RendererOBJ extends TileEntitySpecialRenderer implements IItemRenderer {

	private IModelCustom renderer;
	private ResourceLocation texture;

	public RendererOBJ(ResourceLocation model, ResourceLocation texture) {
		this.renderer = AdvancedModelLoader.loadModel(model);
		this.texture = texture;
	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partialTick) {
		ForgeDirection orientation = ForgeDirection.NORTH;
		if(entity instanceof IRotatable){
			orientation = ((IRotatable)entity).getRotation();
		}
		
		glPushMatrix();

		glTranslated(x, y, z);

		glTranslated(0.5, 0.5, 0.5);
		if (orientation == EAST) {
			glRotated(180, 0, 1.0, 0);
		} else if (orientation == SOUTH) {
			glRotated(90, 0, 1.0, 0);
		} else if (orientation == NORTH) {
			glRotated(270, 0, 1.0, 0);
		}
		glTranslated(0, -0.5, 0);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		renderer.renderAll();

		glPopMatrix();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
		case ENTITY:
		case EQUIPPED_FIRST_PERSON:
		case EQUIPPED:
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY:
			renderItem(item, 0f, 0f, 0f);
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			renderItem(item, 0.5f, 0.5f, 0.5f);
			break;
		case INVENTORY:
			renderItem(item, 0f, -0.5f, 0f);
			break;
		default:
			renderItem(item, 0f, 0f, 0f);
		}
	}

	private void renderItem(ItemStack itemStack, float x, float y, float z) {
		glPushMatrix();

		glTranslatef(x, y, z);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		renderer.renderAll();

		glPopMatrix();
	}
}
