package com.mhfs.api.manual.knowledge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mhfs.api.manual.util.IPage;
import com.mhfs.capacitors.misc.Lo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class SimpleReloadableManual implements IManual, IResourceManagerReloadListener{
	
	private String name;
	private IResourceManager resourceManager;
	private Map<String, List<IPage>> chapters;
	private List<IPage> index;
	private PageLoaderManager manager;
	private ResourceLocation texture;
	
	/**
	 * Allows you to create your own Manual.
	 * @param textureLocation indicates what textures should be used for the manual. (Background, buttons, searchbox etc)
	 * @param languageString an unlocalized, localizable String which will be localized to the manuals resource location.
	 * @param manager the PageLoaderManager, prepared with loaders, to load the Manual.
	 */
	public SimpleReloadableManual(ResourceLocation textureLocation, String languageString, PageLoaderManager manager){
		this.name = languageString;
		this.manager = manager;
		this.texture = textureLocation;
	}
	
	public ResourceLocation getTextureLocation() {
		return texture;
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
			index = manager.getIndex(chapters);
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
			List<IPage> pages = loadPages(args[0], args[1]);
			if(pages == null){
				throw new Exception("Invalid page type '" + args[0] + "' for '" + args[1] + "' in '" + loc.toString());
			}
			list.addAll(pages);
		}
		
		return list;
	}

	private List<IPage> loadPages(String type, String location) throws IOException {
		ResourceLocation loc = new ResourceLocation(location);
		try {
			return manager.loadPages(type, loc, resourceManager);
		} catch(Exception e) {
			String msg = String.format("Error loading page '%s' (type '%s')", location, type);
			throw new IOException(msg, e);
		}
	}
	
	private InputStream getInputStream(ResourceLocation loc) throws IOException{
		IResource resource = resourceManager.getResource(loc);
		return resource.getInputStream();
	}
}
