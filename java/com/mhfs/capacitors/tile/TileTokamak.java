package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.BigCapacitorsMod;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileTokamak extends TileEntity implements IEnergyReceiver, IEnergyProvider, ITickable {

	private int energy;
	private FluidTank hydrogenTank;
	
	private double temperature; //UNIT: °C
	
	private boolean formed;

	private final static int MAX_ENERGY = 5000000;
	private final static int MAX_GAS_VOLUME = 3000;
	private final static int MAX_RF_DRAIN = 800;
	
	private final static double FUSION_START_TEMP = 10000000;
	private final static double ROOM_TEMP = 20;
	private final static double KELVIN_PER_RF = 10;
	private final static double LOSS_FACTOR = 0.00000001D;
	
	private final static int RF_PER_MB_HYDROGEN = 1500000;

	public TileTokamak() {
		hydrogenTank = new FluidTank(MAX_GAS_VOLUME);
		temperature = ROOM_TEMP;
	}

	public void update() {
		formed = checkFormed();
		if(worldObj.isRemote)return;
		if(formed){
			if(temperature >= FUSION_START_TEMP && hydrogenTank.getFluidAmount() > 0){
				FluidStack stack = hydrogenTank.drain(1, true);
				int drain = (stack == null)?0:stack.amount;
				if(drain == 1){
					energy += RF_PER_MB_HYDROGEN;
					if(energy > MAX_ENERGY){
						energy = MAX_ENERGY;
					}
				}
			}else{
				long extract = Math.min(MAX_RF_DRAIN, energy);
				energy -= extract;
				temperature += extract * KELVIN_PER_RF;
				temperature -= (temperature - ROOM_TEMP)*LOSS_FACTOR;
			}
		}else{
			if(temperature > ROOM_TEMP){
				this.temperature = ROOM_TEMP;
			}
			if(energy > 0){
				energy = 0;
			}
		}
		markForUpdate();
	}
	
	protected void markForUpdate(){
		this.markDirty();
		IBlockState state = this.getBlockType().getStateFromMeta(this.getBlockMetadata());
		worldObj.notifyBlockUpdate(this.pos, state, state, 3);
	}
	
	private boolean checkFormed() {
		return BigCapacitorsMod.instance.fusionReactorMulti.complete(this.pos, worldObj);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(hydrogenTank);
		}
		return super.getCapability(capability, facing);
	}

	public boolean isFormed(){
		return formed;
	}
	
	public FluidTank getHydrogenTank(){
		return hydrogenTank;
	}

	
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(hydrogenTank, null, tag.getTag("tank"));
		this.energy = tag.getInteger("energy");
		this.temperature = tag.getDouble("temperature");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(hydrogenTank, null));
		tag.setLong("energy", this.energy);
		tag.setDouble("temperature", this.temperature);
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

	public double getTemperature() {
		return temperature;
	}
	
	public long getEnergyStored(){
		return energy;
	}
	
	public long getMaxEnergyStored(){
		return MAX_ENERGY;
	}
	
	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int)energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return MAX_ENERGY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int amount = Math.min(MAX_ENERGY - energy, maxReceive);
		if(!simulate){
			energy += amount;
		}
		return amount;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		int amount = Math.min(MAX_ENERGY, maxExtract);
		if(!simulate){
			energy -= amount;
		}
		return amount;
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack stack) {
		return FluidUtil.interactWithFluidHandler(stack, hydrogenTank, player);
	}
}
