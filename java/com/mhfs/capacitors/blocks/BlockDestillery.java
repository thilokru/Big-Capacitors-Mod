package com.mhfs.capacitors.blocks;

import java.util.ArrayList;

import cofh.api.block.IDismantleable;

import com.mhfs.capacitors.misc.IChapterRelated;
import com.mhfs.capacitors.tile.destillery.TileDistillery;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDestillery extends BlockContainer implements IChapterRelated, IOrientedBlock, IDismantleable{

	public BlockDestillery(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileDistillery();
	}
	
	public ForgeDirection getOrientation(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return ForgeDirection.getOrientation(meta);
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z,
			ForgeDirection axis) {
		if(!(axis == ForgeDirection.UP || axis == ForgeDirection.DOWN)){
			axis = ForgeDirection.UP;
		}
		ForgeDirection dir = getOrientation(world, x, y, z);
		dir = dir.getRotation(axis);
		world.setBlockMetadataWithNotify(x, y, z, dir.ordinal(), 3);
		return true;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		ForgeDirection facing = getPlacementOrientation(hitX, hitZ);
		return facing.ordinal();
	}
	
	private ForgeDirection getPlacementOrientation(float hitX, float hitZ){
		double xRelevance = Math.abs(0.5 - hitX);
		double zRelevance = Math.abs(0.5 - hitZ);
		
		//PosZ
		boolean towardsSouth = hitZ > 0.5;
		//PosX
		boolean towardsEast = hitX > 0.5;
		
		if(hitX == 0.5F && hitZ == 0.5F){
			return ForgeDirection.NORTH;
		}
		if(xRelevance > zRelevance){
			if(towardsEast){
				return ForgeDirection.EAST;
			}else{
				return ForgeDirection.WEST;
			}
		}else{
			if(towardsSouth){
				return ForgeDirection.SOUTH;
			}else{
				return ForgeDirection.NORTH;
			}
		}
	}

	@Override
	public String getChapter() {
		return "Distillery";
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player,
			World world, int x, int y, int z, boolean returnDrops) {
		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y,
			int z) {
		// TODO Auto-generated method stub
		return false;
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
