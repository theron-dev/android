package org.hailong.app;

import org.hailong.dom.DOMStyleSheet;
import org.hailong.service.ServiceContext;

public class AppServiceContext extends ServiceContext implements AppContext {

	private DOMStyleSheet _styleSheet;
	
	@Override
	public DOMStyleSheet getStyleSheet() {
		return _styleSheet;
	}

	@Override
	public void setStyleSheet(DOMStyleSheet styleSheet) {
		_styleSheet = styleSheet;
	}

}
