package com.mhfs.capacitors.tile;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockMachineController;
import com.mhfs.capacitors.misc.Helper;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TileCrusherController extends AdvTileEntity implements IEnergyReceiver, ITickable {

	public final static int OPERATION_ENERGY_COST = 80;
	public final static int ENERGY_STORAGE_CAPACITY = 50000;
	
	public final static ImmutableMap<Item, ItemStack> specialProducts = ImmutableMap.<Item, ItemStack>builder()
										.put(Item.getItemFromBlock(Blocks.COAL_ORE), new ItemStack(Items.COAL, 2))
										.put(Item.getItemFromBlock(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL))
										.put(Item.getItemFromBlock(Blocks.GRAVEL), new ItemStack(Blocks.SAND))
										.put(Item.getItemFromBlock(Blocks.SANDSTONE), new ItemStack(Blocks.SAND, 4))
										.put(Item.getItemFromBlock(Blocks.GRASS), new ItemStack(Blocks.DIRT)).build();
	
	private EnumFacing facing;
	private int energy;

	public TileCrusherController() {}

	public void update() {
		if(!isMultiblockComplete())
			return;
		AxisAlignedBB boundingBox = getItemCrushingBoundingBox();
		List<EntityItem> entities = getWorld().getEntitiesWithinAABB(EntityItem.class, boundingBox);
		if(getWorld().isRemote)return;
		if(energy < OPERATION_ENERGY_COST)return;
		for(EntityItem entity : entities){
			ItemStack stack = entity.getEntityItem();
			String oreDictEntry = OreDictionary.getOreName(OreDictionary.getOreIDs(stack)[0]);
			String rawName = oreDictEntry.replaceFirst("ore", "");
			
			ItemStack result = null;
			if(specialProducts.containsKey(stack.getItem())){
				result = specialProducts.get(stack.getItem()).copy();
			}else{
				String gem = "gem" + rawName;
				List<ItemStack> stacks = OreDictionary.getOres(gem);
				if(stacks.size() == 0){
					String dust = "dust" + rawName;
					stacks = OreDictionary.getOres(dust);
				}
				if(stacks.size() != 0){
					result = stacks.get(0).copy();
					result.stackSize = 2;
				}
			}
			
			if(result != null){
				IItemHandler handler = getOutputInventory();
				ItemStack checkStack = result.copy();
				for(int i = 0; i < handler.getSlots(); i++){
					checkStack = handler.insertItem(i, checkStack, true);
					if(checkStack == null)break;
				}
				if(checkStack == null){
					for(int i = 0; i < handler.getSlots(); i++){
						result = handler.insertItem(i, result, false);
						if(result == null)break;
					}
					entity.getEntityItem().stackSize -= 1;
					if(entity.getEntityItem().stackSize <= 0){
						entity.setDead();
					}
					energy -= OPERATION_ENERGY_COST;
					this.sendUpdate();
					break;
				}
			}
		}
	}
	
	private AxisAlignedBB getItemCrushingBoundingBox(){
		BlockPos bbStart = new BlockPos(Helper.rotateVector(new Vec3i(0, 3, -3), facing)).add(getPos());
		BlockPos bbEnd = new BlockPos(Helper.rotateVector(new Vec3i(2, 1, -1), facing)).add(getPos());
		return new AxisAlignedBB(bbStart, bbEnd);
	}
	
	private IItemHandler getOutputInventory(){
		Vec3i toOutput = new Vec3i(2, 0, -2);
		toOutput = Helper.rotateVector(toOutput, facing);
		BlockPos target = getPos().add(toOutput);
		TileEntity te = getWorld().getTileEntity(target);
		return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
	}
	
	private boolean isMultiblockComplete(){
		if(facing == null){
			facing = BigCapacitorsMod.instance.crusherMulti.getCompletedRotation(getPos(), getWorld());
			if(facing == null){
				selfDestruct();
				return false;
			}
		}
		if (!BigCapacitorsMod.instance.crusherMulti.complete(getPos(), getWorld(), facing)) {
			selfDestruct();
			return false;
		}
		return true;
	}
	
	private void selfDestruct(){
		getWorld().removeTileEntity(getPos());
		IBlockState state = getWorld().getBlockState(getPos());
		state.withProperty(BlockMachineController.USED_TE, 0);
		getWorld().setBlockState(getPos(), state);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(facing != null)
			tag.setInteger("rotation", facing.getIndex());
		tag.setInteger("energy", energy);
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("rotation")){
			this.facing = EnumFacing.getFront(tag.getInteger("rotation"));
		}else{
			this.facing = null;
		}
		this.energy = tag.getInteger("energy");
	}

	public void setMultiblockRotation(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return ENERGY_STORAGE_CAPACITY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int receive = Math.min(maxReceive, ENERGY_STORAGE_CAPACITY - energy);
		if(!simulate){
			this.energy += receive;
		}
		return receive;
	}

}
