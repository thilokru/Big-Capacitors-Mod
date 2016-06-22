package com.mhfs.api.lux;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class RoutingStorage implements IStorage<IRouting> {

	@SuppressWarnings("deprecation")
	@Override
	public NBTBase writeNBT(Capability<IRouting> capability, IRouting instance, EnumFacing side) {
		Route[] routes = instance.getEntries();
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("routeCount", routes.length);

		for (int i = 0; i < routes.length; i++) {
			NBTTagCompound subTag = new NBTTagCompound();
			Route route = routes[i];
			subTag.setLong("destination", route.destination.toLong());
			subTag.setLong("nextHop", route.nextHop.toLong());
			subTag.setInteger("distance", route.distance);
			tag.setTag("r" + i, subTag);
		}

		BlockPos[] connections = instance.getConnections();

		tag.setInteger("conCount", connections.length);

		for (int i = 0; i < connections.length; i++) {
			tag.setLong("c" + i, connections[i].toLong());
		}

		return tag;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void readNBT(Capability<IRouting> capability, IRouting instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound compound = (NBTTagCompound)nbt;
		int routeCount = compound.getInteger("routeCount");
		
		for (int i = 0; i < routeCount; i++){
			NBTTagCompound subTag = compound.getCompoundTag("r" + i);
			BlockPos destination = BlockPos.fromLong(subTag.getLong("destination"));
			BlockPos nextHop = BlockPos.fromLong(subTag.getLong("nextHop"));
			int distance = subTag.getInteger("distance");
			instance.addRoutingEntry(new Route(destination, nextHop, distance));
		}
		
		int conCount = compound.getInteger("conCount");
		
		for (int i = 0; i < conCount; i++) {
			instance.addConnection(BlockPos.fromLong(compound.getLong("c" + i)));
		}
	}

}
