package com.mhfs.capacitors.gui.manual;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.misc.Helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import static com.mhfs.capacitors.gui.manual.GuiManualButton.Mode.*;

public class GuiManualButton extends GuiButton {

	private Mode mode;
	public final static int SIZE = 16;

	public GuiManualButton(int id, int x, int y, Mode mode) {
		super(id, x, y, SIZE, SIZE, "");
		if (mode == null)
			throw new IllegalArgumentException("Null is not a valid mode!");
		this.mode = mode;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			boolean hover = isMouseHovering(mouseX, mouseY);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			mc.getTextureManager().bindTexture(GuiManual.BOOK_TEXTURES);
			int u = mode == FORWARD ? 0 : mode == BACKWARD ? 16 : 32;
			this.drawTexturedModalRect(this.xPosition, this.yPosition, u, hover ? 16 : 0, 16, 16);
		}
	}

	@Override
	public void playPressSound(SoundHandler sh) {
		Helper.playPageSound(sh);
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

	public static enum Mode {
		FORWARD, BACKWARD, UP;
	}

}
