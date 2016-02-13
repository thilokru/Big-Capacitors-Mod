package com.mhfs.capacitors.gui;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IOverlayHandler {

	/**
	 * @param event The RenderGameOverlayEvent, that caused this method call
	 * @param block The kind of block the player is looking at
	 * @param world The world in which all of this happens.
	 * @param pos the block's location in the world
	 */
	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, BlockPos pos);
}
