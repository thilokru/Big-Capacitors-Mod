package com.mhfs.capacitors.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mhfs.capacitors.tile.lux.TileDrain;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class DebugOverlayHandler implements IOverlayHandler {

	private int width, height;

	@Override
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, int x, int y, int z) {
		width = event.resolution.getScaledWidth();
		height = event.resolution.getScaledHeight();
		TileEntity entity = world.getTileEntity(x, y, z);
		if(entity instanceof TileLuxRouter){
			List<String> debugText = new ArrayList<String>();
			TileLuxRouter router = (TileLuxRouter)entity;
			debugText.add("" + router.getRouteSucction());
			drawHoveringText(debugText, width/2 + 5, height/2 + 5, Minecraft.getMinecraft().fontRenderer);
		}else if(entity instanceof TileDrain){
			List<String> debugText = new ArrayList<String>();
			TileDrain drain = (TileDrain)entity;
			debugText.add("" + drain.getEnergy());
			drawHoveringText(debugText, width/2 + 5, height/2 + 5, Minecraft.getMinecraft().fontRenderer);
		}
	}

	protected void drawHoveringText(List<String> text, int x, int y, FontRenderer font) {
		if (!text.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;
			Iterator<String> iterator = text.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				int l = font.getStringWidth(s);

				if (l > k) {
					k = l;
				}
			}

			int j2 = x + 12;
			int k2 = y - 12;
			int i1 = 8;

			if (text.size() > 1) {
				i1 += 2 + (text.size() - 1) * 10;
			}

			if (j2 + k > this.width) {
				j2 -= 28 + k;
			}

			if (k2 + i1 + 6 > this.height) {
				k2 = this.height - i1 - 6;
			}

			int j1 = -267386864;
			this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
			this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
			this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
			this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
			this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
			int k1 = 1347420415;
			int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
			this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
			this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
			this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
			this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

			for (int i2 = 0; i2 < text.size(); ++i2) {
				String s1 = (String) text.get(i2);
				font.drawStringWithShadow(s1, j2, k2, -1);

				if (i2 == 0) {
					k2 += 2;
				}

				k2 += 10;
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	protected void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_) {
		float f = (float) (p_73733_5_ >> 24 & 255) / 255.0F;
		float f1 = (float) (p_73733_5_ >> 16 & 255) / 255.0F;
		float f2 = (float) (p_73733_5_ >> 8 & 255) / 255.0F;
		float f3 = (float) (p_73733_5_ & 255) / 255.0F;
		float f4 = (float) (p_73733_6_ >> 24 & 255) / 255.0F;
		float f5 = (float) (p_73733_6_ >> 16 & 255) / 255.0F;
		float f6 = (float) (p_73733_6_ >> 8 & 255) / 255.0F;
		float f7 = (float) (p_73733_6_ & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(f1, f2, f3, f);
		tessellator.addVertex((double) p_73733_3_, (double) p_73733_2_, 300D);
		tessellator.addVertex((double) p_73733_1_, (double) p_73733_2_, 300D);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex((double) p_73733_1_, (double) p_73733_4_, 300D);
		tessellator.addVertex((double) p_73733_3_, (double) p_73733_4_, 300D);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
