package com.mhfs.capacitors.gui.manual;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonLink extends GuiButton {

	public GuiButtonLink(int id, int x, int y, int width, int height,
			String text) {
		super(id, x, y, width, height, text);
	}

	public GuiButtonLink(int id, int x, int y, String text) {
		super(id, x, y, getTextWidth(text), getTextHeight(), text);
	}

	private static int getTextWidth(String text) {
		Minecraft mc = Minecraft.getMinecraft();
		return mc.fontRenderer.getStringWidth(text);
	}

	private static int getTextHeight() {
		return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		int state = this.getHoverState(isMouseHovering(mouseX, mouseY));
		int color = Color.BLACK.getRGB();
		if (state == 2) {
			color = Color.BLUE.getRGB();
		}
		mc.fontRenderer.drawString(this.displayString, this.xPosition,
				this.yPosition, color, false);
	}

	private boolean isMouseHovering(int mouseX, int mouseY) {
		int minX = this.xPosition;
		int minY = this.yPosition;
		int maxX = this.xPosition + this.width;
		int maxY = this.yPosition + this.height;
		boolean withinLowerBounds = mouseX >= minX && mouseY >= minY;
		boolean withinUpperBounds = mouseX <= maxX && mouseY <= maxY;
		return withinLowerBounds && withinUpperBounds;
	}

	@Override
	public void func_146113_a(SoundHandler sh) {
		sh.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(
				"big_capacitors:pageTurn"), 1.0F));
	}
}
