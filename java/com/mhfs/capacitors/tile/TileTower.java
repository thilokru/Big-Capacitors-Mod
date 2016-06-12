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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class TileTower extends TileEntity implements IFluidHandler{
	
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
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return resource.getFluid() == tank.getFluid().getFluid()? drain(from, resource.amount, doDrain) : null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		FluidStack ret = tank.drain(maxDrain, doDrain);
		markForUpdate();
		return ret;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return tank.getFluid().getFluid()==fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		this.releasingSteam = tag.getBoolean("steam");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		tag.setBoolean("steam", releasingSteam);
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

	public boolean onBlockActivated(EntityPlayer player, ItemStack stack, EnumFacing side) {
		return FluidUtil.interactWithTank(stack, player, this, side);
	}
}
