package com.mhfs.capacitors.gui.manual;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer buf = tes.getBuffer();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		mc.getTextureManager().bindTexture(loc);
		buf.pos(xPos + width / 2 + disWidth / 2, yPos, screen.getZpos()).tex( 1, 0).endVertex();//Upper Right
		buf.pos(xPos + width / 2 - disWidth / 2, yPos, screen.getZpos()).tex( 0, 0).endVertex();//Upper Left
		buf.pos(xPos + width / 2 - disWidth / 2, yPos + disHeight, screen.getZpos()).tex(0, 1).endVertex();//Lower Right
		buf.pos(xPos + width / 2 + disWidth / 2, yPos + disHeight, screen.getZpos()).tex(1, 1).endVertex();//Lower Left
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
