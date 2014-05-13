package org.hailong.dom;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DOMViewElement extends DOMLayoutElement {

	private View _view;
	
	public boolean isViewLoaded(){
		return _view != null;
	}
	
	@Override
	public void removeFromParent(){
		
		if(_view != null){
			
			ViewParent v = _view.getParent();
			
			if(v != null && v instanceof ViewGroup){
				((ViewGroup)v).removeView(_view);
			}
			
		}
		
		super.removeFromParent();
	}
	
	protected void onViewEntityChanged(IDOMViewEntity viewEntity){
		
		
		super.onViewEntityChanged(viewEntity);
	}
}
