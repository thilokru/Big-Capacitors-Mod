package com.mhfs.capacitors.gui.manual;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class ImagePage implements IPage{
	
	private ResourceLocation loc;
	private int disHeight, disWidth;
	
	public ImagePage(ResourceLocation rl, int disHeight, int disWidth){
		this.loc = rl;
		this.disHeight = disHeight;
		this.disWidth = disWidth;
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height, int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		Tessellator tes = Tessellator.instance;
		tes.startDrawingQuads();
		mc.getTextureManager().bindTexture(loc);
		tes.addVertexWithUV(xPos + width / 2 + disWidth / 2, yPos, screen.getZpos(), 1, 0);//Upper Right
		tes.addVertexWithUV(xPos + width / 2 - disWidth / 2, yPos, screen.getZpos(), 0, 0);//Upper Left
		tes.addVertexWithUV(xPos + width / 2 - disWidth / 2, yPos + disHeight, screen.getZpos(), 0, 1);//Lower Right
		tes.addVertexWithUV(xPos + width / 2 + disWidth / 2, yPos + disHeight, screen.getZpos(), 1, 1);//Lower Left
		tes.draw();
	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc,
			GuiManualChapter screen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen,
			int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}

}
