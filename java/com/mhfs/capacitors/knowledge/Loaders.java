package com.mhfs.capacitors.knowledge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.mhfs.api.manual.gui.pages.CraftingPage;
import com.mhfs.api.manual.gui.pages.ImagePage;
import com.mhfs.api.manual.gui.pages.MultiblockPage;
import com.mhfs.api.manual.gui.pages.TextPage;
import com.mhfs.api.manual.knowledge.IIndexPageLoader;
import com.mhfs.api.manual.knowledge.IPageLoader;
import com.mhfs.api.manual.knowledge.PageLoaderManager;
import com.mhfs.api.manual.util.IPage;
import com.mhfs.capacitors.gui.pages.IndexPage;
import com.mhfs.capacitors.gui.pages.LogoPage;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Loaders {
	
	public static void registerLoadersToManager(PageLoaderManager manager) {
		manager.registerLoader("text", new TextPageLoader());
		manager.registerLoader("craft", new CraftingPageLoader());
		manager.registerLoader("img", new ImagePageLoader());
		manager.registerLoader("mb", new MultiblockPageLoader());
	}
	
	public static void registerIndexLoaderToManager(PageLoaderManager manager) {
		manager.setIndexLoader(new IndexLoader());
	}
	
	private static InputStream getInputStream(ResourceLocation loc, IResourceManager resourceManager) throws IOException{
		IResource resource = resourceManager.getResource(loc);
		return resource.getInputStream();
	}
	
	private static List<IPage> returnHelper(IPage page) {
		return Lists.newArrayList(page);
	}
	
	public static class IndexLoader implements IIndexPageLoader{
		public List<IPage> getIndex(Map<String, List<IPage>> chapters) {
			List<IPage> index = new ArrayList<IPage>();
			index.add(new LogoPage());
			Set<String> titles = chapters.keySet();
			String[] titleArray = titles.toArray(new String[0]);
			Arrays.sort(titleArray);
			index.add(new IndexPage(titleArray));
			return index;
		}
	}
	
	public static class TextPageLoader implements IPageLoader {
		public List<IPage> load(ResourceLocation location, IResourceManager manager) throws Exception {
			String text = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			String line;
			while((line = br.readLine()) != null){
				if(!line.trim().isEmpty()){
					if(!text.endsWith(" ") && !line.startsWith(" ")) {
						text += " " + line;
					} else {
						text += line;
					}
				} else {
					text += "\n";
				}
			}
			return returnHelper(new TextPage(text, "\n"));
		}
	}
	
	public static class CraftingPageLoader implements IPageLoader {
		public List<IPage> load(ResourceLocation location, IResourceManager manager) throws Exception {
			String[] in = new String[9];
			String out;
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			for(int i = 0; i < in.length; i++){
				in[i] = br.readLine();
			}
			out = br.readLine();
			return returnHelper(new CraftingPage(in, out));
		}
	}
	
	public static class ImagePageLoader implements IPageLoader {
		public List<IPage> load(ResourceLocation location, IResourceManager manager) throws Exception {
			String loc;
			int disWidth, disHeight;
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			loc = br.readLine();
			disWidth = Integer.parseInt(br.readLine());
			disHeight = Integer.parseInt(br.readLine());
			return returnHelper(new ImagePage(new ResourceLocation(loc), disHeight, disWidth));
		}
	}
	
	public static class MultiblockPageLoader implements IPageLoader {

		@Override
		public List<IPage> load(ResourceLocation location, IResourceManager manager) throws Exception {
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			String mbLoc = br.readLine();
			float scale = Float.parseFloat(br.readLine());
			return returnHelper(new MultiblockPage(mbLoc, scale, manager));
		}
		
	}
}
