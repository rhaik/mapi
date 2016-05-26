package com.cyhd.common.util;

import java.util.Collection;

public class TotalList<E> {

	private Integer total;
	private Integer offset;
	private Collection<E> elements;
	private String extraInfo;
	
	public TotalList(){
		
	}
	
	public TotalList(int total, Collection<E> elements) {
		super();
		this.total = total;
		this.elements = elements;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Collection<E> getElements() {
		return elements;
	}
	public void setElements(Collection<E> elements) {
		this.elements = elements;
	}
	
	public boolean hasElements(){
		return elements != null && elements.size() > 0;
	}
	
	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	
	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "TotalList [total=" + total + ", elements=" + elements + "]";
	}
	
	
}
