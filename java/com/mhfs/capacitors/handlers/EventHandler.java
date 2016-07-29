package com.mhfs.capacitors.handlers;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.capabilities.CapabilityMBBean;
import com.mhfs.capacitors.capabilities.IMBBean;
import com.mhfs.capacitors.capabilities.PlayerCapabilityProvider;
import com.mhfs.capacitors.network.ClientGuiEvent;
import com.mhfs.capacitors.network.ConfigUpdateMessage;
import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
	
	@SubscribeEvent
	public void handlePlayerCapabilityGathering(AttachCapabilitiesEvent.Entity event){
		if(event.getEntity() instanceof EntityPlayer){
			event.addCapability(new ResourceLocation(BigCapacitorsMod.modid , "playerCaps"), new PlayerCapabilityProvider());
		}
	}
	
	@SubscribeEvent
	public void handleGuiButtonPress(ClientGuiEvent event) {
		ResourceLocation action = event.getAction();
		if(action.toString().startsWith("big_capacitors:showMB")){
			IMBBean bean = event.getContext().getServerHandler().playerEntity.getCapability(CapabilityMBBean.CAPABILTY_MB_BEAN, null);
			if(action.getResourcePath().startsWith("showMBCrusher")){
				bean.setMB(BigCapacitorsMod.instance.crusherMulti);
				String path = action.getResourcePath();
				int facingID = Integer.parseInt(path.substring(path.length() - 1, path.length()));
				bean.setFacing(EnumFacing.getFront(facingID));
			}else if(action.getResourcePath().equals("showMBFusion")){
				bean.setMB(BigCapacitorsMod.instance.fusionReactorMulti);
				bean.setFacing(EnumFacing.NORTH);
			}
		}
	}
}
