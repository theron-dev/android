package org.hailong.dom;

public interface IDOMView {

	public void setElement(DOMElement element);
	
	public void onElementAttributeChanged(DOMElement element, String name,String value);
	
}
