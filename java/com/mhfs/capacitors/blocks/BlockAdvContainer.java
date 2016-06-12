package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class BlockAdvContainer extends BlockContainer {

	private boolean opaque;

	protected BlockAdvContainer(Material material, String name, boolean opaque) {
		super(material);
		this.setUnlocalizedName(name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);

		this.setRegistryName(new ResourceLocation(BigCapacitorsMod.modid, name));
		GameRegistry.register(this);

		Item item = new ItemBlock(this);
		item.setRegistryName(this.getRegistryName());
		GameRegistry.register(item);

		this.opaque = opaque;
	}

	protected BlockAdvContainer(Material material, String name) {
		this(material, name, false);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return opaque;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

}
