package com.mhfs.capacitors.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPos {

	public int x, y, z;
	
	public BlockPos(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void goTowards(ForgeDirection dir, int steps){
		this.x += dir.offsetX * steps;
		this.y += dir.offsetY * steps;
		this.z += dir.offsetZ * steps;
	}
	
	public BlockPos clone(){
		return new BlockPos(x, y, z);
	}
	
	public Block getBlock(IBlockAccess world){
		return world.getBlock(x, y, z);
	}
	
	public TileEntity getTileEntity(IBlockAccess world){
		if(world == null)System.err.println("Problem!");
		return world.getTileEntity(x, y, z);
	}
	
	public double getDistance(BlockPos pos){
		int deltaX = x - pos.x;
		int deltaY = y - pos.y;
		int deltaZ = z - pos.z;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof BlockPos){
			BlockPos other = (BlockPos)arg0;
			if (this.x == other.x && this.y == other.y && this.z == other.z) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return x ^ y ^ z;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag, String id) {
		tag.setIntArray(id, new int[]{x, y, z});
		return tag;
	}
	
	public static BlockPos fromNBT(NBTTagCompound tag, String id){
		int[] raw = tag.getIntArray(id);
		if(raw.length != 3){
			return null;
		}
		return new BlockPos(raw[0], raw[1], raw[2]);
	}
}
