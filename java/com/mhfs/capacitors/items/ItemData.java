package com.mhfs.capacitors.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class ItemData {
	
	private String name;
	private String textureName;
	private IIcon icon;
	private boolean isSpecial;
	
	public ItemData(String name, String textureName, boolean isSpecial){
		this.name = name;
		this.textureName = textureName;
		this.isSpecial = isSpecial;
	}
	
	public String getName(){
		return name;
	}
	
	public IIcon getIcon(){
		return icon;
	}
	
	public boolean isSpecial(){
		return isSpecial;
	}
	
	public void loadIcon(IIconRegister register){
		this.icon = register.registerIcon(textureName);
	}
}
