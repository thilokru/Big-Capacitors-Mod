package com.mhfs.capacitors.blocks;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public interface IOrientedBlock {

	public ForgeDirection getOrientation(IBlockAccess world, int x, int y, int z);
}
