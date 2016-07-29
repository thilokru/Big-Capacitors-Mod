package com.mhfs.capacitors.knowledge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mhfs.capacitors.gui.manual.CraftingPage;
import com.mhfs.capacitors.gui.manual.IPage;
import com.mhfs.capacitors.gui.manual.ImagePage;
import com.mhfs.capacitors.gui.manual.IndexPage;
import com.mhfs.capacitors.gui.manual.LogoPage;
import com.mhfs.capacitors.gui.manual.MultiblockPage;
import com.mhfs.capacitors.gui.manual.TextPage;
import com.mhfs.capacitors.misc.Lo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class SimpleReloadableKnowledgeRegistry implements IKnowledgeRegistry, IResourceManagerReloadListener{
	
	private String name;
	private IResourceManager resourceManager;
	private Map<String, List<IPage>> chapters;
	private List<IPage> index;
	
	public SimpleReloadableKnowledgeRegistry(String languageString){
		this.name = languageString;
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

	@Override
	public void onResourceManagerReload(IResourceManager irm) {
		this.resourceManager = irm;
		chapters = new HashMap<String, List<IPage>>();
		index = null;
		Lo.g.info("Reloading user manual...");
		try {
			ResourceLocation loc = new ResourceLocation(I18n.format(name));
			this.loadManual(loc);
		} catch (Exception e) {
			System.err.println("An error occured while reloading the Manual of Big Capacitors!");
			e.printStackTrace();
		}
	}
	
	private void loadManual(ResourceLocation loc) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(loc)));
		String line;
		while((line = br.readLine()) != null){
			String[] args = line.split("#");
			List<IPage> chapter = loadChapter(new ResourceLocation(args[1]));
			registerChapter(args[0], chapter);
		}
	}

	private List<IPage> loadChapter(ResourceLocation loc) throws Exception{
		List<IPage> list = new ArrayList<IPage>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(loc)));
		String line;
		while((line = br.readLine()) != null){
			String[] args = line.split("#");
			IPage page = loadPage(args[0], args[1]);
			if(page == null){
				throw new Exception("Invalid page type '" + args[0] + "' for '" + args[1] + "' in '" + loc.toString());
			}
			list.add(page);
		}
		
		return list;
	}

	private IPage loadPage(String type, String location) throws IOException {
		ResourceLocation loc = new ResourceLocation(location);
		if(type.equals("text")){
			return loadTextPage(loc);
		}else if(type.equals("craft")){
			return loadCraftingPage(loc);
		}else if(type.equals("img")){
			return loadImagePage(loc);
		}else if(type.equals("mb")){
			return loadMultiblockPage(loc);
		}
		
		return null;
	}

	private IPage loadTextPage(ResourceLocation location) throws IOException {
		String text = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location)));
		String line;
		while((line = br.readLine()) != null){
			text += line + "\n";
		}
		return new TextPage(text, "\n");
	}
	
	private IPage loadCraftingPage(ResourceLocation location) throws IOException {
		String[] in = new String[9];
		String out;
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location)));
		for(int i = 0; i < in.length; i++){
			in[i] = br.readLine();
		}
		out = br.readLine();
		return new CraftingPage(in, out);
	}
	
	private IPage loadImagePage(ResourceLocation location) throws IOException {
		String loc;
		int disWidth, disHeight;
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location)));
		loc = br.readLine();
		disWidth = Integer.parseInt(br.readLine());
		disHeight = Integer.parseInt(br.readLine());
		return new ImagePage(new ResourceLocation(loc), disHeight, disWidth);
	}
	
	private IPage loadMultiblockPage(ResourceLocation location) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location)));
		String mbLoc = br.readLine();
		String orientatedActions = br.readLine();
		return new MultiblockPage(mbLoc, orientatedActions, resourceManager);
	}
	
	private InputStream getInputStream(ResourceLocation loc) throws IOException{
		IResource resource = resourceManager.getResource(loc);
		return resource.getInputStream();
	}
}
