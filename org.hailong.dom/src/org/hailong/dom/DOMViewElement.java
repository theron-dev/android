package org.hailong.dom;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DOMViewElement extends DOMLayoutElement {

	private View _view;
	
	public boolean isViewLoaded(){
		return _view != null;
	}
	
	public void setView(View view){
		_view = view;
		if(_view != null && _view instanceof IDOMView){
			((IDOMView) _view).setElement(this);
		}
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
		
		if(viewEntity==null){
			setView(null);
		}
		
		super.onViewEntityChanged(viewEntity);
	}
}
