package com.mhfs.api.manual.gui;

import java.io.IOException;

import com.mhfs.capacitors.misc.Lo;
import com.mhfs.capacitors.misc.TextureHelper;
import com.mhfs.capacitors.misc.TextureHelper.SubTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiManual extends GuiScreen {

	public final static int MARGIN = 10, BOOK_BORDER = 8;
	private final static ResourceLocation TEX_CFG_LOC = new ResourceLocation("big_capacitors", "textures/other/manual.cfg");
	public final static TextureHelper TEXTURES;

	static {
		TextureHelper helper = null;
		try {
			helper = TextureHelper.loadFromJSON(Minecraft.getMinecraft().getResourceManager(), TEX_CFG_LOC);
		} catch (IOException e) {
			Lo.g.error("Manual texture error occured!", e);
		}
		TEXTURES = helper;
	}
	
	private boolean unicodeFlag;

	public GuiManual() {
		super();
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
