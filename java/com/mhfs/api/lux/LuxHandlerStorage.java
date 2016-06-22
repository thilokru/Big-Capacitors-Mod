package com.mhfs.api.lux;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class LuxHandlerStorage implements IStorage<ILuxHandler>{

	@SuppressWarnings("deprecation")
	@Override
	public NBTBase writeNBT(Capability<ILuxHandler> capability, ILuxHandler instance, EnumFacing side) {
		BlockPos[] active = instance.getActiveConnections();
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("activeCount", active.length);

		for (int i = 0; i < active.length; i++) {
			tag.setLong("a" + i, active[i].toLong());
		}

		return tag;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void readNBT(Capability<ILuxHandler> capability, ILuxHandler instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound)nbt;
		
		int activeCount = tag.getInteger("activeCount");
		
		for (int i = 0; i < activeCount; i++) {
			instance.addActiveConnection(BlockPos.fromLong(tag.getLong("a" + i)));
		}
	}

}
