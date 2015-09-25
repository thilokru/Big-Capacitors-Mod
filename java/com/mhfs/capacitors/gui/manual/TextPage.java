package com.mhfs.capacitors.gui.manual;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class TextPage implements IPage {
	
	private String text;
	private String newLine;
	
	public TextPage(String text, String newLine){
		this.text = text;
		this.newLine = newLine;
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int height, int width, int mouseX, int mouseY) {
		String[] lines = text.split(newLine);
		for(String line:lines){
			mc.fontRenderer.drawString(line, xPos, yPos, Color.BLACK.getRGB(), false);
			yPos += mc.fontRenderer.FONT_HEIGHT;
		}
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
