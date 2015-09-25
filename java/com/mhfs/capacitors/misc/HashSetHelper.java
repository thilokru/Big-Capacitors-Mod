package com.mhfs.capacitors.misc;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;

import scala.actors.threadpool.Arrays;

import com.mhfs.capacitors.tile.BlockPos;

import net.minecraft.nbt.NBTTagCompound;

public class HashSetHelper {

	public static NBTTagCompound blockPosSetToNBT(HashSet<BlockPos> set){
		NBTTagCompound tag = new NBTTagCompound();
		BlockPos[] entries = set.toArray(new BlockPos[0]);
		for(int i = 0; i < entries.length; i++){
			BlockPos pos = entries[i];
			tag.setIntArray(i + "", new int[]{pos.x, pos.y, pos.z});
		}
		tag.setInteger("size", entries.length);
		return tag;
	}
	
	@SuppressWarnings("unchecked")
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
			buf.writeInt(pos.x);
			buf.writeInt(pos.y);
			buf.writeInt(pos.z);
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
}
