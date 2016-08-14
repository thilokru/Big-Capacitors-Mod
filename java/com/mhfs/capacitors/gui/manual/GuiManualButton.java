package com.mhfs.capacitors.gui.manual;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.misc.Helper;
import com.mhfs.capacitors.misc.TextureHelper.SubTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

public class GuiManualButton extends GuiButton {

	private Mode mode;

	public GuiManualButton(int id, int x, int y, Mode mode) {
		super(id, x, y, 0, 0, "");
		if (mode == null)
			throw new IllegalArgumentException("Null is not a valid mode!");
		this.mode = mode;
		SubTexture info = GuiManual.TEXTURES.getTextureInfo(mode.getTextureName(false));
		this.width = info.getWidth();
		this.height = info.getHeight();
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			boolean hover = isMouseHovering(mouseX, mouseY);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GuiManual.TEXTURES.drawTextureAt(mc, mode.getTextureName(hover), this.xPosition, this.yPosition);
		}
	}

	@Override
	public void playPressSound(SoundHandler sh) {
		Helper.playPageSound(sh);
	}

	private boolean isMouseHovering(int mouseX, int mouseY) {
		SubTexture info = GuiManual.TEXTURES.getTextureInfo(mode.getTextureName(false));
		int minX = this.xPosition;
		int minY = this.yPosition;
		int maxX = this.xPosition + info.getWidth();
		int maxY = this.yPosition + info.getHeight();
		boolean withinLowerBounds = mouseX >= minX && mouseY >= minY;
		boolean withinUpperBounds = mouseX <= maxX && mouseY <= maxY;
		return withinLowerBounds && withinUpperBounds;
	}

	public static enum Mode {
		FORWARD("button_right_active", "button_right_inactive"),
		BACKWARD("button_left_active", "button_left_inactive"),
		UP("button_up_active", "button_up_inactive");

		private String activeTexture;
		private String inactiveTexture;

		private Mode(String active, String inactive) {
			this.activeTexture = active;
			this.inactiveTexture = inactive;
		}

		public String getTextureName(boolean active) {
			return active ? activeTexture : inactiveTexture;
		}
	}

}
