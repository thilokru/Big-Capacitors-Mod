package com.mhfs.capacitors.blocks;

import java.util.List;

import com.mhfs.capacitors.misc.IChapterRelated;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMany extends Block implements IChapterRelated{

	private BlockData[] subBlocks;

	public BlockMany(BlockData[] subBlocks) {
		super(Material.rock);
		this.subBlocks = subBlocks;
	}

	public int getDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return subBlocks[meta].getIcon(side);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < subBlocks.length; i++) {
			subBlocks[i].registerTextures(register);
		}
	}

	public float getBlockHardness(World world, int x, int y, int z) {
		return subBlocks[world.getBlockMetadata(x, y, z)].getHardness();
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

	public void injectSubStacks() {
		for (int i = 0; i < subBlocks.length; i++) {
			BlockData sub = subBlocks[i];
			this.setHarvestLevel(sub.getMiningTool(), sub.getHarvestLevel(), i);
			GameRegistry.registerCustomItemStack(sub.getName(), new ItemStack(Item.getItemFromBlock(this), 1, i));
		}
	}

	@Override
	public String getChapter(IBlockAccess access, int x, int y, int z) {
		return subBlocks[access.getBlockMetadata(x, y, z)].getChapter();
	}
}
