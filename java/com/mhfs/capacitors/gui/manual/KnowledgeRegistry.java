package com.mhfs.capacitors.gui.manual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scala.actors.threadpool.Arrays;

public class KnowledgeRegistry {
	
	static{
		INSTANCE = new KnowledgeRegistry();
	}

	public final static KnowledgeRegistry INSTANCE;
	
	private Map<String, List<IPage>> chapters;
	private List<IPage> index;
	
	private KnowledgeRegistry(){
		chapters = new HashMap<String, List<IPage>>();
	}
	
	public void registerChapter(String name, List<IPage> pages){
		chapters.put(name, pages);
	}
	
	public Set<String> getChapters(){
		return chapters.keySet();
	}
	
	public List<IPage> getChapter(String name){
		return chapters.get(name);
	}
	
	public List<IPage> getIndex(){
		if(index == null){
			index = new ArrayList<IPage>();
			index.add(new LogoPage());
			Set<String> titles = chapters.keySet();
			String[] titleArray = titles.toArray(new String[0]);
			Arrays.sort(titleArray);
			index.add(new IndexPage(titleArray));
		}
		return index;
	}
}
