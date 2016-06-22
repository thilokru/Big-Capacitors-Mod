package com.mhfs.api.lux;

import net.minecraft.util.math.BlockPos;

public class Route extends DistanceSpec{
	
	public BlockPos destination;

	public Route(BlockPos destination, BlockPos hop, int value) {
		super(hop, value);
		this.destination = destination;
	}

}
