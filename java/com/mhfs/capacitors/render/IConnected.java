package com.mhfs.capacitors.render;

import java.util.Set;

import net.minecraft.util.BlockPos;

public interface IConnected {
	public Set<BlockPos> getConnections();
}
