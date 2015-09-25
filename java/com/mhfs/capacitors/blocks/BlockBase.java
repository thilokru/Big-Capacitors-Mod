package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.misc.IChapterRelated;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block implements IChapterRelated{

	private String chapter;
	
	public BlockBase(Material mat) {
		super(mat);
	}

	@Override
	public String getChapter() {
		return chapter;
	}
	
	public void setChapter(String chapter){
		this.chapter = chapter;
	}

}
