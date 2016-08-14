package com.mhfs.capacitors.gui;

import java.awt.Color;

import com.mhfs.capacitors.misc.IChapterRelated;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ManualOverlayHandler extends Gui implements IOverlayHandler {

	@Override
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, BlockPos pos) {
		if (block instanceof IChapterRelated) {
			IChapterRelated rel = (IChapterRelated) block;
			if (rel.getChapter(world, pos) == null)
				return;
			Gui gui = Minecraft.getMinecraft().ingameGUI;

			int xPos = event.getResolution().getScaledWidth() / 2;
			int yPos = event.getResolution().getScaledHeight() / 2;
			
			GuiOverlayHandler.OVERLAY.drawTextureAt(Minecraft.getMinecraft(), "manual", xPos - 16, yPos);
			
			gui.drawString(Minecraft.getMinecraft().fontRendererObj, rel.getChapter(world, pos), xPos + 2, yPos + 5, Color.WHITE.getRGB());
		}
	}
}
