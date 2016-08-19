package com.mhfs.api.manual.gui;

import com.mhfs.capacitors.misc.TextureHelper;
import com.mhfs.capacitors.misc.TextureHelper.SubTexture;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiManual extends GuiScreen {

	public final static int MARGIN = 10, BOOK_BORDER = 8;
	public static TextureHelper TEXTURES;
	
	private boolean unicodeFlag;

	public GuiManual(ResourceLocation textureLocation) {
		super();
		TEXTURES = TextureHelper.get(textureLocation);
	}

	public void drawBackground() {
		SubTexture tex = TEXTURES.getTextureInfo("background");
		int xOffset = (this.width - tex.getWidth()) / 2;
		TEXTURES.drawTextureAt(this.mc, "background", xOffset, 2);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void preTextRender() {
		this.unicodeFlag = mc.fontRendererObj.getUnicodeFlag();
		mc.fontRendererObj.setUnicodeFlag(true);
	}

	public void postTextRender() {
		mc.fontRendererObj.setUnicodeFlag(unicodeFlag);
	}

	public float getZpos() {
		return this.zLevel;
	}
}
