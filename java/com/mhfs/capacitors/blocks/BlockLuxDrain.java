package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.lux.TileDrain;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLuxDrain extends BlockContainer{

	public BlockLuxDrain(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileDrain();
	}

}
