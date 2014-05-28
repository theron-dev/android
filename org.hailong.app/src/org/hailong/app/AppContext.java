package org.hailong.app;

import org.hailong.dom.DOMStyleSheet;
import org.hailong.service.IServiceContext;

public interface AppContext extends IServiceContext {

	public DOMStyleSheet getStyleSheet();
	
	public void setStyleSheet(DOMStyleSheet styleSheet);
	
}
