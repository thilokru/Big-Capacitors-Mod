package com.mhfs.api.lux;

import net.minecraft.util.BlockPos;

public class SucctionSpec {
	
	public BlockPos lastHop;
	public int sucction;
	
	public SucctionSpec(BlockPos hop, int value) {
		this.sucction = value;
		this.lastHop = hop;
	}
}
