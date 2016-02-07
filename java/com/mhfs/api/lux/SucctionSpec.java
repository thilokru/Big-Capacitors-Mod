package com.mhfs.api.lux;

import com.mhfs.capacitors.misc.BlockPos;

public class SucctionSpec {
	
	public BlockPos lastHop;
	public int sucction;
	
	public SucctionSpec(BlockPos hop, int value) {
		this.sucction = value;
		this.lastHop = hop;
	}
}
