package com.mhfs.capacitors.render;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.blocks.BlockCapacitor;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

public class RendererCapacitor implements ISimpleBlockRenderingHandler,
		IItemRenderer {

	private int renderID;

	public RendererCapacitor(int id) {
		this.renderID = id;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if (block instanceof BlockCapacitor) {
			BlockCapacitor cap = (BlockCapacitor) block;
			ForgeDirection orientation = cap.getOrientation(world, x, y, z);
			renderBlock(orientation, renderer, cap, x, y, z);
			return true;
		}
		return false;
	}

	private void renderBlock(ForgeDirection orientation, RenderBlocks renderer,
			BlockCapacitor cap, int x, int y, int z) {
		GL11.glPushMatrix();
		renderer.partialRenderBounds = false;
		renderer.renderAllFaces = true;
		switch (orientation) {
		case NORTH:
			renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 0.25D);
			break;
		case SOUTH:
			renderer.setRenderBounds(0D, 0D, 0.75D, 1D, 1D, 1D);
			break;
		case EAST:
			renderer.setRenderBounds(0.75D, 0D, 0D, 1D, 1D, 1D);
			break;
		case WEST:
			renderer.setRenderBounds(0D, 0D, 0D, 0.25D, 1D, 1D);
			break;
		case UP:
			renderer.setRenderBounds(0D, 0.75D, 0D, 1D, 1D, 1D);
			break;
		case DOWN:
			renderer.setRenderBounds(0D, 0D, 0D, 1D, 0.25D, 1D);
			break;
		default:
			renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 0.25D);
			break;
		}
		renderer.renderStandardBlock(cap.getMetal(), x, y, z);

		switch (orientation) {
		case NORTH:
			renderer.setRenderBounds(0D, 0D, 0.25D, 1D, 1D, 0.5D);
			break;
		case SOUTH:
			renderer.setRenderBounds(0D, 0D, 0.5D, 1D, 1D, 0.75D);
			break;
		case EAST:
			renderer.setRenderBounds(0.5D, 0D, 0D, 0.75D, 1D, 1D);
			break;
		case WEST:
			renderer.setRenderBounds(0.25D, 0D, 0D, 0.5D, 1D, 1D);
			break;
		case UP:
			renderer.setRenderBounds(0D, 0.5D, 0D, 1D, 0.75D, 1D);
			break;
		case DOWN:
			renderer.setRenderBounds(0D, 0.25D, 0D, 1D, 0.5D, 1D);
			break;
		default:
			renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 0.25D);
			break;
		}
		renderer.renderStandardBlock(BigCapacitorsMod.getInsulator(), x, y, z);

		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
		case ENTITY:
		case EQUIPPED_FIRST_PERSON:
		case EQUIPPED:
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY:
			renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f);
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			renderItem((RenderBlocks) data[0], item, 0.5f, 0.5f, 0.5f);
			break;
		case INVENTORY:
			renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f);
			break;
		default:
			renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f);
		}
	}

	private void renderItem(RenderBlocks renderer, ItemStack itemStack,
			float x, float y, float z) {
		Tessellator tess = Tessellator.instance;
		BlockCapacitor cap = (BlockCapacitor) Block.getBlockFromItem(itemStack
				.getItem());

		IIcon metalIcon = cap.getMetal().getIcon(0, 0);
		IIcon insulatorIcon = BigCapacitorsMod.getInsulator().getIcon(0, 0);

		Block metal = cap.getMetal();
		Block insulator = BigCapacitorsMod.getInsulator();

		metal.setBlockBounds(0F, 0F, 0.25F, 1F, 1F, 0.5F);
		insulator.setBlockBounds(0, 0, 0, 1F, 1F, 0.25F);

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		//Draw Insulator
		renderer.setRenderBoundsFromBlock(insulator);
		
		tess.startDrawingQuads();
		tess.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(insulator, x, y, z, insulatorIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(insulator, x, y, z, insulatorIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(insulator, x, y, z, insulatorIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(insulator, x, y, z, insulatorIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(insulator, x, y, z, insulatorIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(insulator, x, y, z, insulatorIcon);
		tess.draw();
		
		//Draw Metal
		renderer.setRenderBoundsFromBlock(metal);

		tess.startDrawingQuads();
		tess.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(metal, x, y, z, metalIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(metal, x, y, z, metalIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(metal, x, y, z, metalIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(metal, x, y, z, metalIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(metal, x, y, z, metalIcon);
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(metal, x, y, z, metalIcon);
		tess.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		metal.setBlockBounds(0, 0, 0, 1, 1, 1);
		insulator.setBlockBounds(0, 0, 0, 1, 1, 1);
		renderer.setRenderBoundsFromBlock(metal);
	}
}
