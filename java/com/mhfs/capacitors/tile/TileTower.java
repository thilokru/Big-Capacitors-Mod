package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileTower extends TileEntity {
	
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
			markForUpdate();
		}else{
			TileTower tower = (TileTower) worldObj.getTileEntity(pos.offset(EnumFacing.UP));
			tower.condense(output, times);
		}
	}
	
	protected void markForUpdate(){
		this.markDirty();
		IBlockState state = this.getBlockType().getStateFromMeta(this.getBlockMetadata());
		worldObj.notifyBlockUpdate(this.pos, state, state, 3);
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
		return tag;
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 1, tag);
	}
	
	public boolean isReleasingSteam(){
		return releasingSteam;
	}
	
	public void resetSteamState(){
		this.releasingSteam = false;
		markForUpdate();
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
}
