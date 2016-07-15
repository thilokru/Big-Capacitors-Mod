package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.api.lux.RoutingImpl;
import com.mhfs.api.lux.ILuxHandler;
import com.mhfs.api.lux.LuxAPI;
import com.mhfs.api.lux.LuxHandlerImpl;
import com.mhfs.capacitors.blocks.IOrientedBlock;
import static com.mhfs.capacitors.misc.Helper.markForUpdate;
import com.mhfs.capacitors.render.IConnected;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEnergyTransciever extends TileEntity implements ITickable, IConnected{
	
	private Mode mode = Mode.TRANSCEIVER;
	
	private RoutingImpl routingHandler;
	private LuxHandlerImpl luxHandler;
	
	public TileEnergyTransciever(){
		this.routingHandler = new RoutingImpl(this);
		this.luxHandler = new LuxHandlerImpl(this);
	}
	
	public void update() {
		if (worldObj.isRemote)
			return;
		
		if(mode.isReceiver()){
			handleReceive();
		}
		if(mode.isTransmitter()){
			handleTransmit();
		}			
	}
	
	private void handleTransmit(){
		Set<BlockPos> sinks = routingHandler.getDestinations();
		for(BlockPos pos : sinks){
			TileEntity tile = this.worldObj.getTileEntity(pos);
			
			if(tile.hasCapability(LuxAPI.LUX_FLOW_CAPABILITY, null)){
				ILuxHandler handler = tile.getCapability(LuxAPI.LUX_FLOW_CAPABILITY, null);
				long energy = getEnergyForTarget(handler.getNeed(), sinks.size());
				if(energy != 0){
					luxHandler.energyFlow(null, pos, energy);
					markForUpdate(this);
				}
			}
		}
	}
	
	private void handleReceive(){
		routingHandler.advertiseSink();
		IEnergyHandler handler = getConnectedTile();
		if(handler == null)return;
		if(handler instanceof IEnergyReceiver){
			IEnergyReceiver receiver = (IEnergyReceiver)handler;
			long transmit = receiver.receiveEnergy(getRotation().getOpposite(), Integer.MAX_VALUE, true);
			transmit = Math.min(transmit, this.luxHandler.drain(Long.MAX_VALUE, true));
			while(transmit > 0 && receiver.receiveEnergy(getRotation().getOpposite(), (int)transmit, true) != 0){
				transmit -= this.luxHandler.drain(receiver.receiveEnergy(getRotation().getOpposite(), (int)transmit, false), false);
			}
			luxHandler.setNeed(receiver.receiveEnergy(getRotation().getOpposite(), Integer.MAX_VALUE, true));
		}
		
		
	}
	
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == LuxAPI.LUX_FLOW_CAPABILITY){
			return LuxAPI.LUX_FLOW_CAPABILITY.cast(this.luxHandler);
		}else if(cap == LuxAPI.ROUTING_CAPABILITY){
			return LuxAPI.ROUTING_CAPABILITY.cast(this.routingHandler);
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == LuxAPI.LUX_FLOW_CAPABILITY || cap == LuxAPI.ROUTING_CAPABILITY)
			return true;
		return super.hasCapability(cap, side);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		String string = tag.getString("mode");
		if(string.equals("")){
			this.mode = Mode.RECEIVER;
		}else{
			this.mode = Mode.valueOf(string);
		}
		LuxAPI.ROUTING_CAPABILITY.readNBT(routingHandler, null, tag.getTag("routing"));
		LuxAPI.LUX_FLOW_CAPABILITY.readNBT(luxHandler, null, tag.getTag("lux"));
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setString("mode", mode.toString());
		tag.setTag("routing", LuxAPI.ROUTING_CAPABILITY.writeNBT(routingHandler, null));
		tag.setTag("lux", LuxAPI.LUX_FLOW_CAPABILITY.writeNBT(luxHandler, null));
		this.resetConnectionState();
		return tag;
	}
	
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.pos, 1, tag);
	}
	
	private long getEnergyForTarget(long need, int drainCount) {
		IEnergyHandler handler = getConnectedTile();
		if(handler == null)return 0;
		if(!(handler instanceof IEnergyProvider))return 0;
		IEnergyProvider provider = (IEnergyProvider)handler;
		int maxProvide = provider.extractEnergy(getRotation().getOpposite(), Integer.MAX_VALUE, true);
		int amount = Math.min((int)need, maxProvide/drainCount);
		provider.extractEnergy(getRotation().getOpposite(), amount, false);
		return amount;
	}

	public Mode getMode(){
		return mode;
	}
	
	public void switchMode(){
		mode = mode.getNext();
		this.routingHandler.disadvertiseSink();
		if(mode.isReceiver())this.routingHandler.advertiseSink();
		markForUpdate(this);
	}
	
	public IEnergyHandler getConnectedTile(){
		if(this.blockType == null)return null;
		EnumFacing direction = getRotation();
		BlockPos tilePos = this.getPos().offset(direction);
		TileEntity entity = this.worldObj.getTileEntity(tilePos);
		if(entity instanceof IEnergyHandler){
			IEnergyHandler handler = (IEnergyHandler)entity;
			if(handler.canConnectEnergy(direction.getOpposite())){
				return handler;
			}
		}
		return null;
	}


	public EnumFacing getRotation() {
		return ((IOrientedBlock)this.blockType).getOrientation(worldObj, this.pos);
	}

	@Override
	public Set<BlockPos> getConnections() {
		Set<BlockPos> set = new HashSet<BlockPos>();
		for(BlockPos pos : routingHandler.getConnections()){
			set.add(pos);
		}
		return set;
	}

	@Override
	public Set<BlockPos> getActiveConnections() {
		return luxHandler.getActive();
	}

	@Override
	public void resetConnectionState() {
		this.luxHandler.resetActive();
		markForUpdate(this);
	}
	
	public static enum Mode{
		RECEIVER(true, false) {
			@Override
			public Mode getNext() {
				return TRANSMITTER;
			}
		}, TRANSMITTER(false, true) {
			@Override
			public Mode getNext() {
				return TRANSCEIVER;
			}
		}, TRANSCEIVER(true, true) {
			@Override
			public Mode getNext() {
				return RECEIVER;
			}
		};
		
		private boolean receiver, transmitter;
		
		Mode(boolean receiver, boolean transmitter){
			this.receiver = receiver;
			this.transmitter = transmitter;
		}
		
		public abstract Mode getNext();

		public boolean isReceiver(){
			return receiver;
		}
		
		public boolean isTransmitter(){
			return transmitter;
		}
	}

	public void onDestroy() {
		this.routingHandler.disonnectSink();
	}

}
