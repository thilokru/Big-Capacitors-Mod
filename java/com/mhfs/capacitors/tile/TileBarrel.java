package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Fluids;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileBarrel extends TileEntity implements ISidedInventory, IFluidHandler, ITickable{
	
	private final static int maxProgress = 1000;
	private final static int tankCapacity = 5000;
	
	private ItemStack potatoStack;
	private boolean processing;
	private int progress;
	private FluidTank wineTank;
	
	public TileBarrel(){
		wineTank = new FluidTank(Fluids.fluidWine, 0, tankCapacity);
		potatoStack = new ItemStack(Items.potato, 0);
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
				this.potatoStack = new ItemStack(Items.potato, 0);
			}
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}
	
	public void onRightClick(World world, EntityPlayer player) {
		if(processing)return;
		if(world.isRemote)return;
		ItemStack stack = player.getHeldItem();
		if(stack == null)return;
		if(stack.getItem() == Items.potato){
			if(potatoStack == null){
				stack.stackSize--;
				potatoStack = new ItemStack(Items.potato, 1);
			}else{
				if(potatoStack.stackSize < this.getInventoryStackLimit()){
					stack.stackSize--;
					potatoStack.stackSize++;
				}
			}
			this.markDirty();
			this.worldObj.markBlockForUpdate(this.pos);
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
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(stack.getItem() == Items.potato && potatoStack == null){
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
		return stack.getItem() == Items.potato;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
		if(face == EnumFacing.UP){
			if(stack.getItem() == Items.potato){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
		if(face == EnumFacing.UP){
			if(stack.getItem() == Items.potato){
				return true;
			}
		}
		return false;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(from == EnumFacing.DOWN && resource.getFluid() == wineTank.getFluid().getFluid()){
			return wineTank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(from == EnumFacing.DOWN){
			return wineTank.drain(maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if(from == EnumFacing.DOWN){
			return true;
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		if(from == EnumFacing.DOWN){
			return new FluidTankInfo[]{wineTank.getInfo()};
		}
		return new FluidTankInfo[]{};
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		FluidTank wineTank = new FluidTank(tankCapacity);
		wineTank.readFromNBT(tag.getCompoundTag("wineTank"));
		this.wineTank = wineTank;

		this.potatoStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
		
		this.processing = tag.getBoolean("processing");
		this.progress =  tag.getInteger("progress");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound wineTankTag = new NBTTagCompound();
		wineTank.writeToNBT(wineTankTag);
		tag.setTag("wineTank", wineTankTag);

		NBTTagCompound stackTag = new NBTTagCompound();
		potatoStack.writeToNBT(stackTag);
		tag.setTag("stack", stackTag);
		
		tag.setBoolean("processing", processing);
		tag.setInteger("progress", progress);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.pos, 1, tag);
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
		return StatCollector.translateToLocal("tile.barrel.name");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
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
