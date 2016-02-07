package com.mhfs.capacitors.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class DirectedBlock extends Block implements IOrientedBlock{

	protected DirectedBlock(Material mat) {
		super(mat);
	}

	public ForgeDirection getOrientation(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return ForgeDirection.getOrientation(meta);
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z,
			ForgeDirection axis) {
		ForgeDirection dir = getOrientation(world, x, y, z);
		dir = dir.getRotation(axis);
		world.setBlockMetadataWithNotify(x, y, z, dir.ordinal(), 3);
		return true;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		return ForgeDirection.getOrientation(side).getOpposite().ordinal();
	}
}
