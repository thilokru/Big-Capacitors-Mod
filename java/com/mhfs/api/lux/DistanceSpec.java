package com.mhfs.api.lux;

import net.minecraft.util.math.BlockPos;

public class DistanceSpec {
	
	public BlockPos nextHop;
	public int distance;
	
	public DistanceSpec(BlockPos hop, int distance) {
		this.distance = distance;
		this.nextHop = hop;
	}
}
