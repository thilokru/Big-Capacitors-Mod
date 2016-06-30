package com.mhfs.capacitors.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOverlayHandler {

	private Map<Item, IOverlayHandler> handlers;

	public GuiOverlayHandler() {
		handlers = new HashMap<Item, IOverlayHandler>();
	}

	@SubscribeEvent
	public void handleOverlay(RenderGameOverlayEvent event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && event instanceof RenderGameOverlayEvent.Pre) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			ItemStack stack = player.getHeldItemMainhand();
			if (stack != null) {
				IOverlayHandler handler = handlers.get(stack.getItem());
				if(handler == null)return;
				RayTraceResult thing = Minecraft.getMinecraft().objectMouseOver;
				if(thing == null)return;
				if(thing.typeOfHit != RayTraceResult.Type.BLOCK)return;
				World world = player.worldObj;
				if(world == null)return;
				if(thing.getBlockPos() == null)return;
				Block block = world.getBlockState(thing.getBlockPos()).getBlock();
				handler.drawOverlay(event, block, world, thing.getBlockPos());
			}
		}
	}

	public void registerHandler(Item item, IOverlayHandler handler) {
		handlers.put(item, handler);
	}
}
