package com.mhfs.capacitors.gui.manual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PageLoader {
	
	public static void loadManual(Minecraft mc, ResourceLocation loc) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(loc, mc)));
		String line;
		while((line = br.readLine()) != null){
			String[] args = line.split("#");
			List<IPage> chapter = loadChapter(mc, new ResourceLocation(args[1]));
			KnowledgeRegistry.INSTANCE.registerChapter(args[0], chapter);
		}
	}

	public static List<IPage> loadChapter(Minecraft mc, ResourceLocation loc) throws Exception{
		List<IPage> list = new ArrayList<IPage>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(loc, mc)));
		String line;
		while((line = br.readLine()) != null){
			String[] args = line.split("#");
			IPage page = loadPage(args[0], args[1], mc);
			if(page == null){
				throw new Exception("Invalid page type '" + args[0] + "' for '" + args[1] + "' in '" + loc.toString());
			}
			list.add(page);
		}
		
		return list;
	}

	private static IPage loadPage(String type, String location, Minecraft mc) throws IOException {
		ResourceLocation loc = new ResourceLocation(location);
		if(type.equals("text")){
			return loadTextPage(loc, mc);
		}else if(type.equals("craft")){
			return loadCraftingPage(loc, mc);
		}else if(type.equals("img")){
			return loadImagePage(loc, mc);
		}
		
		return null;
	}

	private static IPage loadTextPage(ResourceLocation location, Minecraft mc) throws IOException {
		String text = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, mc)));
		String line;
		while((line = br.readLine()) != null){
			text += line + "\n";
		}
		return new TextPage(text, "\n");
	}
	
	private static IPage loadCraftingPage(ResourceLocation location, Minecraft mc) throws IOException {
		String[] in = new String[9];
		String out;
		int outAmount;
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, mc)));
		for(int i = 0; i < in.length; i++){
			in[i] = br.readLine();
		}
		out = br.readLine();
		outAmount = Integer.parseInt(br.readLine());
		return new CraftingPage(in, out, outAmount);
	}
	
	private static IPage loadImagePage(ResourceLocation location, Minecraft mc) throws IOException {
		String loc;
		int disWidth, disHeight;
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream(location, mc)));
		loc = br.readLine();
		disWidth = Integer.parseInt(br.readLine());
		disHeight = Integer.parseInt(br.readLine());
		return new ImagePage(new ResourceLocation(loc), disHeight, disWidth);
	}
	
	private static InputStream getInputStream(ResourceLocation loc, Minecraft mc) throws IOException{
		IResourceManager irm = mc.getResourceManager();
		IResource resource = irm.getResource(loc);
		return resource.getInputStream();
	}
}
