package com.mhfs.api.manual.util;

import com.mhfs.api.manual.gui.GuiManualChapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public interface IPage {

	//GUI
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height);
	
	/**
	 * Please draw the mouse related stuff later. Just mark it to be drawn.
	 * Later, drawMouseRelated is called.
	 * @param mc
	 * @param screen
	 * @param xPos
	 * @param yPos
	 * @param width
	 * @param height
	 * @param mouseX
	 * @param mouseY
	 */
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY);
	
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen, int mouseX, int mouseY);
	
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height);
	
	public void actionPerformed(GuiButton button, Minecraft mc, GuiManualChapter screen);
}
