package com.mhfs.api.manual.gui.pages;

import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mhfs.api.helper.DefinedBlock;
import com.mhfs.api.manual.gui.GuiManualChapter;
import com.mhfs.api.manual.util.IPage;
import com.mhfs.capacitors.misc.Multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class MultiblockPage implements IPage {

	private Multiblock mb;
	private float scale;
	private final static int ITEM_SIZE = 16;

	public MultiblockPage(String mbLoc, float scale, IResourceManager manager) {
		mb = Multiblock.getMultiblock(new ResourceLocation(mbLoc), manager);
		this.scale = scale;
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY) {
		Set<DefinedBlock> blocks = mb.getBlocks(new BlockPos(0, 0, 0), EnumFacing.NORTH);

		BlockRendererDispatcher br = mc.getBlockRendererDispatcher();
		GlStateManager.depthMask(true);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(xPos + width / 2,  yPos + height / 2, screen.getZpos() + 10 * ITEM_SIZE);
		GL11.glScaled(ITEM_SIZE, -ITEM_SIZE, ITEM_SIZE);
		GL11.glScalef(scale, scale, scale);
		GL11.glRotated(20, 1, 0, 0);
		GL11.glRotated(45, 0, 1, 0);
		RenderHelper.enableStandardItemLighting();

		for (DefinedBlock block : blocks) {
			if(block.getBlockType() == null || block.getBlockType() == Blocks.AIR) continue;
			GL11.glPushMatrix();
			GL11.glTranslatef(block.getX(), block.getY(), block.getZ());
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			IBlockState state = null;
			if(block.getMetadata() == -1) {
				state = block.getBlockType().getDefaultState();
			} else {
				state = block.getBlockType().getStateFromMeta(block.getMetadata());
			}
			br.renderBlockBrightness(state, 0.75F);
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen, int mouseX, int mouseY) {}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc, GuiManualChapter screen) {}

}
