package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.destillery.TileTower;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockDestillationTower extends BlockContainer{

	public final static String name = "blockDestillationTower";
	
	public BlockDestillationTower(Material mat) {
		super(mat);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTower();
	}
	
	public int getRenderType() {
		return 3;
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		return getAABB(pos);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return getAABB(pos);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		AxisAlignedBB aabb = getAABB(pos);
		float minX = (float) (aabb.minX - pos.getX());
		float minY = (float) (aabb.minY - pos.getY());
		float minZ = (float) (aabb.minZ - pos.getZ());
		float maxX = (float) (aabb.maxX - pos.getX());
		float maxY = (float) (aabb.maxY - pos.getY());
		float maxZ = (float) (aabb.maxZ - pos.getZ());

		this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}

	private AxisAlignedBB getAABB(BlockPos pos) {
		double sixteenth = 1D/16;
		return new AxisAlignedBB(pos.getX() + sixteenth, pos.getY(), pos.getZ() + sixteenth, pos.getX() + 1 - sixteenth, pos.getY() + 1, pos.getZ() + 1 - sixteenth);
	}

}
