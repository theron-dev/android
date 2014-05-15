package org.hailong.dom;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DOMViewElement extends DOMLayoutElement {

	private View _view;
	
	public boolean isViewLoaded(){
		return _view != null;
	}
	
	public View getView(){
		return _view;
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
	
	public Class<?> getViewClass(){
		
		try {
			
			return Class.forName(stringValue("viewClass","android.view.View"));
	
		} catch (ClassNotFoundException e) {
			Log.e(DOM.TAG, Log.getStackTraceString(e));
		}
	
		return android.view.View.class;
	}
	
	protected void onViewEntityChanged(IDOMViewEntity viewEntity){
		
		if(viewEntity==null){
			setView(null);
		}
		else {
			setView(viewEntity.elementViewOf(this, getViewClass()));
		}
		
		super.onViewEntityChanged(viewEntity);
	}
}
