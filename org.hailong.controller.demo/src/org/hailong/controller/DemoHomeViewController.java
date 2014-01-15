package org.hailong.controller;

import org.hailong.controller.demo.R;
import org.hailong.framework.controllers.IViewController;
import org.hailong.framework.controllers.IViewControllerContext;
import org.hailong.framework.controllers.TabBarController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class DemoHomeViewController extends TabBarController<DemoContext> {

	private RadioGroup _radioGroup;
	
	public DemoHomeViewController(IViewControllerContext<DemoContext> context,
			int viewLayout) {
		super(context, viewLayout);
		
		
	}

	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		_radioGroup = (RadioGroup) getView().findViewById(R.id.tabRadioGroup);
		
		if(_radioGroup != null){
			_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					
					setSelectedIndex( group.indexOfChild(group.findViewById(checkedId)));
				}
			});
			
			int index = 0;
			if(index < _radioGroup.getChildCount()){
				((RadioButton)_radioGroup.getChildAt(index)).setChecked(true);
			}
			for(IViewController<DemoContext> viewController : getViewControllers()){
				if(index < _radioGroup.getChildCount()){
					RadioButton tab = (RadioButton)_radioGroup.getChildAt(index ++);
					tab.setText(viewController.getTitle());
				}
			}
		}
	}
	
	@Override
	protected void didViewUnLoaded(){
		super.didViewUnLoaded();
		
		_radioGroup = null;
	}
	
	@Override
	public void viewWillAppear(boolean animated){
		super.viewWillAppear(animated);
		
	}
	
	@Override
	public void viewDidAppear(boolean animated){
		super.viewDidAppear(animated);
	}
	
	@Override
	public void viewWillDisappear(boolean animated){
		super.viewWillDisappear(animated);
	}
	
	@Override
	public void viewDidDisappear(boolean animated){
		super.viewDidDisappear(animated);
		
	}
	
	@Override
	public int getControllerOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	}
	
	public boolean onPressBack(){
		
		new AlertDialog.Builder(getContext()).setCancelable(true).setItems(new String[]{"退出"}, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
			}}).create().show();
		
		return false;
	}
	
}
