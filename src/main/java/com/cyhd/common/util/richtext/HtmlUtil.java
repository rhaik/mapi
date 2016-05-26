package com.cyhd.common.util.richtext;

import static com.cyhd.common.util.richtext.AttributeParser.APOSTROPHE_DELIMITER;
import static com.cyhd.common.util.richtext.AttributeParser.QUOTATION_DELIMITER;
import static com.cyhd.common.util.richtext.TagParser.BR_BEGIN_TAG;
import static com.cyhd.common.util.richtext.TagParser.IMG_BEGIN_TAG;
import static com.cyhd.common.util.richtext.TagParser.OBJECT_BEGIN_TAG;
import static com.cyhd.common.util.richtext.TagParser.P_BEGIN_TAG;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import com.cyhd.common.util.richtext.AttributeParser.Attribute;
import com.cyhd.common.util.richtext.HtmlParser.Element;
import com.cyhd.common.util.richtext.HtmlParser.Node;
import com.cyhd.common.util.richtext.TagParser.Tag;

public class HtmlUtil {
	
	/**
	 * Interface for HTML document element selector, which selects from an input
	 * HTML document the element we want to operate on (e.g., replaced).
	 * 
	 */
	public interface ElementSelector {
		/**
		 * Whether this element is the target we want to operate on?
		 * 
		 * @param doc
		 * @param element
		 * @return
		 */
		boolean isTargetElement(String doc, Element element);
	}
	
	/**
	 * Retrieve document element specified by the selector.
	 * 
	 * @param doc
	 * @param selector
	 * @return String
	 */
	public static String extractDocumentElement(String doc, 
			ElementSelector selector)
	{
		if (doc == null || doc.length() == 0 || selector == null)
			return doc;
		Element document = HtmlParser.parse(doc);
		if (document == null)
			return null;

		/**
		 * Search for the HTML element. This is a Depth First Search algorithm.
		 */
		Stack<Node> hierarchy = new Stack<Node>();
		hierarchy.push(document);
		int position = 0;
		while (!hierarchy.empty()) {
			Node node = hierarchy.peek();

			if (node instanceof Element) {
				Element e = (Element) node;

				if (e.beginTagBeginIndex >= position) {
					// The first time we traverse this element
					position = e.beginTagEndIndex;

					if (selector.isTargetElement(doc, e)) {
						return doc.substring(e.beginTagBeginIndex, e.endIndex);
					}
				}
				// Check this element's children
				Collection<Node> children = e.getChildren();
				if (children == null || children.isEmpty()) {
					// back trace
					position = e.endIndex;
					hierarchy.pop();
				} else {
					Iterator<Node> iter = children.iterator();
					boolean done = true;
					while (iter.hasNext()) {
						Node cur = iter.next();
						if (cur.beginIndex >= position) {
							hierarchy.push(cur);
							done = false;
							break;
						}
					}
					if (done) {
						// back trace
						position = e.endIndex;
						hierarchy.pop();
					}
				}
			} else {
				// Ignore other HTML element
				position = node.endIndex;
				hierarchy.pop();
			}
		}

		return null;
	}
	
	/**
	 * Retrieve document element specified by the selector.
	 * 
	 * @param doc
	 * @param selector
	 * @return String
	 */
	public static Element extractElement(String doc, 
			ElementSelector selector)
	{
		if (doc == null || doc.length() == 0 || selector == null)
			return null;
		Element document = HtmlParser.parse(doc);
		if (document == null)
			return null;

		/**
		 * Search for the HTML element. This is a Depth First Search algorithm.
		 */
		Stack<Node> hierarchy = new Stack<Node>();
		hierarchy.push(document);
		int position = 0;
		while (!hierarchy.empty()) {
			Node node = hierarchy.peek();

			if (node instanceof Element) {
				Element e = (Element) node;

				if (e.beginTagBeginIndex >= position) {
					// The first time we traverse this element
					position = e.beginTagEndIndex;

					if (selector.isTargetElement(doc, e)) {
						return e;
					}
				}
				// Check this element's children
				Collection<Node> children = e.getChildren();
				if (children == null || children.isEmpty()) {
					// back trace
					position = e.endIndex;
					hierarchy.pop();
				} else {
					Iterator<Node> iter = children.iterator();
					boolean done = true;
					while (iter.hasNext()) {
						Node cur = iter.next();
						if (cur.beginIndex >= position) {
							hierarchy.push(cur);
							done = false;
							break;
						}
					}
					if (done) {
						// back trace
						position = e.endIndex;
						hierarchy.pop();
					}
				}
			} else {
				// Ignore other HTML element
				position = node.endIndex;
				hierarchy.pop();
			}
		}
		return null;
	}
	
	/**
	 * Document element attributes transformer. It returns the changed
	 * attributes part. If an element is not changed, it should return the
	 * original attributes part of the element.
	 * 
	 */
	public interface AttributesTransformer {
		public String transformAttributes(String doc, Element element);
	}
	
	/**
	 * Transform elements' attributes using specified attributes transformer.
	 * 
	 * @param doc
	 * @param transformer
	 * @return
	 */
	public static String transformElementAttributes(String doc, 
			AttributesTransformer transformer) 
	{
		if (doc == null || doc.length() == 0 || transformer == null)
			return doc;
		Element document = HtmlParser.parse(doc);
		if (document == null)
			return doc;

		/**
		 * Search for the HTML element we want to replace and replace it. This
		 * is a Depth First Search algorithm.
		 */
		StringBuilder buf = new StringBuilder();
		Stack<Node> hierarchy = new Stack<Node>();
		hierarchy.push(document);
		int position = 0;
		while (!hierarchy.empty()) {
			Node node = hierarchy.peek();

			if (node instanceof Element) {
				Element e = (Element) node;

				if (e.beginTagBeginIndex >= position) {
					// The first time we traverse this element
					position = e.beginTagEndIndex;

					if (e != document)
						buf.append(doc.substring(e.beginTagBeginIndex, 
								e.attributeBeginIndex));

					buf.append(transformer.transformAttributes(doc, e));

					if (e != document)
						buf.append(doc.substring(e.attributeEndIndex,
								e.beginTagEndIndex));
				}
				// Check this element's children
				Collection<Node> children = e.getChildren();
				if (children == null || children.isEmpty()) {
					if (e != document && e.contentEndIndex < e.endIndex) {
						// This element has an end tag
						buf.append(doc.substring(e.contentEndIndex, 
								e.endIndex));
					}
					// back trace
					position = e.endIndex;
					hierarchy.pop();
				} else {
					Iterator<Node> iter = children.iterator();
					boolean done = true;
					while (iter.hasNext()) {
						Node cur = iter.next();
						if (cur.beginIndex >= position) {
							hierarchy.push(cur);
							done = false;
							break;
						}
					}
					if (done) {
						if (e != document && e.contentEndIndex < e.endIndex) {
							// This element has an end tag
							buf.append(doc.substring(e.contentEndIndex, 
									e.endIndex));
						}
						position = e.endIndex;
						hierarchy.pop();
					}
				}
			} else {
				// Output other HTML elements directly
				buf.append(doc.substring(node.beginIndex, node.endIndex));
				position = node.endIndex;
				hierarchy.pop();
			}
		}

		return buf.toString();
	}
	
	/**
	 * Interface for HTML document element changer, which changes elements of an
	 * input HTML document. The replacement string for the input element is 
	 * returned. 
	 * 
	 */
	public interface ElementChanger {
		/**
		 * Return the replacement string for the element. If the input element 
		 * is not an element you want to replace, simply returns null. If you 
		 * want to remove this element from output HTML document, simply returns
		 * an empty string ("").
		 * 
		 * @param doc
		 * @param element
		 * @return
		 */
		String replaceElement(String doc, Element element);
	}
	
	public static final short REPLACED = 1;
	
	/**
	 * Replace document element with specified replacement. The elements are 
	 * selected by the specified element selector.
	 * 
	 * @param doc
	 * @param selector
	 * @param replacement
	 * @return
	 */
	public static String replaceDocumentElement(String doc, 
			ElementChanger changer)
	{
		if (doc == null || doc.length() == 0 || changer == null)
			return doc;
		Element document = HtmlParser.parse(doc);
		if (document == null)
			return doc;

		/**
		 * Search for the HTML element we want to replace and replace it. This
		 * is a Depth First Search algorithm.
		 */
		StringBuilder buf = new StringBuilder();
		Stack<Node> hierarchy = new Stack<Node>();
		hierarchy.push(document);
		int position = 0;
		while (!hierarchy.empty()) {
			Node node = hierarchy.peek();

			if (node instanceof Element) {
				Element e = (Element) node;

				if (e.beginTagBeginIndex >= position) {
					// The first time we traverse this element
					position = e.beginTagEndIndex;

					String replacement = changer.replaceElement(doc, e);
					if (replacement != null) {
						buf.append(replacement);
						e.flags = REPLACED;
						position = e.endIndex;
					} else if (e != document) {
						buf.append(doc.substring(e.beginTagBeginIndex,
								e.beginTagEndIndex));
					}
				}
				// Check this element's children
				Collection<Node> children = e.getChildren();
				if (children == null || children.isEmpty()) {
					if (e != document && e.contentEndIndex < e.endIndex
							&& e.flags != REPLACED) {
						// This element has an end tag
						buf.append(doc.substring(e.contentEndIndex,	
								e.endIndex));
					}
					// back trace
					position = e.endIndex;
					hierarchy.pop();
				} else {
					Iterator<Node> iter = children.iterator();
					boolean done = true;
					while (iter.hasNext()) {
						Node cur = iter.next();
						if (cur.beginIndex >= position) {
							hierarchy.push(cur);
							done = false;
							break;
						}
					}
					if (done) {
						if (e != document && e.contentEndIndex < e.endIndex
								&& e.flags != REPLACED) {
							// This element has an end tag
							buf.append(doc.substring(e.contentEndIndex,
									e.endIndex));
						}
						position = e.endIndex;
						hierarchy.pop();
					}
				}
			} else {
				// Output other HTML elements directly
				buf.append(doc.substring(node.beginIndex, node.endIndex));
				position = node.endIndex;
				hierarchy.pop();
			}
		}

		return buf.toString();
	}

	/**
	 * Count images and characters (excluding whitespace) in the document. The 
	 * first element of the returned array is the number of images, and the
	 * second is the number of characters.
	 * 
	 * @param document
	 * @return
	 */
	public static int[] countImagesAndCharacters(String document) {
		if (document == null || document.length() == 0)
			return new int[]{0, 0};
		
		int imageCount = 0;
		int charCount = 0;
		
		Tag tag = null;
		int position = 0;
		while ((tag = TagParser.nextTag(document, position)) != null) {
			for (int i = position; i < tag.beginIndex; i++) {
				char c = document.charAt(i);
				if (!Character.isWhitespace(c) && c != '　')
					charCount++;
			}
			if (tag.type == IMG_BEGIN_TAG)
				imageCount++;
			position = tag.endIndex;
		}
		for (int i = position; i < document.length(); i++) {
			char c = document.charAt(i);
			if (!Character.isWhitespace(c) && c != '　')
				charCount++;
		}
		return new int[]{imageCount, charCount};
	}
	
	/**
	 * Retrieve the value of specified attribute of an element.
	 * 
	 * @param doc
	 * @param element
	 * @param attributeName
	 * @return
	 */
	public static String getAttributeValue(String doc, Element element, 
			String attributeName) 
	{
		String attributes = doc.substring(element.attributeBeginIndex, 
				element.attributeEndIndex);
		if (attributes.length() == 0)
			return null;

		Attribute attr = null;
		String name, value;
		int pos = 0;
		while ((attr = AttributeParser.nextAttribute(attributes, pos)) != null) 
		{
			pos = attr.endIndex;

			name = attributes.substring(attr.nameBeginIndex, 
					attr.nameEndIndex);
			if (name.equalsIgnoreCase(attributeName)) {
				value = attributes.substring(attr.valueBeginIndex,
						attr.valueEndIndex);
				
				if ((attr.delimiter.equals(QUOTATION_DELIMITER) ||
					attr.delimiter.equals(APOSTROPHE_DELIMITER)) &&
					value.indexOf('\\') >= 0) {
					
					boolean escaping = false;
				
					StringBuilder buf = new StringBuilder();
					for (int i = 0; i < value.length(); i++) {
						char c = value.charAt(i);
						if (escaping) {
							buf.append(c);
							escaping = false;
						}
						else if (c == '\\') 
							escaping = true;
						else 
							buf.append(c);
					}
					return buf.toString();
				}	
				else
					return value;
			}
		}
		
		return null;
	}
	
	/**
	 * Determine whether the input string is an HTML document.
	 * 
	 * @param document
	 * @return
	 */
	public static boolean isHtmlDocument(String input) {
		if (input == null || input.length() == 0)
			return false;

		return TagParser.nextTag(input, 0) != null;
	}
	
	/**
	 * Remove all markups in the input document. This is a utility method.
	 * 
	 * @param input
	 * @return
	 */
	public static String removeMarkups(String input) {
		if (input == null || input.length() == 0)
			return input;
		
		StringBuilder buf = new StringBuilder();
		Tag tag = null;
		int pos = 0;
		while ((tag = TagParser.nextTag(input, pos)) != null) {
			if (pos < tag.beginIndex)
				buf.append(input, pos, tag.beginIndex);
			pos = tag.endIndex;
		}
		if (pos < input.length())
			buf.append(input, pos, input.length());
		
		return buf.toString();	
	}
	
	/**
	 * Replace all markups in the input document with whitespace and convert the
	 * input string into plain text.
	 * 
	 * @param input
	 * @return
	 */
	public static String replaceMarkupWithWhitespace(String input) {
		if (input == null || input.length() == 0)
			return input;
		
		StringBuilder buf = new StringBuilder();
		Tag tag = null;
		int pos = 0;
		while ((tag = TagParser.nextTag(input, pos)) != null) {
			if (pos < tag.beginIndex)
				buf.append(input, pos, tag.beginIndex);
			buf.append(' ');
			pos = tag.endIndex;
		}
		if (pos < input.length())
			buf.append(input, pos, input.length());
		
		return compactWhitespaces(escapeFromHtml(buf.toString()));	
	}
	
	/**
	 * Escape special characters in input string, including '&', '<', '>' and 
	 * '"'.
	 * 
	 * @param input
	 * @return
	 */
	public static String escapeToHtml(String input) {
		if (input == null || input.length() == 0)
			return input;
		
		int extra = 0;
		char c;
		int i;
		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<' || c == '>')
				extra += 3;
			else if (c == '"')
				extra += 5;
			else if (c == '&')
				extra += 4;
		}
		
		if (extra == 0)
			// No special character in the input string
			return input;
		
		StringBuilder buf = new StringBuilder(input.length() + extra);
		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<')
				buf.append("&lt;");
			else if (c == '>')
				buf.append("&gt;");
			else if (c == '"')
				buf.append("&quot;");
			else if (c == '&')
				buf.append("&amp;");
			else
				buf.append(c);
		}
		return buf.toString();
	}

	/**
	 * Convert HTML escape characters (including "&amp;", "&lt;", "&gt;", 
	 * "&nbsp;", and "&quot;") in input string.
	 *  
	 * @param input
	 * @return
	 */
	public static String escapeFromHtml(String input) {
		if (input == null || input.length() == 0)
			return input;
		
		int extra = 0;
		char c;
		int i;
		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '&') {
				if (i + 5 < input.length() &&
					((input.charAt(i + 1) == 'n' &&
					  input.charAt(i + 2) == 'b' &&
					  input.charAt(i + 3) == 's' &&
					  input.charAt(i + 4) == 'p') ||
					 (input.charAt(i + 1) == 'q' &&
					  input.charAt(i + 2) == 'u' &&
					  input.charAt(i + 3) == 'o' &&
					  input.charAt(i + 4) == 't')) &&
					input.charAt(i + 5) == ';') {
					extra += 5;
					i += 5;
				}
				else if (i + 4 < input.length() && 
					input.charAt(i + 1) == 'a' &&
					input.charAt(i + 2) == 'm' &&
					input.charAt(i + 3) == 'p' &&
					input.charAt(i + 4) == ';') {
					extra += 4;
					i += 4;
				}
				else if (i + 3 < input.length() &&
						(input.charAt(i + 1) == 'l' ||
							input.charAt(i + 1) == 'g') &&
						input.charAt(i + 2) == 't' &&
						input.charAt(i + 3) == ';') {
					extra += 3;
					i += 3;
				}
			}
		}
		if (extra == 0)
			// no escaped characters in input string
			return input;
		
		StringBuilder buf = new StringBuilder(input.length() - extra);
		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '&') {
				if (i + 5 < input.length() &&
					((input.charAt(i + 1) == 'n' &&
					  input.charAt(i + 2) == 'b' &&
					  input.charAt(i + 3) == 's' &&
					  input.charAt(i + 4) == 'p') ||
					 (input.charAt(i + 1) == 'q' &&
					  input.charAt(i + 2) == 'u' &&
					  input.charAt(i + 3) == 'o' &&
					  input.charAt(i + 4) == 't')) &&
					input.charAt(i + 5) == ';') {
					buf.append(input.charAt(i + 1) == 'n' ? ' ' : '"');
					i += 5;
				}
				else if (i + 4 < input.length() && 
						input.charAt(i + 1) == 'a' &&
						input.charAt(i + 2) == 'm' &&
						input.charAt(i + 3) == 'p' &&
						input.charAt(i + 4) == ';') {
						buf.append('&');
						i += 4;
				}
				else if (i + 3 < input.length() &&
						(input.charAt(i + 1) == 'l' ||
							input.charAt(i + 1) == 'g') &&
						input.charAt(i + 2) == 't' &&
						input.charAt(i + 3) == ';') {
					buf.append(input.charAt(i + 1) == 'l' ? '<' : '>');
					i += 3;
				}
				else
					buf.append(c);
			}
			else
				buf.append(c);
		}
		
		return buf.toString();
	}
	
	/**
	 * Compact whitespaces in the input string. Leading and trailing whitespaces are removed.
	 * Consecutive whitespaces are replaced by a space character.
	 * 
	 * @param input
	 * @return
	 */
	public static String compactWhitespaces(String input) {
		if (input == null || input.length() == 0)
			return input;
		
		// Fast path
		boolean hasConsecutiveWhitespaces = false;
		int whitespaceBeginIndex = -1;
		for (int i = 0; i < input.length(); ++i) {
			if (Character.isWhitespace(input.charAt(i))) {
				if (whitespaceBeginIndex < 0) 
					whitespaceBeginIndex = i;
				else {
					hasConsecutiveWhitespaces = true;
					break;
				}
			}
			else 
				if (whitespaceBeginIndex >= 0)
					// reset beginIndex
					whitespaceBeginIndex = -1;
		}
		if (!hasConsecutiveWhitespaces)
			return input;
		
		// Slow path
		// Ignore leading white spaces
		int i = 0;
		while (i < input.length() && Character.isWhitespace(input.charAt(i)))
			++i;
		if (i == input.length())
			return "";
		
		StringBuilder buf = new StringBuilder(input.length());
		int nonWhitespaceBeginIndex = whitespaceBeginIndex = -1;
		for (; i < input.length(); ++i) {
			if (Character.isWhitespace(input.charAt(i))) { 
				if (whitespaceBeginIndex < 0) 
					whitespaceBeginIndex = i;
				if (nonWhitespaceBeginIndex >= 0) {
					buf.append(input, nonWhitespaceBeginIndex, i);
					nonWhitespaceBeginIndex = -1;
				}
			}
			else {
				if (whitespaceBeginIndex >= 0) {
					whitespaceBeginIndex = -1;
					buf.append(' ');
				}
				if (nonWhitespaceBeginIndex < 0) 
					nonWhitespaceBeginIndex = i;
			}
		}
		if (nonWhitespaceBeginIndex >= 0)
			buf.append(input, nonWhitespaceBeginIndex, input.length());
		return buf.toString();
	}
	
	/**
	 * Convert an HTML document to plain text, removing all mark-ups. The <br>,
	 * <br/>, <p> and </p> markups are replaced by a new line character. HTML 
	 * character entities (&amp;, &lt;, &gt;, &nbsp; and &quot;) are converted 
	 * to normal characters.
	 *  
	 * @param input
	 * @return
	 */
	public static String toPlainText(String input) {
		input = compactWhitespaces(input);
		if (input == null || input.length() == 0)
			return input;
		
		StringBuilder buf = new StringBuilder();
		Tag tag = null;
		int pos = 0;
		while ((tag = TagParser.nextTag(input, pos)) != null) {
			if (pos < tag.beginIndex)
				buf.append(escapeFromHtml(input.substring(pos, tag.beginIndex)));
			if (tag.type == BR_BEGIN_TAG || tag.type == P_BEGIN_TAG)
				buf.append('\n');
			pos = tag.endIndex;
		}
		if (pos < input.length())
			buf.append(escapeFromHtml(input.substring(pos)));
		
		return buf.toString().trim();	
	}
	
	/**
	 * Convert a plain text string to HTML document. The newline character 
	 * '\n' is replaced by a "<br/>" tag. The HTML character entities ('&', '<',
	 * '>', ' ', and '"') are escaped.
	 * 
	 * @param input
	 * @return
	 */
	public static String fromPlainText(String input) {
		if (input == null || input.length() == 0)
			return input;
		
		int extra = 0;
		char c;
		int i;
		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<' || c == '>')
				extra += 3;
			else if (c == '"')
				extra += 5;
			else if (c == '&')
				extra += 4;
			else if (c == '\n')
				extra += 4;
			else if (c == ' ')
				extra += 5;
		}
		
		if (extra == 0)
			// No special character in the input string
			return input;
		
		StringBuilder buf = new StringBuilder(input.length() + extra);
		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<')
				buf.append("&lt;");
			else if (c == '>')
				buf.append("&gt;");
			else if (c == '"')
				buf.append("&quot;");
			else if (c == '&')
				buf.append("&amp;");
			else if (c == '\n')
				buf.append("<br/>");
			else if (c == ' ')
				buf.append("&nbsp;");
			else
				buf.append(c);
		}
		return buf.toString();
	}
	
	/**
	 * Whether does the input HTML document contain <img> or <object> elements?
	 * 
	 * @param input
	 * @return true if the document contains <img> or <object> elements
	 */
	public static boolean containsImagesOrObjects(String input) {
		if (input == null || input.isEmpty())
			return false;
		
		Tag tag = null;
		int pos = 0;
		while ((tag = TagParser.nextTag(input, pos)) != null) {
			if (tag.type == IMG_BEGIN_TAG || tag.type == OBJECT_BEGIN_TAG)
				return true;
			pos = tag.endIndex;
		}
		return false;
	}
	
	public static class Test {
		public static final void main(String args[]) throws Exception {
			
		}
	}
	
}
