package com.mhfs.capacitors.knowledge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mhfs.api.manual.knowledge.IIndexPageLoader;
import com.mhfs.api.manual.util.IPage;
import com.mhfs.capacitors.gui.pages.IndexPage;
import com.mhfs.capacitors.gui.pages.LogoPage;

public class IndexLoader implements IIndexPageLoader{
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