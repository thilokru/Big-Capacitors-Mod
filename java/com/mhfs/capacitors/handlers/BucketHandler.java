package com.mhfs.capacitors.handlers;

import java.util.HashMap;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
		Block block = world.getBlock(target.blockX, target.blockY, target.blockZ);
		
		Item bucket = FLUID_BLOCK_TO_BUCKET.get(block);
		
		if(bucket != null && world.getBlockMetadata(target.blockX, target.blockY, target.blockZ) == 0){
			world.setBlockToAir(target.blockX, target.blockY, target.blockZ);
			return new ItemStack(bucket);
		}
		return null;
	}
}
