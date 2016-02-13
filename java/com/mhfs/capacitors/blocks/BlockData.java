package com.mhfs.capacitors.blocks;

public class BlockData {
	
	private String blockName;
	private String miningTool;
	private int harvestLevel;
	private float hardness;
	private String chapter;
	
	public BlockData(String name, String miningTool, int harvestLevel, float hardness){
		this.blockName = name;
		this.miningTool = miningTool;
		this.harvestLevel = harvestLevel;
		this.hardness = hardness;
	}
	
	public BlockData(String name, String miningTool, int harvestLevel, float hardness, String chapter){
		this(name, miningTool, harvestLevel, hardness);
		this.chapter = chapter;
	}
	
	public String getName(){
		return blockName;
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

	public String getChapter() {
		return chapter;
	}
}
