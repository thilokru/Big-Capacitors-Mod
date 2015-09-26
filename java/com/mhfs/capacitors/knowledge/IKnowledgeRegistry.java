package com.mhfs.capacitors.knowledge;

import java.util.List;
import java.util.Set;

import com.mhfs.capacitors.gui.manual.IPage;

public interface IKnowledgeRegistry {

	public void registerChapter(String name, List<IPage> pages);
	
	public Set<String> getChapters();
	
	public List<IPage> getChapter(String name);
	
	public List<IPage> getIndex();
}
