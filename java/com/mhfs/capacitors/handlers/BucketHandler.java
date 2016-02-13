package com.mhfs.capacitors.handlers;

import java.util.HashMap;

import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class BucketHandler {

	public final static HashMap<Block, Item> FLUID_BLOCK_TO_BUCKET = new HashMap<Block, Item>();
	
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
		ItemStack result = checkFill(event.world, event.target);
		
		if(result != null){
			event.result = result;
			event.setResult(Result.ALLOW);
		}
	}

	private ItemStack checkFill(World world, MovingObjectPosition target) {
		Block block = world.getBlockState(target.getBlockPos()).getBlock();
		
		Item bucket = FLUID_BLOCK_TO_BUCKET.get(block);
		
		if(bucket != null){
			world.setBlockToAir(target.getBlockPos());
			return new ItemStack(bucket);
		}
		return null;
	}
}
