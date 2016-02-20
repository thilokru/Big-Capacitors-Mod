package com.mhfs.capacitors.blocks;

import java.util.List;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.items.ItemBlockMany;
import com.mhfs.capacitors.misc.IChapterRelated;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMany extends Block implements IChapterRelated{

	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 5);
	
	public final static String name = "blockMany";
	
	private BlockData[] subBlocks;

	public BlockMany(BlockData[] subBlocks) {
		super(Material.rock);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
		this.setUnlocalizedName(name);
		GameRegistry.registerBlock(this, ItemBlockMany.class, name);
		this.subBlocks = subBlocks;
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
	}
	
	private int getID(IBlockState state){
		return state.getValue(TYPE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
	    return getDefaultState().withProperty(TYPE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return state.getValue(TYPE);
	}
	
	@Override
	protected BlockState createBlockState() {
	    return new BlockState(this, new IProperty[] { TYPE });
	}

	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@Override
	public int getHarvestLevel(IBlockState state){
		return subBlocks[getID(state)].getHarvestLevel();
	}
	
	public String getHarvestTool(IBlockState state){
		return subBlocks[getID(state)].getMiningTool();
	}

	public float getBlockHardness(World world, BlockPos pos) {
		return subBlocks[getID(world.getBlockState(pos))].getHardness();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List entries) {
		for (int i = 0; i < subBlocks.length; i++) {
			entries.add(new ItemStack(item, 1, i));
		}
	}

	public String getUnlocalizedSubName(int id) {
		return "tile." + subBlocks[id].getName();
	}

	@Override
	public String getChapter(IBlockAccess access, BlockPos pos) {
		return subBlocks[getID(access.getBlockState(pos))].getChapter();
	}

	public BlockData[] getData() {
		return subBlocks;
	}
}
