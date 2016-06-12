package com.mhfs.capacitors.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public interface IOrientedBlock {

	public EnumFacing getOrientation(IBlockAccess world, BlockPos pos);
}
