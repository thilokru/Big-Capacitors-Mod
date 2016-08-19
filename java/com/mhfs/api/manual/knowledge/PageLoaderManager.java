package com.mhfs.api.manual.knowledge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mhfs.api.manual.util.IPage;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PageLoaderManager {
	
	private Map<String, IPageLoader> loaderMappings;
	private IIndexPageLoader indexLoader;
	
	public PageLoaderManager() {
		loaderMappings = new HashMap<String, IPageLoader>();
	}
	
	public PageLoaderManager(IIndexPageLoader indexLoader) {
		this();
		this.indexLoader = indexLoader;
	}
	
	public PageLoaderManager(IIndexPageLoader indexLoader, Map<String, IPageLoader> loaderMappings) {
		this(indexLoader);
		this.loaderMappings.putAll(loaderMappings);
	}
	
	public void registerLoader(String type, IPageLoader loader) {
		loaderMappings.put(type, loader);
	}
	
	public void setIndexLoader(IIndexPageLoader indexLoader) {
		this.indexLoader = indexLoader;
	}
	
	public List<IPage> getIndex(Map<String, List<IPage>> chapters) {
		return indexLoader.getIndex(chapters);
	}
	
	public List<IPage> loadPages(String type, ResourceLocation pagesLocation, IResourceManager resourceManager) throws Exception {
		IPageLoader loader = loaderMappings.get(type);
		if(loader == null) throw new IllegalStateException(String.format("Unregistered page type '%s' can't be loaded! You need to register it.", type));
		return loader.load(pagesLocation, resourceManager);
	}
}
