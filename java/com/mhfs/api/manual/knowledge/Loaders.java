package com.mhfs.api.manual.knowledge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mhfs.api.manual.gui.pages.CraftingPage;
import com.mhfs.api.manual.gui.pages.ImagePage;
import com.mhfs.api.manual.gui.pages.MultiblockPage;
import com.mhfs.api.manual.gui.pages.TablePage;
import com.mhfs.api.manual.gui.pages.TextPage;
import com.mhfs.api.manual.util.IPage;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Loaders {
	
	public static void registerLoadersToManager(PageLoaderManager manager) {
		manager.registerLoader("text", new TextPageLoader());
		manager.registerLoader("table", new TablePageLoader());
		manager.registerLoader("craft", new CraftingPageLoader());
		manager.registerLoader("img", new ImagePageLoader());
		manager.registerLoader("mb", new MultiblockPageLoader());
	}
	
	private static InputStream getInputStream(ResourceLocation loc, IResourceManager resourceManager) throws IOException{
		IResource resource = resourceManager.getResource(loc);
		return resource.getInputStream();
	}
	
	private static List<IPage> returnHelper(IPage page) {
		return Lists.newArrayList(page);
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
	
	public static class TablePageLoader implements IPageLoader {
		public List<IPage> load(ResourceLocation location, IResourceManager manager) throws Exception {
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, manager)));
			List<String[]> lines = new ArrayList<String[]>();
			String line;
			while((line = br.readLine()) != null) {
				lines.add(line.split("\t"));
			}
			int maxLength = 0;
			for(String[] entries : lines) {
				maxLength = Math.max(maxLength, entries.length);		
			}
			String[][] data = new String[maxLength][lines.size()];
			for(int y = 0; y < lines.size(); y++) {
				for(int x = 0; x < maxLength; x++) {
					if(x >= lines.get(y).length) {
						data[x][y] = "";
					} else {
						data[x][y]= lines.get(y)[x];
					}
				}
			}
			return returnHelper(new TablePage(data));
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
