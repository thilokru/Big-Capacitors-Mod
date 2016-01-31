package com.mhfs.capacitors.gui;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IOverlayHandler {

	/**
	 * @param event The RenderGameOverlayEvent, that caused this method call
	 * @param block The kind of block the player is looking at
	 * @param world The world in which all of this happens.
	 * @param x The block's x-coordinate 
	 * @param y The block's y-coordinate
	 * @param z The block's z-coordinate
	 */
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, int x, int y, int z);
}
