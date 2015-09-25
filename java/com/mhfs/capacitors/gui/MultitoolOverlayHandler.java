package com.mhfs.capacitors.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.CapacitorEnergyStorage;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.destillery.TileDistillery;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class MultitoolOverlayHandler extends Gui implements IOverlayHandler {
	
	private final static ResourceLocation overlayTexture = new ResourceLocation(BigCapacitorsMod.modid, "textures/other/overlay.png");

	private void drawOverlay(RenderGameOverlayEvent event, TileEntity entity) {
		if (entity == null)
			return;
		if (entity instanceof TileCapacitor) {
			renderCapacitorOverlay(event, entity);
		}else if (entity instanceof TileDistillery){
			renderDestilleryOverlay(event, entity);
		}

	}


	private void renderDestilleryOverlay(RenderGameOverlayEvent event,
			TileEntity entity) {
		TileDistillery tile = (TileDistillery) entity;
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;
		if (tile == null)return;
		
		float maxCap = (float)TileDistillery.TANK_CAPCITY;
		
		FluidStack in = tile.getInputStack();
		renderFluidStack(in, xPos - 20, yPos + 5, in != null?in.amount/maxCap:0);
		
		float filled = (float)tile.getEnergyStored(null)/(float)tile.getMaxEnergyStored(null);
		renderEnergy(xPos - 3, yPos + 5, filled);
		
		FluidStack out = tile.getOutputStack();
		renderFluidStack(out, xPos + 5, yPos + 5, out != null?out.amount/maxCap:0);
	}
	
	private void renderEnergy(int x, int y, float filled){
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		
		bindTexture(overlayTexture);
		this.drawTexturedModalRect(x, y, 55, 0, 7, 16);
		int v = (int) (16 * ( 1F - filled));
		y += (16 * (1F - filled));
		int height  = (int)( 16 * filled);
		this.drawTexturedModalRect(x, y, 48, v, 7, height);
		GL11.glPopMatrix();
	}
	
	private void renderFluidStack(FluidStack stack, int x, int y, float percentageFilled){
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		
		bindTexture(overlayTexture);
		this.drawTexturedModalRect(x, y, 32, 0, 16, 16);
		
		if(stack != null){
			Fluid fluid = stack.getFluid();
			IIcon icon = fluid.getIcon();
			if(icon != null){
				bindTexture(fluid.getSpriteNumber());
				this.drawTexturedModelRectFromIcon(x, (int) (y + (1 - percentageFilled) * 16), icon, 16, (int) (16 * percentageFilled));
			}
		}
		
		bindTexture(overlayTexture);
		this.drawTexturedModalRect(x, y, 16, 0, 16, 16);
		GL11.glPopMatrix();
	}

	private void renderCapacitorOverlay(RenderGameOverlayEvent event,
			TileEntity entity) {
		TileCapacitor tile = (TileCapacitor) entity;
		Gui gui = Minecraft.getMinecraft().ingameGUI;
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;
		if (tile == null || tile.getEntityCapacitor() == null)
			return;
		CapacitorEnergyStorage storage = tile.getEntityCapacitor().getStorage();
		String text = "RF: " + storage.getAllEnergyStored() + "/"
				+ storage.getWholeCapacity();
		gui.drawString(Minecraft.getMinecraft().fontRenderer, text, xPos + 5,
				yPos + 5, Color.WHITE.getRGB());
		ForgeDirection extract = tile.getEntityCapacitor()
				.getExtractEnergy();
		if (extract == tile.getOrientation()){
			bindTexture(overlayTexture);
			this.drawTexturedModalRect(xPos - 21, yPos, 0, 0, 16, 16);
		}
		/**gui.drawString(Minecraft.getMinecraft().fontRenderer, ""
				+ tile.getEntityCapacitor().hashCode(), xPos, yPos + 20,
				Color.WHITE.getRGB());**/
	}
	
	protected void bindTexture(ResourceLocation loc){
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}
	
	protected void bindTexture(int id){
		ResourceLocation loc = Minecraft.getMinecraft().renderEngine.getResourceLocation(id);
		this.bindTexture(loc);
	}

	@Override
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, int x,
			int y, int z) {
		drawOverlay(event, world.getTileEntity(x, y, z));
	}
}
