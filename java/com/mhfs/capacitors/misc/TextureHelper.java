package com.mhfs.capacitors.misc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class TextureHelper implements IResourceManagerReloadListener {
	
	private final static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ResourceLocation.class, new TypeAdapter<ResourceLocation>(){
		@Override
		public void write(JsonWriter out, ResourceLocation value) throws IOException {
			out.value(value.toString());
		}
		@Override
		public ResourceLocation read(JsonReader in) throws IOException {
			return new ResourceLocation(in.nextString());
		}
	}).create();
	
	private ResourceLocation textureFile;
	private int textureSizeX, textureSizeY;
	private Map<String, SubTexture> subTextures;
	
	private transient ResourceLocation location;
	
	protected TextureHelper(){
		this.subTextures =  new HashMap<String, SubTexture>();
	}
	
	public TextureHelper(ResourceLocation texture, Map<String, SubTexture> nameToSubTextureMap) {
		this();
		this.textureFile = texture;
		this.subTextures.putAll(nameToSubTextureMap);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		try {
			TextureHelper helper = loadInternal(resourceManager, location);
			this.subTextures = helper.subTextures;
			this.textureSizeX = helper.textureSizeX;
			this.textureSizeY = helper.textureSizeY;
			this.textureFile = helper.textureFile;
		} catch (IOException e) {
			Lo.g.error("Exception while reloading a TextureHelper", e);
		}
	}
	
	public SubTexture getTextureInfo(String name) {
		return subTextures.get(name);
	}
	
	public void drawTextureAt(Minecraft mc, String textureName, int x, int y) {
		SubTexture sub = subTextures.get(textureName);
		if(sub == null) return;
		mc.getTextureManager().bindTexture(textureFile);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, sub.x, sub.y, sub.width, sub.height, textureSizeX, textureSizeY);
	}
	
	public static TextureHelper loadFromJSON(IResourceManager manager, ResourceLocation location) throws IOException{
		TextureHelper helper = loadInternal(manager, location);
		((IReloadableResourceManager) manager).registerReloadListener(helper);
		return helper;
	}
	
	private static TextureHelper loadInternal(IResourceManager manager, ResourceLocation location) throws IOException {
		InputStreamReader resourceIn = new InputStreamReader(manager.getResource(location).getInputStream());
		TextureHelper helper = gson.fromJson(resourceIn, TextureHelper.class);
		helper.location = location;
		return helper;
	}
	
	public static class SubTexture {
		private final int x, y;
		private final int width, height;
		
		public SubTexture(int x, int y, int width, int height){
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}
	}
}
