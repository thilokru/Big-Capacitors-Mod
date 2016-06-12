package com.mhfs.capacitors.handlers;

import java.util.HashMap;

import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class BucketHandler {

	public final static HashMap<Block, Item> FLUID_BLOCK_TO_BUCKET = new HashMap<Block, Item>();
	
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
		ItemStack bucket = checkFill(event.getWorld(), event.getTarget());
		
		if(bucket != null){
			event.setFilledBucket(bucket);
			event.setResult(Result.ALLOW);
		}
	}

	private ItemStack checkFill(World world, RayTraceResult target) {
		Block block = world.getBlockState(target.getBlockPos()).getBlock();
		
		Item bucket = FLUID_BLOCK_TO_BUCKET.get(block);
		
		if(bucket != null){
			world.setBlockToAir(target.getBlockPos());
			return new ItemStack(bucket);
		}
		return null;
	}
}
