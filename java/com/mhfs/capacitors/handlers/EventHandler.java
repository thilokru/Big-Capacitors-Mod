package com.mhfs.capacitors.handlers;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.network.ConfigUpdateMessage;
import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class EventHandler {

	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		BigCapacitorsMod.instance.dielectricities = BigCapacitorsMod.instance.dielectricitiesFromConfig;
		BigCapacitorsMod.instance.voltages = BigCapacitorsMod.instance.voltagesFromConfig;
	}

	@SubscribeEvent
	public void handleJoin(PlayerEvent.PlayerLoggedInEvent event) {
		BigCapacitorsMod.instance.network.sendTo(new ConfigUpdateMessage(BigCapacitorsMod.instance.dielectricitiesFromConfig, BigCapacitorsMod.instance.voltagesFromConfig), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void handleConfigChange(ConfigChangedEvent event) {
		BigCapacitorsMod.proxy.loadDielectricities(BigCapacitorsMod.instance);
		BigCapacitorsMod.instance.config.save();
	}

	@SubscribeEvent
	public void handleBlockBreak(BreakEvent event) {
		if (event.getWorld().isRemote)
			return;
		TileEntity entity = event.getWorld().getTileEntity(event.getPos());
		if (entity != null && entity instanceof TileCapacitor) {
			((TileCapacitor) entity).onBreak(event);
		}
	}
}
