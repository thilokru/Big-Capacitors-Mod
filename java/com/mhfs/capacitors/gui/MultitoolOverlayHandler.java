package com.mhfs.capacitors.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.misc.Helper;
import com.mhfs.capacitors.tile.CapacitorWallWrapper;
import com.mhfs.capacitors.tile.TileBarrel;
import com.mhfs.capacitors.tile.TileCapacitor;
import com.mhfs.capacitors.tile.TileFuelCell;
import com.mhfs.capacitors.tile.TileTomahawk;
import com.mhfs.capacitors.tile.destillery.TileBoiler;
import com.mhfs.capacitors.tile.destillery.TileTower;
import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class MultitoolOverlayHandler extends Gui implements IOverlayHandler {

	private final static ResourceLocation overlayTexture = new ResourceLocation(BigCapacitorsMod.modid, "textures/other/overlay.png");

	private void drawOverlay(RenderGameOverlayEvent event, TileEntity entity) {
		if (entity == null)
			return;
		if (entity instanceof TileCapacitor) {
			renderCapacitorOverlay(event, entity);
		} else if (entity instanceof TileBoiler) {
			renderBoilerOverlay(event, (TileBoiler) entity);
		} else if (entity instanceof TileTower) {
			renderTowerOverlay(event, (TileTower) entity);
		} else if (entity instanceof TileBarrel) {
			renderBarrelOverlay(event, entity);
		} else if (entity instanceof TileTomahawk) {
			renderFusionOverlay(event, (TileTomahawk) entity);
		} else if (entity instanceof TileFuelCell) {
			renderFuelCellOverlay(event, (TileFuelCell) entity);
		} else if (entity instanceof TileEnergyTransciever) {
			renderEnergyTransferOverlay(event, (TileEnergyTransciever) entity);
		}

	}

	private void renderEnergyTransferOverlay(RenderGameOverlayEvent event, TileEnergyTransciever entity) {
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;
		Gui gui = Minecraft.getMinecraft().ingameGUI;
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		String text = entity.getMode().toString();
		gui.drawString(fr, text, xPos + 5, yPos + 5, Color.WHITE.getRGB());
	}

	private void renderFuelCellOverlay(RenderGameOverlayEvent event, TileFuelCell entity) {
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;
		Gui gui = Minecraft.getMinecraft().ingameGUI;

		renderFluidStack(entity.getInputTank(), xPos - 20, yPos + 5);

		float filled = (float) entity.getEnergyStored(null) / (float) entity.getMaxEnergyStored(null);
		renderEnergy(xPos - 3, yPos + 5, filled);

		String text = "H";
		gui.drawString(Minecraft.getMinecraft().fontRendererObj, text, xPos + 10, yPos + 22, Color.WHITE.getRGB());
		renderGas(entity.getHydrogenTank(), xPos + 5, yPos + 5);
	}

	private void renderFusionOverlay(RenderGameOverlayEvent event, TileTomahawk entity) {
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;
		Gui gui = Minecraft.getMinecraft().ingameGUI;

		float filled = (float) ((double) entity.getEnergyStored() / (double) entity.getMaxEnergyStored());
		renderEnergy(xPos - 3, yPos + 5, filled);

		// String text = "Plasma: " + entity.getHydrogenTank().getFluidAmount()
		// + "/" + entity.getHydrogenTank().getCapacity();
		renderGas(entity.getHydrogenTank(), xPos + 8, yPos + 5);

		String text = "at " + (int) entity.getTemperature() + " °C";
		gui.drawString(Minecraft.getMinecraft().fontRendererObj, text, xPos - 3, yPos + 22, Color.WHITE.getRGB());
	}

	private void renderBarrelOverlay(RenderGameOverlayEvent event, TileEntity entity) {
		TileBarrel barrel = (TileBarrel) entity;

		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;

		this.renderItemStack(barrel.getStackInSlot(0), xPos - 18, yPos + 2);
		this.renderFluidStack(barrel.getWineTank(), xPos + 2, yPos + 5);
	}

	private void renderBoilerOverlay(RenderGameOverlayEvent event, TileBoiler entity) {
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;

		renderFluidStack(entity.getInputTank(), xPos + 10, yPos + 5);

		float filled = (float) entity.getEnergyStored(null) / (float) entity.getMaxEnergyStored(null);
		renderEnergy(xPos, yPos + 5, filled);
	}

	private void renderTowerOverlay(RenderGameOverlayEvent event, TileTower entity) {
		if (entity.isTopMost()) {
			int xPos = event.resolution.getScaledWidth() / 2;
			int yPos = event.resolution.getScaledHeight() / 2;
			renderFluidStack(entity.getTank(), xPos + 5, yPos + 5);
		}
	}

	private void renderEnergy(int x, int y, float filled) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);

		bindTexture(overlayTexture);
		this.drawTexturedModalRect(x, y, 55, 0, 7, 16);
		int v = (int) (16 * (1F - filled));
		y += (16 * (1F - filled));
		int height = (int) (16 * filled);
		this.drawTexturedModalRect(x, y, 48, v, 7, height);
		GL11.glPopMatrix();
	}

	private void renderFluidStack(IFluidTank tank, int x, int y) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);

		bindTexture(overlayTexture);
		this.drawTexturedModalRect(x, y, 32, 0, 16, 16);

		FluidStack stack = tank.getFluid();

		if (stack != null) {
			Fluid fluid = stack.getFluid();
			ResourceLocation loc = Helper.getTextureFromFluid(fluid);
			if (loc != null) {
				bindTexture(loc);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
			}
		}

		bindTexture(overlayTexture);
		float filled = tank.getFluidAmount() / (float) tank.getCapacity();
		this.drawTexturedModalRect(x, y, 32, 0, 16, (int)(16 * (1 - filled)));
		this.drawTexturedModalRect(x, y, 16, 0, 16, 16);
		GL11.glPopMatrix();
	}

	private void renderGas(IFluidTank tank, int x, int y) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);

		bindTexture(overlayTexture);
		this.drawTexturedModalRect(x, y, 78, 0, 16, 16);

		float filled = tank.getFluidAmount() / (float) tank.getCapacity();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(2);
		GL11.glColor4f(0, 0, 0, 1);
		Tessellator tes = Tessellator.getInstance();
		WorldRenderer wr = tes.getWorldRenderer();
		wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_TEX);
		double xEnd = x + 8 - Math.cos(filled * Math.PI) * 6;
		double yEnd = y + 8 - Math.sin(filled * Math.PI) * 6;
		wr.pos(xEnd, yEnd, zLevel).color(0, 0, 0, 1).endVertex();
		wr.pos(x + 8, y + 8, zLevel).color(0, 0, 0, 1).endVertex();
		tes.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	private void renderItemStack(ItemStack stack, int x, int y) {
		renderItemStack(stack, x, y, true);
	}

	private void renderItemStack(ItemStack stack, int x, int y, boolean itemCount) {
		RenderItem ri = Minecraft.getMinecraft().getRenderItem();
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		ri.renderItemAndEffectIntoGUI(stack, x, y);

		if (itemCount) {
			String out = stack.stackSize + "";
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			fr.drawStringWithShadow(out, x + 19 - 2 - fr.getStringWidth(out), y + 6 + 3, Color.WHITE.getRGB());
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	private void renderCapacitorOverlay(RenderGameOverlayEvent event, TileEntity entity) {
		TileCapacitor tile = (TileCapacitor) entity;
		Gui gui = Minecraft.getMinecraft().ingameGUI;
		int xPos = event.resolution.getScaledWidth() / 2;
		int yPos = event.resolution.getScaledHeight() / 2;
		if (tile == null || tile.getEntityCapacitor() == null)
			return;
		CapacitorWallWrapper storage = tile.getEntityCapacitor();
		if (storage.isGrounded()) {
			GL11.glEnable(GL11.GL_BLEND);
			bindTexture(overlayTexture);
			this.drawTexturedModalRect(xPos + 5, yPos, 0, 0, 16, 16);
		} else {
			String text = "Energy: " + storage.getEnergyStored() + "/" + storage.getMaxEnergyStored();
			gui.drawString(Minecraft.getMinecraft().fontRendererObj, text, xPos + 5, yPos + 5, Color.WHITE.getRGB());
		}

		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			String text = "Hash: " + storage.hashCode() + " Tile:" + tile.hashCode();
			gui.drawString(Minecraft.getMinecraft().fontRendererObj, text, xPos + 5, yPos + 15, Color.WHITE.getRGB());

			text = "Charge: " + storage.getEnergyStored() + " Cap:" + storage.getMaxEnergyStored();
			gui.drawString(Minecraft.getMinecraft().fontRendererObj, text, xPos + 5, yPos + 25, Color.WHITE.getRGB());

			text = "Grounded: " + storage.isGrounded();
			gui.drawString(Minecraft.getMinecraft().fontRendererObj, text, xPos + 5, yPos + 35, Color.WHITE.getRGB());
		}
	}

	protected void bindTexture(ResourceLocation loc) {
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

	@Override
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, BlockPos pos) {
		drawOverlay(event, world.getTileEntity(pos));
	}
}
