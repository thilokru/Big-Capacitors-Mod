package com.mhfs.capacitors.misc;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class HashSetHelper {

	public static NBTTagCompound blockPosSetToNBT(Set<BlockPos> containedBlocks){
		NBTTagCompound tag = new NBTTagCompound();
		BlockPos[] entries = containedBlocks.toArray(new BlockPos[0]);
		for(int i = 0; i < entries.length; i++){
			BlockPos pos = entries[i];
			tag.setIntArray(i + "", new int[]{pos.getX(), pos.getY(), pos.getZ()});
		}
		tag.setInteger("size", entries.length);
		return tag;
	}
	
	public static HashSet<BlockPos> nbtToBlockPosSet(NBTTagCompound tag){
		BlockPos[] entries = new BlockPos[tag.getInteger("size")];
		for(int i = 0; i < entries.length; i++){
			int[] raw = tag.getIntArray(i + "");
			entries[i] = new BlockPos(raw[0], raw[1], raw[2]);
		}
		HashSet<BlockPos> set = new HashSet<BlockPos>();
		set.addAll(Arrays.asList(entries));
		return set;
	}
	
	public static void writeBlockPosSetToByteBuffer(ByteBuf buf, HashSet<BlockPos> set){
		BlockPos[] entries = set.toArray(new BlockPos[0]);
		buf.writeInt(entries.length);
		for(int i = 0; i < entries.length; i++){
			BlockPos pos = entries[i];
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
		}
	}
	
	public static HashSet<BlockPos> readBlockPosSetFromByteBuffer(ByteBuf buf){
		int size = buf.readInt();
		HashSet<BlockPos> ret = new HashSet<BlockPos>();
		for(int i = 0; i < size; i++){
			ret.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
		}
		return ret;
	}

	public static NBTBase intSetToNBT(Set<Integer> linkedWalls) {
		NBTTagCompound tag = new NBTTagCompound();
		Integer[] entries = linkedWalls.toArray(new Integer[0]);
		for(int i = 0; i < entries.length; i++){
			tag.setInteger(i + "", entries[i].intValue());
		}
		tag.setInteger("size", entries.length);
		return tag;
	}
	
	public static HashSet<Integer> nbtToIntSet(NBTTagCompound tag){
		Integer[] entries = new Integer[tag.getInteger("size")];
		for(int i = 0; i < entries.length; i++){
			entries[i] = tag.getInteger(i + "");
		}
		HashSet<Integer> set = new HashSet<Integer>();
		set.addAll(Arrays.asList(entries));
		return set;
	}
}
