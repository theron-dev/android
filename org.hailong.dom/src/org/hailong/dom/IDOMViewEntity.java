package org.hailong.dom;

import android.os.Handler;
import android.view.View;

public interface IDOMViewEntity {

	public void doAction(IDOMViewEntity viewEntity, DOMElement element);
	
	public void doNeedsDisplay(DOMElement element);
	
	public View elementViewOf(DOMElement element,Class<?> viewClass);
	
	public void elementLayoutView(DOMElement element,View view);
	
	public void elementDetach(DOMElement element);
	
	public void elementVisable(IDOMViewEntity viewEntity,DOMElement element);
	
	public Handler getHandler();
}
