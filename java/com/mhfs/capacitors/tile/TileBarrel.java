package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Fluids;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileBarrel extends AdvTileEntity implements ISidedInventory, ITickable{
	
	private final static int maxProgress = 1000;
	private final static int tankCapacity = 5000;
	
	private ItemStack potatoStack;
	private boolean processing;
	private int progress;
	private FluidTank wineTank;
	
	public TileBarrel(){
		wineTank = new FluidTank(Fluids.fluidWine, 0, tankCapacity);
		potatoStack = new ItemStack(Items.POTATO, 0);
	}
	
	@Override
	public void update(){
		if(worldObj.isRemote)return;
		if(potatoStack.stackSize == this.getInventoryStackLimit() && !processing){
			this.processing = true;
			this.progress = 0;
		}
		if(this.processing){
			progress++;
			if(progress == maxProgress){
				this.progress = 0;
				this.processing = false;
				this.wineTank.fill(new FluidStack(Fluids.fluidWine, 2000), true);
				this.potatoStack = new ItemStack(Items.POTATO, 0);
			}
		}
		markForUpdate();
	}
	
	public void onRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(processing)return;
		if(world.isRemote)return;
		ItemStack stack = player.getHeldItem(hand);
		if(stack == null)return;
		if(stack.getItem() == Items.POTATO){
			if(potatoStack == null){
				stack.stackSize--;
				potatoStack = new ItemStack(Items.POTATO, 1);
			}else{
				if(potatoStack.stackSize < this.getInventoryStackLimit()){
					stack.stackSize--;
					potatoStack.stackSize++;
				}
			}
			markForUpdate();
		} else {
			FluidUtil.interactWithFluidHandler(stack, wineTank, player);
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == EnumFacing.DOWN){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == EnumFacing.DOWN){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(wineTank);
		}
		return super.getCapability(capability, facing);
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
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(stack.getItem() == Items.POTATO && potatoStack == null){
			potatoStack = stack;
		}
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() == Items.POTATO;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
		if(face == EnumFacing.UP){
			if(stack.getItem() == Items.POTATO){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
		if(face == EnumFacing.UP){
			if(stack.getItem() == Items.POTATO){
				return true;
			}
		}
		return false;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(wineTank, null, tag.getTag("tank"));

		this.potatoStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
		
		this.processing = tag.getBoolean("processing");
		this.progress =  tag.getInteger("progress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(wineTank, null));

		NBTTagCompound stackTag = new NBTTagCompound();
		potatoStack.writeToNBT(stackTag);
		tag.setTag("stack", stackTag);
		
		tag.setBoolean("processing", processing);
		tag.setInteger("progress", progress);
		
		return tag;
	}

	public FluidTank getWineTank() {
		return wineTank;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index == 0){
			return potatoStack;
		}
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {}

	@Override
	public String getName() {
		return I18n.format("tile.barrel.name");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.UP){
			return new int[]{0};
		}else{
			return new int[]{};
		}
	}
}
