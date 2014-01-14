package org.hailong.controller;

import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.controllers.ViewController;

public class DemoBaseController extends ViewController<DemoContext> {

	public DemoBaseController(IViewControllerContext<DemoContext> context,
			int viewLayout) {
		super(context, viewLayout);
		
	}
	
	

}
