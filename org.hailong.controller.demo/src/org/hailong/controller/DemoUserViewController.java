package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.controllers.IViewControllerContext;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DemoUserViewController extends DemoBaseController {

	public DemoUserViewController(IViewControllerContext<DemoContext> context,
			int viewLayout) {
		super(context, viewLayout);
		
	}

	@Override
	public void viewWillAppear(boolean animated){
		super.viewWillAppear(animated);
	}
	
	@Override
	public int getControllerOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}
	
	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		TextView titleView = (TextView) getView().findViewById(R.id.titleTextView);
		
		titleView.setText(getTitle());
		
		Button button = (Button) getView().findViewById(R.id.button1);
		
		button.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				
				getControllerContext().openUrl("User", true);
				
			}});
	}
}
