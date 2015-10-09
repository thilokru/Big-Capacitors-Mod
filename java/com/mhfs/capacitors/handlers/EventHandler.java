package com.mhfs.capacitors.handlers;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.network.ConfigUpdateMessage;
import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class EventHandler {

	@SubscribeEvent
	public void handleDespawn(WorldEvent.Unload event) {
		
	}

	@SubscribeEvent
	public void handleSpawn(WorldEvent.Load event) {
		
	}

	@SubscribeEvent
	public void handleTick(WorldTickEvent event) {
		if (event.side != Side.SERVER)
			return;
		if (event.world.isRemote)
			return;
		if (event.phase == TickEvent.Phase.END)
			return;
		if (BigCapacitorsMod.instance.dielectricities == null && event.side == Side.SERVER) {
			BigCapacitorsMod.instance.dielectricities = BigCapacitorsMod.instance.dielectricitiesFromConfig;
		}
		if (BigCapacitorsMod.instance.voltages == null && event.side == Side.SERVER) {
			BigCapacitorsMod.instance.voltages = BigCapacitorsMod.instance.voltagesFromConfig;
		}
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
		if (event.world.isRemote)
			return;
		TileEntity entity = event.world.getTileEntity(event.x, event.y, event.z);
		if (entity != null && entity instanceof TileCapacitor) {
			((TileCapacitor) entity).onBreak(event);
		}
	}
}
