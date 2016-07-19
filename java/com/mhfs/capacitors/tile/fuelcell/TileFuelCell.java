package com.mhfs.capacitors.tile.fuelcell;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.misc.IRotatable;
import com.mhfs.capacitors.tile.AdvTileEntity;
import com.mhfs.capacitors.tile.TileTower;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileFuelCell extends AdvTileEntity implements IEnergyReceiver, IRotatable, ITickable{
	
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
		markForUpdate();
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
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(water);
		}
		return super.getCapability(capability, facing);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(water, null, tag.getTag("tank"));
		this.energy = tag.getInteger("energy");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(water, null));
		
		tag.setLong("energy", energy);
		return tag;
	}

	@Override
	public EnumFacing getRotation() {
		try{
			return ((BlockFuelCell) this.blockType).getOrientation(worldObj, this.pos);
		}catch(NullPointerException npe){
			return null;
		}
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
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int amount = Math.min(MAX_TRANSFER, Math.min(MAX_ENERGY - energy, maxReceive));
		if(!simulate){
			energy += amount;
			markForUpdate();
		}
		return amount;
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack stack) {
		return FluidUtil.interactWithFluidHandler(stack, water, player);
	}
}
