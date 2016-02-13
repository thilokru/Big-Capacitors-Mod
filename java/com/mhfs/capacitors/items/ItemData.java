package com.mhfs.capacitors.items;

public class ItemData {
	
	private String name;
	private boolean isSpecial;
	
	public ItemData(String name, boolean isSpecial){
		this.name = name;
		this.isSpecial = isSpecial;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isSpecial(){
		return isSpecial;
	}
}
