package com.mhfs.api.manual.knowledge;

import java.util.List;
import java.util.Map;

import com.mhfs.api.manual.util.IPage;

public interface IIndexPageLoader {

	public List<IPage> getIndex(Map<String, List<IPage>> chapters);
}
