package com.cyhd.common.util.richtext;

import java.util.Collection;

import com.cyhd.common.util.KeywordMatcher;
import com.cyhd.common.util.KeywordMatcher.Match;
import com.cyhd.common.util.richtext.TagParser.Tag;

public class KeywordMarker {
	
	// The start tag for marked keyword, e.g., <span style="color: red;">
	private String startTag;
	// The end tag for marked keyword, e.g., </span>
	private String endTag;
	
	// The keyword matcher
	private KeywordMatcher matcher;
	
	public KeywordMarker(String startTag, String endTag) {
		this(startTag, endTag, true);
	}
	
	public KeywordMarker(String startTag, String endTag, boolean caseInsensitive) {
		this.startTag = (startTag == null) ? "" : startTag;
		this.endTag = (endTag == null) ? "" : endTag;
		matcher = new KeywordMatcher(caseInsensitive);
	}

	public String getStartTag()
	{
		return startTag;
	}

	public void setStartTag(String startTag)
	{
		this.startTag = startTag;
	}

	public String getEndTag()
	{
		return endTag;
	}

	public void setEndTag(String endTag)
	{
		this.endTag = endTag;
	}
	
	/**
	 * Add a keyword.
	 * 
	 * @param keyword
	 */
	public void addKeyword(String keyword) {
		matcher.addKeyword(keyword);
	}
	
	/**
	 * Batch add keywords.
	 * 
	 * @param keywords
	 */
	public void addKeywords(Collection<String> keywords) {
		if (keywords != null)
			for (String keyword : keywords)
				matcher.addKeyword(keyword);
	}
	
	/**
	 * Remove a keyword.
	 *  
	 * @param keyword
	 */
	public void removeKeyword(String keyword) {
		matcher.removeKeyword(keyword);
	}
	
	/**
	 * Batch remove keywords.
	 * 
	 * @param keywords
	 */
	public void removeKeywords(Collection<String> keywords) {
		if (keywords != null)
			for (String keyword : keywords)
				matcher.removeKeyword(keyword);
	}
	
	
	
	/**
	 * Mark keywords in HTML document.
	 * 
	 * @param input
	 * @return
	 */
	public String markHtmlDocument(String input) {
		if (input == null || input.length() == 0)
			// no input string
			return input;
		if (matcher.size() == 0)
			return input;

		StringBuilder buf = new StringBuilder();
		Tag tag = null;
		int position = 0;
		while ((tag = TagParser.nextTag(input, position)) != null) {
			if (tag.beginIndex > position) {
				// Mark the plain text part before a tag
				String text = input.substring(position, tag.beginIndex);
				String escaped = HtmlUtil.escapeFromHtml(text);
				
				Match match = null;
				int idx = 0;
				while ((match = matcher.nextMatch(escaped, idx)) != null) {
					if (match.beginIndex > idx) {
						text = escaped.substring(idx, match.beginIndex);
						buf.append(HtmlUtil.escapeToHtml(text));
					}
					buf.append(startTag);
					text = escaped.substring(match.beginIndex, match.endIndex);
					buf.append(HtmlUtil.escapeToHtml(text));
					buf.append(endTag);
					
					idx = match.endIndex;
				}
				if (idx < escaped.length()) {
					text = escaped.substring(idx);
					buf.append(HtmlUtil.escapeToHtml(text));
				}
			}
			buf.append(input, tag.beginIndex, tag.endIndex);
			position = tag.endIndex;
		}
		if (position < input.length()) {
			// Mark the remaining plain text part at the end
			String text = input.substring(position);
			String escaped = HtmlUtil.escapeFromHtml(text);
			
			Match match = null;
			int idx = 0;
			while ((match = matcher.nextMatch(escaped, idx)) != null) {
				if (match.beginIndex > idx) {
					text = escaped.substring(idx, match.beginIndex);
					buf.append(HtmlUtil.escapeToHtml(text));
				}
				buf.append(startTag);
				text = escaped.substring(match.beginIndex, match.endIndex);
				buf.append(HtmlUtil.escapeToHtml(text));
				buf.append(endTag);
				
				idx = match.endIndex;
			}
			if (idx < escaped.length()) {
				text = escaped.substring(idx);
				buf.append(HtmlUtil.escapeToHtml(text));
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * Mark keyword in plain text. It also escape HTML character entities in 
	 * the text, so that the content could be directly presented as HTML 
	 * document.
	 * 
	 * @param input
	 * @return
	 */
	public String markPlainText(String input) {
		if (input == null || input.length() == 0)
			return input;
		if (matcher.size() == 0)
			return HtmlUtil.escapeToHtml(input);
		
		Match match = null;
		// Fast path
		if ((match = matcher.nextMatch(input, 0)) == null)
			// no match at all
			// escape html characters
			return HtmlUtil.escapeToHtml(input);
				
		// Do my job
		StringBuilder buf = new StringBuilder();
		int position = 0;
		while ((match = matcher.nextMatch(input, position)) != null) {
			if (match.beginIndex > position) 
				buf.append(HtmlUtil.escapeToHtml(
						input.substring(position, match.beginIndex) ) );
			buf.append(startTag);
			buf.append(HtmlUtil.escapeToHtml(
					input.substring(match.beginIndex, match.endIndex) ) );
			buf.append(endTag);
			
			position = match.endIndex;
		}
		if (position < input.length()) 
			buf.append(HtmlUtil.escapeToHtml(input.substring(position) ) );
		return buf.toString();
	}
	

}
