package com.mhfs.api.manual.knowledge;

import java.util.List;

import com.mhfs.api.manual.util.IPage;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public interface IPageLoader {

	public List<IPage> load(ResourceLocation location, IResourceManager manager) throws Exception;
}
