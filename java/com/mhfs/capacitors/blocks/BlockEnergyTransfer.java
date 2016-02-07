package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.tile.lux.TileEnergyTransciever;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEnergyTransfer extends BlockContainer implements IOrientedBlock{

	public BlockEnergyTransfer(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEnergyTransciever();
	}

	@Override
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
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEnergyTransciever target = (TileEnergyTransciever) world.getTileEntity(x, y, z);
		target.onDestroy();
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		return side;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderBlockPass() {
		return 1;
	}

	public int getRenderType() {
		return -1;
	}

}
