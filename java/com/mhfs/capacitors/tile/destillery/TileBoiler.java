package com.mhfs.capacitors.tile.destillery;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.tile.TileTower;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileBoiler extends TileEntity implements ITickable, IEnergyReceiver {

	public final static int MAX_RF_PER_TICK = 80;
	public final static int RF_CAPACITY = 15000;
	public final static int TANK_CAPACITY = 2000;

	private FluidTank inputTank;
	private int energy;

	public TileBoiler() {
		this.inputTank = new FluidTank(TANK_CAPACITY);
	}

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		if (checkFormed()) {
			FluidStack fluid = inputTank.getFluid();
			if (fluid != null) {
				DestilleryRecipe recipe = DestilleryRecipeRegistry.forInput(fluid.getFluid());
				if (recipe != null) {
					int cost = recipe.getRFCost();
					int elecTimes = Math.min(energy, MAX_RF_PER_TICK) / cost;
					int fluidTimes = inputTank.getFluidAmount() / recipe.getInput().amount;
					int times = Math.min(elecTimes, fluidTimes);
					energy -= cost * times;
					inputTank.drain(recipe.getInput().amount * times, true);
					TileTower tank = (TileTower) worldObj.getTileEntity(this.getPos().offset(EnumFacing.UP, 2));
					tank.condense(recipe.getOutput(), times);
					markForUpdate();
				}
			}
		}
	}
	
	protected void markForUpdate(){
		this.markDirty();
		IBlockState state = this.worldObj.getBlockState(this.getPos());
		worldObj.notifyBlockUpdate(this.pos, state, state, 3);
	}

	private boolean checkFormed() {
		BlockPos working = this.pos.offset(EnumFacing.UP);
		Block test = worldObj.getBlockState(working).getBlock();
		if (test.equals(Blocks.blockTower)) {
			working = working.offset(EnumFacing.UP);
			test = worldObj.getBlockState(working).getBlock();
			if (test.equals(Blocks.blockTower)) {
				return true;
			}
		}
		return false;
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
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank);
		}
		return super.getCapability(capability, facing);
	}	

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return RF_CAPACITY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int free = RF_CAPACITY - energy;
		int receive = Math.min(maxReceive, free);
		if (!simulate) {
			energy += receive;
		}
		markForUpdate();
		return receive;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(inputTank, null, tag.getTag("tank"));

		this.energy = tag.getInteger("energy");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(inputTank, null));

		tag.setInteger("energy", energy);
		
		return tag;
	}
	
	public NBTTagCompound getUpdateTag(){
		return this.writeToNBT(super.getUpdateTag());
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 1, tag);
	}

	public IFluidTank getInputTank() {
		return inputTank;
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack stack) {
		return FluidUtil.interactWithFluidHandler(stack, inputTank, player);
	}
}
