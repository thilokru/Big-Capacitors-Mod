package com.mhfs.capacitors.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class Helper {

	/**
	 * A helper method to help, when a bucket is right-clicked onto a TileEntity
	 * 
	 * @param player
	 *            the player who is clicking
	 * @param tank
	 *            the tank which should be filled
	 * @return whether changes to the tank were made.
	 */
	public static boolean checkBucketFill(EntityPlayer player, IFluidTank tank) {
		ItemStack stack = player.inventory.getCurrentItem();
		if (stack != null && FluidContainerRegistry.isFilledContainer(stack)) {
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
			if (tank.fill(fluid, false) == fluid.amount) {
				tank.fill(fluid, true);
				if (!player.capabilities.isCreativeMode) {
					stack.stackSize--;
					ItemStack empty = FluidContainerRegistry.drainFluidContainer(stack);
					if (!player.inventory.addItemStackToInventory(empty)) {
						player.dropPlayerItemWithRandomChoice(empty, false);
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * A helper method to help, when a bucket is right-clicked onto a TileEntity
	 * 
	 * @param player
	 *            the player who is clicking
	 * @param tank
	 *            the tank which should be drained.
	 * @return whether changes to the tank were made.
	 */
	public static boolean checkBucketDrain(EntityPlayer player, IFluidTank tank) {
		ItemStack stack = player.inventory.getCurrentItem();
		if(tank.getFluidAmount() == 0)return false;
		if (stack != null && FluidContainerRegistry.isEmptyContainer(stack)) {
			int volume = FluidContainerRegistry.getContainerCapacity(tank.getFluid(), stack);
			FluidStack drain = tank.drain(volume, false);
			if (drain.amount == volume) {
				tank.drain(volume, true);
				player.inventory.consumeInventoryItem(stack.getItem());
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(drain, stack);
				if (!player.inventory.addItemStackToInventory(filled)) {
					player.dropPlayerItemWithRandomChoice(filled, false);
				}
				player.inventory.markDirty();
				player.worldObj.updateEntity(player);
				return true;
			}
		}
		return false;
	}
}
