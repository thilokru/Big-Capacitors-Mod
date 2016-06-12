package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockStirlingEngine;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileStirlingEngine extends TileEntity implements IEnergyProvider, ITickable{
	
	public final static int MAX_ENERGY = 80000;
	public final static int MAX_TRANSFER = 80;
	
	private int energy;
	
	@Override
	public void update() {
		if(worldObj.isRemote)return;
		if(isActive()){
			energy += 40;
			markForUpdate();
		}
	}
	
	protected void markForUpdate(){
		this.markDirty();
		IBlockState state = this.getBlockType().getStateFromMeta(this.getBlockMetadata());
		worldObj.notifyBlockUpdate(this.pos, state, state, 3);
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
		return from == getFacing();
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(canConnectEnergy(from)){
			int extractable = Math.min(energy, Math.min(maxExtract, MAX_TRANSFER));
			if(!simulate){
				energy -= extractable;
			}
			return extractable;
		}
		return 0;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy = tag.getInteger("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
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
	
	private EnumFacing getFacing(){
		try{
			return worldObj.getBlockState(pos).getValue(BlockStirlingEngine.ORIENTATION);
		}catch(NullPointerException npe){
			return null;
		}
	}
	
	private boolean isActive(){
		if(worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == Blocks.LIT_FURNACE){
			TileEntityFurnace furnace = (TileEntityFurnace)worldObj.getTileEntity(pos.offset(EnumFacing.DOWN));
			ItemStack todo = furnace.getStackInSlot(0);
			return todo == null || todo.stackSize == 0;
		}else if(worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == Blocks.FURNACE){
			TileEntityFurnace furnace = (TileEntityFurnace)worldObj.getTileEntity(pos.offset(EnumFacing.DOWN));
			int burnTime = TileEntityFurnace.getItemBurnTime(furnace.getStackInSlot(1));
			if(burnTime <= 0)return false;
			furnace.setField(0, burnTime);//furnaceBurnTime: How long the furnace will be lit.
			furnace.setField(1, burnTime);//currentBurnTime: How long the current item would burn in total.
			furnace.setField(2, 1);//cookTime: the update method needs it.
			ItemStack fuelStack = furnace.getStackInSlot(1);
			fuelStack.stackSize--;
			if(fuelStack.stackSize == 0){
				furnace.setInventorySlotContents(1, fuelStack.getItem().getContainerItem(fuelStack));
			}
			BlockFurnace.setState(true, this.worldObj, furnace.getPos());
			furnace.markDirty();
		}
		return false;
	}

}
