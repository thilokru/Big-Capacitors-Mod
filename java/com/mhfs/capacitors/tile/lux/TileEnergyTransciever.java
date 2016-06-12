package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.api.lux.AbstractMonoconnectedRoutingTile;
import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.ILuxDrain;
import com.mhfs.api.lux.ILuxHandler;
import com.mhfs.capacitors.blocks.IOrientedBlock;
import com.mhfs.capacitors.misc.IRotatable;
import com.mhfs.capacitors.render.IConnected;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class TileEnergyTransciever extends AbstractMonoconnectedRoutingTile implements ILuxDrain, IRotatable, IConnected{
	
	private Mode mode = Mode.TRANSCEIVER;
	private boolean isTransmitting = false;
	
	public void update() {
		super.update();
		if (worldObj.isRemote)
			return;
		if (connection == null)
			return;
		IRouting tile = (IRouting) this.worldObj.getTileEntity(connection);
		if (tile == null) {
			connection = null;
			return;
		}
		isTransmitting = false;
		if(mode.isReceiver()){
			tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		}
		if(mode.isTransmitter()){
			ILuxHandler link = (ILuxHandler)this.worldObj.getTileEntity(connection);
			for(BlockPos pos:drains){
				ILuxDrain drain = (ILuxDrain)this.worldObj.getTileEntity(pos);
				if(drain == null || drain == this)continue;
				long energy = getEnergyForTarget(drain.getNeed(), drains.size());
				if(energy != 0){
					isTransmitting = true;
					link.energyFlow(this.getPosition(), pos, energy);
					this.markForUpdate();
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.isTransmitting = tag.getBoolean("transmitting");
		String string = tag.getString("mode");
		if(string.equals("")){
			this.mode = Mode.RECEIVER;
		}else{
			this.mode = Mode.valueOf(string);
		}
	}
	
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setString("mode", mode.toString());
		tag.setBoolean("transmitting", this.isTransmitting);
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
		if(this.connection == null){
			this.markForUpdate();
			return;
		}
		IRouting handler = (IRouting)this.worldObj.getTileEntity(connection);
		if(handler == null)return;
		handler.handleDisconnect(this.getPosition(), 64);
		handler.connect(this.getPosition());
	}
	
	public IEnergyHandler getConnectedTile(){
		if(this.blockType == null)return null;
		EnumFacing direction = getRotation();
		BlockPos tilePos = this.getPosition().offset(direction);
		TileEntity entity = this.worldObj.getTileEntity(tilePos);
		if(entity instanceof IEnergyHandler){
			IEnergyHandler handler = (IEnergyHandler)entity;
			if(handler.canConnectEnergy(direction.getOpposite())){
				return handler;
			}
		}
		return null;
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		if(mode.isReceiver()){
			IEnergyHandler handler = getConnectedTile();
			if(handler == null)return;
			if(handler instanceof IEnergyReceiver){
				IEnergyReceiver receiver = (IEnergyReceiver)handler;
				receiver.receiveEnergy(getRotation().getOpposite(), (int) amount, false);
			}
		}
	}

	@Override
	public long getNeed() {
		IEnergyHandler handler = getConnectedTile();
		if(handler == null)return 0;
		if(handler instanceof IEnergyReceiver){
			IEnergyReceiver receiver = (IEnergyReceiver)handler;
			return receiver.receiveEnergy(getRotation().getOpposite(), Integer.MAX_VALUE, true);
		}
		return 0;
	}

	@Override
	public EnumFacing getRotation() {
		return ((IOrientedBlock)this.blockType).getOrientation(worldObj, this.pos);
	}

	@Override
	public Set<BlockPos> getConnections() {
		Set<BlockPos> set = new HashSet<BlockPos>();
		set.add(this.connection);
		return set;
	}

	@Override
	public Set<BlockPos> getActiveConnections() {
		if(isTransmitting){
			Set<BlockPos> set = new HashSet<BlockPos>();
			set.add(this.connection);
			return set;
		}
		return null;
	}

	@Override
	public void resetConnectionState() {
		this.isTransmitting = false;
		this.markForUpdate();
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

}
