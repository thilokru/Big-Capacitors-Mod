package com.mhfs.capacitors.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.misc.IChapterRelated;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ManualOverlayHandler extends Gui implements IOverlayHandler {

	private final static ResourceLocation overlayTexture = new ResourceLocation(BigCapacitorsMod.modid, "textures/other/overlay.png");

	@Override
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, BlockPos pos) {
		if (block instanceof IChapterRelated) {
			IChapterRelated rel = (IChapterRelated) block;
			if (rel.getChapter(world, pos) == null)
				return;
			Gui gui = Minecraft.getMinecraft().ingameGUI;

			int xPos = event.getResolution().getScaledWidth() / 2;
			int yPos = event.getResolution().getScaledHeight() / 2;
			
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(1, 1, 1, 1);
			
			bindTexture(overlayTexture);
			this.drawTexturedModalRect(xPos - 16, yPos, 62, 0, 16, 16);
			GL11.glPopMatrix();
			
			gui.drawString(Minecraft.getMinecraft().fontRendererObj, rel.getChapter(world, pos), xPos + 2, yPos + 5, Color.WHITE.getRGB());
		}
	}

	private void bindTexture(ResourceLocation loc) {
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}
}
