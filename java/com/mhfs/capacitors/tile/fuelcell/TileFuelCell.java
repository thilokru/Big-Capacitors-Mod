package com.mhfs.capacitors.tile.fuelcell;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.misc.Helper;
import com.mhfs.capacitors.misc.IRotatable;
import com.mhfs.capacitors.tile.TileTower;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileFuelCell extends TileEntity implements IFluidHandler, IEnergyReceiver, IRotatable, ITickable{
	
	private int energy;
	private FluidTank water;
	
	public final static int MAX_ENERGY = 120000, MAX_TRANSFER = 800;
	
	public TileFuelCell(){
		water = new FluidTank(2000);
	}
	
	public void update(){
		if(worldObj.isRemote)return;
		if(!isFormed())return;
		long en = Math.min(80, energy);
		FluidStack wa = water.drain(1, false);
		if(en == 80 && wa != null && wa.amount == 1){
			energy -= 80;
			water.drain(1, true);
			FluidStack hydrogen = new FluidStack(Fluids.gasHydrogen, 10);
			EnumFacing rot = getRotation();
			EnumFacing tankSide = rot.rotateYCCW();
			TileTower tower = (TileTower) worldObj.getTileEntity(pos.offset(tankSide));
			tower.condense(hydrogen, 1);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}
	
	private boolean isFormed() {
		EnumFacing rot = getRotation();
		if(rot == null)return false;
		BlockPos tank1 = this.pos.offset(rot.rotateYCCW());
		boolean formed = checkTower(tank1);
		
		BlockPos tank2 = this.pos.offset(rot.rotateY());
		return formed && checkTower(tank2);
	}
	
	private boolean checkTower(BlockPos pos){
		boolean formed = true;
		formed = formed && worldObj.getBlockState(pos).getBlock().equals(Blocks.blockTower);
		pos = pos.offset(EnumFacing.UP);
		formed = formed && worldObj.getBlockState(pos).getBlock().equals(Blocks.blockTower);
		pos = pos.offset(EnumFacing.UP);
		formed = formed && !(worldObj.getBlockState(pos).getBlock().equals(Blocks.blockTower));
		return formed;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.water.readFromNBT(tag.getCompoundTag("water"));
		this.energy = tag.getInteger("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound waterNBT = new NBTTagCompound();
		water.writeToNBT(waterNBT);
		tag.setTag("water", waterNBT);
		
		tag.setLong("energy", energy);
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.pos, 1, tag);
	}

	@Override
	public EnumFacing getRotation() {
		try{
			return ((BlockFuelCell) this.blockType).getOrientation(worldObj, this.pos);
		}catch(NullPointerException npe){
			return null;
		}
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(!canFill(from, resource.getFluid()))return 0;
		return water.fill(resource, doFill);
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
		return from == EnumFacing.UP && fluid.equals(Fluids.fluidDestilledWater);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[]{water.getInfo()};
	}

	public FluidTank getInputTank() {
		return water;
	}


	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return MAX_ENERGY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == EnumFacing.DOWN;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int amount = Math.min(MAX_TRANSFER, Math.min(MAX_ENERGY - energy, maxReceive));
		if(!simulate){
			energy += amount;
			this.markDirty();
			worldObj.markBlockForUpdate(pos);
		}
		return amount;
	}

	public boolean onBlockActivated(EntityPlayer player) {
		boolean holdingContainer = Helper.isHoldingContainer(player);
		if(!holdingContainer)return false;
		if (Helper.checkBucketFill(player, water)) {
			this.markDirty();
			worldObj.markBlockForUpdate(pos);
		}
		return true;
	}

}
