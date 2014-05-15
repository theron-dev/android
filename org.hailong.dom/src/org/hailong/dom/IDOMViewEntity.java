package org.hailong.dom;

import android.view.View;

public interface IDOMViewEntity {

	public void doAction(DOMElement element);
	
	public void doNeedsDisplay(DOMElement element);
	
	public View elementViewOf(DOMElement element,Class<?> viewClass);
	
	public void elementLayoutView(DOMElement element,View view);
	
	public void elementDetach(DOMElement element);
	
}
