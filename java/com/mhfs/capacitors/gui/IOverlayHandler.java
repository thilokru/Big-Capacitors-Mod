package com.mhfs.capacitors.gui;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IOverlayHandler {

	public void drawOverlay(RenderGameOverlayEvent event, Block block, IBlockAccess world, int x, int y, int z);
}
