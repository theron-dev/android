package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.container.ContainerView;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.views.LoadingView;
import org.hailong.framework.views.ViewLayout;

public class DemoContainerViewController extends DemoBaseController {

	
	public DemoContainerViewController(
			IViewControllerContext<DemoContext> context, String viewLayout) {
		super(context, viewLayout);

	}
	
	@Override
	protected void didViewLoadedContextStart(){
		super.didViewLoadedContextStart();

		ViewLayout viewLayout = new ViewLayout(getContext(),R.layout.toploading);
		
		ContainerView containerView = (ContainerView) getView().findViewById(R.id.containerLayoutView);
		
		containerView.setTopLoadingView((LoadingView)viewLayout.getView());

		containerView.setBottomLoadingView((LoadingView)viewLayout.getView());

		containerView.setContentSize(0, 1024);
		
	}
	
	@Override
	protected void didViewUnLoaded(){
		
		
		super.didViewUnLoaded();
	}

}
