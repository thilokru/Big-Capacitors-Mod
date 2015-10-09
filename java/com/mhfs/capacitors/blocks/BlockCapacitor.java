package com.mhfs.capacitors.blocks;

import java.util.ArrayList;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.misc.IChapterRelated;
import com.mhfs.capacitors.tile.BlockPos;
import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCapacitor extends Block implements ITileEntityProvider, IChapterRelated, IOrientedBlock {

	private double resistance;
	private Block metal;

	public BlockCapacitor(Material material, double resistance) {
		super(material);
		this.resistance = resistance;
	}

	public double getResistance() {
		return resistance;
	}

	public void setMetal(Block metal) {
		this.metal = metal;
	}

	public Block getMetal() {
		return metal;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		if(BigCapacitorsMod.capacitorRenderer == null){
			return 0;
		}
		return BigCapacitorsMod.capacitorRenderer.getRenderId();
	}

	public ArrayList<BlockPos> getConnectedCapacitors(IBlockAccess world,
			int x, int y, int z) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		ForgeDirection dir = getOrientation(world, x, y, z);
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (direction != dir && direction != dir.getOpposite()) {
				int nX = x + direction.offsetX;
				int nY = y + direction.offsetY;
				int nZ = z + direction.offsetZ;
				Block block = world.getBlock(nX, nY, nZ);
				if (block instanceof BlockCapacitor) {
					if (this.getOrientation(world, nX, nY, nZ) == dir) {
						list.add(new BlockPos(nX, nY, nZ));
					}
				}
			}
		}
		return list;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileCapacitor(false);
	}

	public boolean onBlockEventReceived(World world, int x, int y, int z,
			int i1, int i2) {
		super.onBlockEventReceived(world, x, y, z, i1, i2);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(i1, i2)
				: false;
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
		((TileCapacitor) world.getTileEntity(x, y, z)).onRotate();
		return true;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		return ForgeDirection.getOrientation(side).getOpposite().ordinal();
	}

	@Override
	public String getChapter() {
		return "Capacitor Walls";
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return getAABB(world, x, y, z);
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		AxisAlignedBB aabb = getAABB(world, x, y, z);
		float minX = (float) (aabb.minX - x);
		float minY = (float) (aabb.minY - y);
		float minZ = (float) (aabb.minZ - z);
		float maxX = (float) (aabb.maxX - x);
		float maxY = (float) (aabb.maxY - y);
		float maxZ = (float) (aabb.maxZ - z);
		
		this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
 	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		return getAABB(world, x, y, z);
    }
	
	private AxisAlignedBB getAABB(IBlockAccess world, int x, int y, int z){
		ForgeDirection dir = this.getOrientation(world, x, y, z);
		float minX, minY, minZ, maxX, maxY, maxZ;
		minX = minY = minZ = 0;
		maxX = maxY = maxZ = 1;
		if(dir == ForgeDirection.NORTH){
			maxZ = 0.5F;
		}else if(dir == ForgeDirection.SOUTH){
			minZ = 0.5F;
		}else if(dir == ForgeDirection.WEST){
			maxX = 0.5F;
		}else if(dir == ForgeDirection.EAST){
			minX = 0.5F;
		}else if(dir == ForgeDirection.DOWN){
			maxY = 0.5F;
		}else if(dir == ForgeDirection.UP){
			minY = 0.5F;
		}
        return AxisAlignedBB.getBoundingBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
	}
}
