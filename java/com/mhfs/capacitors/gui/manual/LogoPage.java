package com.mhfs.capacitors.gui.manual;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class LogoPage implements IPage {

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY) {
		boolean backup = mc.fontRendererObj.getUnicodeFlag();
		mc.fontRendererObj.setUnicodeFlag(false);
		mc.getTextureManager().bindTexture(GuiManual.BOOK_TEXTURES);
		int logoX = xPos;
		int logoY = yPos + (height / 2) - 16;
		GL11.glColor4f(1F, 1F, 1F, 1F);
		screen.drawTexturedModalRect(logoX, logoY, 48, 0, 16, 32);
		
		int textX = logoX + 16 + GuiManual.MARGIN / 2;
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		logoX = xPos + width - 16;
		screen.drawTexturedModalRect(logoX, logoY, 64, 0, 16, 32);
		
		int textY = yPos + (height / 2);
		int color = 0x333333;
		FontRenderer fr = mc.fontRendererObj;
		fr.drawString("Big", textX, textY - fr.FONT_HEIGHT, color, false);
		fr.drawString("Capacitors", textX, textY, color, false);
		mc.fontRendererObj.setUnicodeFlag(backup);
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos, int yPos,
			int width, int height) {
	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos, int yPos,
			int width, int height) {
	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc, GuiManualChapter screen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen,
			int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}

}
