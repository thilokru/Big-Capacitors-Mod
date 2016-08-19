package com.mhfs.api.manual.knowledge;

import java.util.List;
import java.util.Set;

import com.mhfs.api.manual.util.IPage;

import net.minecraft.util.ResourceLocation;

public interface IManual {

	public void registerChapter(String name, List<IPage> pages);
	
	public Set<String> getChapters();
	
	public List<IPage> getChapter(String name);
	
	public List<IPage> getIndex();
	
	public ResourceLocation getTextureLocation();
}
