package com.mhfs.capacitors.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IChapterRelated {

	public String getChapter(IBlockAccess world, BlockPos pos);
}
