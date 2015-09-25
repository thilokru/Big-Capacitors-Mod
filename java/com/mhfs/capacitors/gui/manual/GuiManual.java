package com.mhfs.capacitors.gui.manual;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiManual extends GuiScreen {

	public static final ResourceLocation BOOK_TEXTURES = new ResourceLocation(
			"big_capacitors:textures/other/manual.png");
	public final static int MARGIN = 10, BOOK_BORDER = 8;
	protected int bookU = 0;
	protected int bookV = 76;
	protected int bookImageWidth = 256;
	protected int bookImageHeight = 180;
	protected int yOffset = 2;

	private boolean unicodeFlag;

	public GuiManual() {
		super();
	}

	public void drawBackground() {
		this.mc.getTextureManager().bindTexture(BOOK_TEXTURES);
		int xOffset = (this.width - this.bookImageWidth) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(xOffset, yOffset, bookU, bookV,
				bookImageWidth, bookImageHeight);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void preTextRender() {
		this.unicodeFlag = mc.fontRenderer.getUnicodeFlag();
		mc.fontRenderer.setUnicodeFlag(true);
	}

	public void postTextRender() {
		mc.fontRenderer.setUnicodeFlag(unicodeFlag);
	}

	public float getZpos() {
		return this.zLevel;
	}
}
