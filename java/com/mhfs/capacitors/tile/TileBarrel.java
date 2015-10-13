package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileBarrel extends TileEntity implements ISidedInventory, IFluidHandler{
	
	private final static int maxProgress = 1000;
	
	private ItemStack potatoStack;
	private boolean processing;
	private int progress;
	private FluidTank wineTank;
	
	public TileBarrel(){
		wineTank = new FluidTank(BigCapacitorsMod.instance.fluidWine, 0, 2000);
	}
	
	@Override
	public void updateEntity(){
		if(worldObj.isRemote)return;
		if(this.processing){
			progress++;
			if(progress == maxProgress){
				this.progress = 0;
				this.processing = false;
				this.wineTank.fill(new FluidStack(BigCapacitorsMod.instance.fluidWine, 2000), true);
				this.potatoStack = null;
			}
		}
	}
	
	public void onRightClick(World world, EntityPlayer player) {
		if(processing)return;
		if(world.isRemote)return;
		ItemStack stack = player.getHeldItem();
		if(stack.getItem() == Items.potato){
			if(potatoStack == null){
				stack.stackSize--;
				potatoStack = new ItemStack(Items.potato, 1);
			}else{
				if(potatoStack.stackSize < this.getInventoryStackLimit()){
					stack.stackSize--;
					potatoStack.stackSize++;
					if(potatoStack.stackSize == this.getInventoryStackLimit()){
						this.processing = true;
						this.progress = 0;
					}
				}
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot == 0)return potatoStack;
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(stack.getItem() == Items.potato && potatoStack == null){
			potatoStack = stack;
		}
	}

	@Override
	public String getInventoryName() {
		return StatCollector.translateToLocal("tile.barrel.name");
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 16;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() == Items.potato;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int sideID) {
		ForgeDirection side = ForgeDirection.getOrientation(sideID);
		if(side == ForgeDirection.UP){
			return new int[]{0};
		}else{
			return new int[]{};
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideID) {
		if(ForgeDirection.getOrientation(sideID) == ForgeDirection.UP){
			if(stack.getItem() == Items.potato){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int sideID) {
		if(ForgeDirection.getOrientation(sideID) == ForgeDirection.UP){
			if(stack.getItem() == Items.potato){
				return true;
			}
		}
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(from == ForgeDirection.DOWN && resource.getFluid() == wineTank.getFluid().getFluid()){
			return wineTank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(from == ForgeDirection.DOWN){
			return wineTank.drain(maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if(from == ForgeDirection.DOWN){
			return true;
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if(from == ForgeDirection.DOWN){
			return new FluidTankInfo[]{wineTank.getInfo()};
		}
		return new FluidTankInfo[]{};
	}

}
