package com.mhfs.capacitors.tile.destillery;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.misc.Helper;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class TileBoiler extends TileEntity implements ITickable, IFluidHandler, IEnergyReceiver {

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
					this.markDirty();
					this.worldObj.markBlockForUpdate(pos);
				}
			}
		}
	}

	private boolean checkFormed() {
		BlockPos working = this.pos.offset(EnumFacing.UP);
		Block test = worldObj.getBlockState(working).getBlock();
		if (test.equals(Blocks.blockDestillationTower)) {
			working = working.offset(EnumFacing.UP);
			test = worldObj.getBlockState(working).getBlock();
			if (test.equals(Blocks.blockDestillationTower)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return inputTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return inputTank.getFluid().getFluid() == null ? true : inputTank.getFluid().getFluid().equals(fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { inputTank.getInfo() };
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
		this.markDirty();
		worldObj.markBlockForUpdate(pos);
		return receive;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		inputTank.readFromNBT(tag.getCompoundTag("input"));

		this.energy = tag.getInteger("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound inputTag = new NBTTagCompound();
		inputTank.writeToNBT(inputTag);
		tag.setTag("input", inputTag);

		tag.setInteger("energy", energy);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 1, tag);
	}

	public IFluidTank getInputTank() {
		return inputTank;
	}

	public boolean onBlockActivated(EntityPlayer player) {
		boolean ret = Helper.isHoldingContainer(player);
		if (Helper.checkBucketFill(player, inputTank)) {
			this.markDirty();
			worldObj.markBlockForUpdate(pos);
		}
		return ret;
	}

}
