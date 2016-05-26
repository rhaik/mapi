package com.cyhd.common.util.richtext;

import com.cyhd.common.util.richtext.HtmlParser.Element;
import com.cyhd.common.util.richtext.HtmlUtil.ElementSelector;

public class ObjectElementSelector implements ElementSelector {

	@Override
	public boolean isTargetElement(String doc, Element element) {
		if (element.tag == TagParser.OBJECT_BEGIN_TAG) return true;
		return false;
	}

	private static final ObjectElementSelector _instance = new ObjectElementSelector();
	public static final ObjectElementSelector instance() { return _instance;}
}
