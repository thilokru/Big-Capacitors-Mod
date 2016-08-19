package com.mhfs.api.manual.gui.pages;

import java.awt.Color;

import com.mhfs.api.manual.gui.GuiManualChapter;
import com.mhfs.api.manual.util.IPage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class TextPage implements IPage {
	
	private String text;
	private String newLine;
	
	public TextPage(String text, String newLine){
		this.text = text;
		this.newLine = newLine;
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY) {
		String[] lines = text.split(newLine);
		for(String line:lines){
			int drawnLines = drawLineWrappedText(mc.fontRendererObj, line, xPos, yPos, width, Color.BLACK.getRGB());
			yPos += mc.fontRendererObj.FONT_HEIGHT * drawnLines;
		}
	}
	
	private int drawLineWrappedText(FontRenderer fr, String text, int xPos, int yPos, int lineWidth, int color) {
		int drawnLines = 1;
		int cursorX = xPos;
		int cursorY = yPos + fr.FONT_HEIGHT;
		String[] words = text.split(" ");
		for(String word : words) {
			word += " ";
			int wordWidth = fr.getStringWidth(word);
			if((wordWidth + cursorX) < (xPos + lineWidth)) {
				fr.drawString(word, cursorX, cursorY, color, false);
				cursorX += wordWidth;
			} else {
				cursorX = xPos;
				cursorY += fr.FONT_HEIGHT;
				fr.drawString(word, cursorX, cursorY, color, false);
				cursorX += wordWidth;
				drawnLines++;
			}
		}
		return drawnLines;
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
