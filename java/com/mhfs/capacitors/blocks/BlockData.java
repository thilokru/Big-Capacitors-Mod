package com.mhfs.capacitors.blocks;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockData {
	
	public BlockData(String name, String icon, String miningTool, int harvestLevel, float hardness, CreativeTabs tab){
		this.blockName = name;
		this.baseIconName = icon;
		this.miningTool = miningTool;
		this.harvestLevel = harvestLevel;
		this.hardness = hardness;
		this.tab = tab;
	}

	private String blockName;
	private String baseIconName;
	private HashMap<ForgeDirection, String> specialTextures;
	private HashMap<ForgeDirection, IIcon> textures;
	private String miningTool;
	private int harvestLevel;
	private float hardness;
	private CreativeTabs tab;
	
	public String getName(){
		return blockName;
	}
	
	public void setSpecialTexture(ForgeDirection side, String texture){
		if(specialTextures == null){
			specialTextures = new HashMap<ForgeDirection, String>();
		}
		specialTextures.put(side, texture);
	}
	
	public void registerTextures(IIconRegister register){
		textures = new HashMap<ForgeDirection, IIcon>();
		for(ForgeDirection dir:ForgeDirection.VALID_DIRECTIONS){
			if(specialTextures != null && specialTextures.containsKey(dir)){
				textures.put(dir, register.registerIcon(specialTextures.get(dir)));
			}else{
				textures.put(dir, register.registerIcon(baseIconName));
			}
		}
	}
	
	public String getIconName(int side){
		ForgeDirection face = ForgeDirection.getOrientation(side);
		if(specialTextures != null && specialTextures.containsKey(face)){
			return specialTextures.get(side);
		}else{
			return baseIconName;
		}
	}
	
	public IIcon getIcon(int side){
		ForgeDirection face = ForgeDirection.getOrientation(side);
		return textures.get(face);
	}
	
	public String getMiningTool(){
		return miningTool;
	}
	
	public int getHarvestLevel(){
		return harvestLevel;
	}
	
	public float getHardness(){
		return hardness;
	}
	
	public CreativeTabs getTab(){
		return tab;
	}
}
