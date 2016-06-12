package com.mhfs.capacitors.blocks;

import java.util.ArrayList;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.misc.IChapterRelated;
import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockCapacitor extends Block implements ITileEntityProvider, IChapterRelated, IOrientedBlock {

	public static final PropertyDirection ORIENTATION = PropertyDirection.create("orientation");
	
	public final static String name = "blockCapacitor";

	private double resistance;
	private Block metal;

	public BlockCapacitor(Material material, double resistance) {
		super(material);
		this.setUnlocalizedName(name);
		this.setRegistryName(BigCapacitorsMod.modid, name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		GameRegistry.register(this);
		
		Item item = new ItemBlock(this);
		item.setRegistryName(this.getRegistryName());
		GameRegistry.register(item);
		
		this.resistance = resistance;
		this.setDefaultState(this.blockState.getBaseState().withProperty(ORIENTATION, EnumFacing.DOWN));
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
	
	public String getName(){
		return "capacitor";
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public ArrayList<BlockPos> getConnectedCapacitors(IBlockAccess world, BlockPos pos) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		EnumFacing dir = getOrientation(world, pos);
		for (EnumFacing direction : EnumFacing.VALUES) {
			if (direction != dir && direction != dir.getOpposite()) {
				BlockPos work = pos.offset(direction);
				Block block = world.getBlockState(work).getBlock();
				if (block instanceof BlockCapacitor) {
					if (this.getOrientation(world, work) == dir) {
						list.add(work);
					}
				}
			}
		}
		return list;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileCapacitor();
		te.setWorldObj(world);
		return te;
	}

	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam) {
		super.onBlockEventReceived(world, pos, state, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
	}

	public EnumFacing getOrientation(IBlockAccess world, BlockPos pos) {
		if(world.getBlockState(pos).getProperties().containsKey(ORIENTATION)){
			return world.getBlockState(pos).getValue(ORIENTATION);
		}
		return EnumFacing.NORTH;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		EnumFacing dir = getOrientation(world, pos);
		dir = dir.rotateAround(axis.getAxis());
		IBlockState newState = world.getBlockState(pos).withProperty(ORIENTATION, dir);
		world.setBlockState(pos, newState);
		((TileCapacitor) world.getTileEntity(pos)).onRotate();
		return true;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(ORIENTATION, facing.getOpposite());
	}

	@Override
	public String getChapter(IBlockAccess access, BlockPos pos) {
		return "Capacitor Walls";
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing dir = state.getValue(ORIENTATION);
		float minX, minY, minZ, maxX, maxY, maxZ;
		minX = minY = minZ = 0;
		maxX = maxY = maxZ = 1;
		if (dir == EnumFacing.NORTH) {
			maxZ = 0.5F;
		} else if (dir == EnumFacing.SOUTH) {
			minZ = 0.5F;
		} else if (dir == EnumFacing.WEST) {
			maxX = 0.5F;
		} else if (dir == EnumFacing.EAST) {
			minX = 0.5F;
		} else if (dir == EnumFacing.DOWN) {
			maxY = 0.5F;
		} else if (dir == EnumFacing.UP) {
			minY = 0.5F;
		}
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ORIENTATION, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ORIENTATION).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ORIENTATION });
	}
}
