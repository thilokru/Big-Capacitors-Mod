package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileTower extends AdvTileEntity {
	
	private FluidTank tank;
	private boolean releasingSteam;
	
	public TileTower(){
		tank = new FluidTank(2000);
	}

	public void condense(FluidStack output, int times) {
		if(isTopMost()){
			releasingSteam = false;
			FluidStack condense = output.copy();
			condense.amount *= times;
			int accepted = tank.fill(condense, true);
			if(accepted != condense.amount){
				releasingSteam = true;
			}
			this.sendUpdate();
		}else{
			TileTower tower = (TileTower) worldObj.getTileEntity(pos.offset(EnumFacing.UP));
			tower.condense(output, times);
		}
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
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
		}
		return super.getCapability(capability, facing);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tank, null, tag.getTag("tank"));
		this.releasingSteam = tag.getBoolean("steam");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(tank, null));
		tag.setBoolean("steam", releasingSteam);
		this.releasingSteam = false;
		return tag;
	}
	
	public boolean isReleasingSteam(){
		return releasingSteam;
	}

	public boolean isTopMost() {
		return !worldObj.getBlockState(getPos().offset(EnumFacing.UP)).getBlock().equals(Blocks.blockTower);
	}
	
	public IFluidTank getTank(){
		return tank;
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack stack) {
		return FluidUtil.interactWithFluidHandler(stack, tank, player);
	}

	public void resetSteamState() {
		this.releasingSteam = false;
	}
}
