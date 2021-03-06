package com.mhfs.capacitors.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiOverlayHandler {

	Map<Item, IOverlayHandler> handlers;

	public GuiOverlayHandler() {
		handlers = new HashMap<Item, IOverlayHandler>();
	}

	@SubscribeEvent
	public void handleOverlay(RenderGameOverlayEvent event) {
		if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			ItemStack stack = player.getHeldItem();
			if (stack != null) {
				IOverlayHandler handler = handlers.get(stack.getItem());
				if(handler == null)return;
				MovingObjectPosition thing = Minecraft.getMinecraft().objectMouseOver;
				World world = player.worldObj;
				if(world == null)return;
				if(thing == null)return;
				Block block = world.getBlock(thing.blockX, thing.blockY, thing.blockZ);
				handler.drawOverlay(event, block, world, thing.blockX, thing.blockY, thing.blockZ);
			}
		}
	}

	public void registerHandler(Item item, IOverlayHandler handler) {
		handlers.put(item, handler);
	}
}
