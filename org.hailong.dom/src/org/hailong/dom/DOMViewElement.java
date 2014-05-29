package org.hailong.dom;

import org.hailong.core.Color;
import org.hailong.core.Size;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DOMViewElement extends DOMLayoutElement{

	private View _view;
	
	public boolean isViewLoaded(){
		return _view != null;
	}
	
	public View getView(){
		return _view;
	}
	
	public void setView(View view){
		_view = view;
		
		if(_view != null){
			_view.setBackgroundColor(colorValue("background-color",new Color()).intValue());
		}
		
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
			
			_view = null;
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
		else if(_view == null){
			setView(viewEntity.elementViewOf(this, getViewClass()));
		}
		
	}
	
	public Size layout(Size size){
		Size s = super.layout(size);
		
		if(isViewLoaded()){
			
			IDOMViewEntity viewEntity =getViewEntity();
			
			if(viewEntity != null){
				viewEntity.elementLayoutView(this, getView());
			}
			
		}
		
		return s;
	}

	@Override
	protected IDOMViewEntity elementViewEntity(DOMElement element){
		return null;
	}
	
	@Override
	public boolean isViewEntity(IDOMViewEntity viewEntity){
		return false;
	}
	
	@Override
	public void setAttributeValue(String name ,String value){
		super.setAttributeValue(name, value);
		
		if(isViewLoaded()){
			View v = getView();
			if(v instanceof IDOMView){
				((IDOMView) v).onElementAttributeChanged(this, name, value);
			}
		}
		
	}
	
	@Override
	public void removeAttributeValue(String name){
		super.removeAttributeValue(name);
		
		if(isViewLoaded()){
			View v = getView();
			if(v instanceof IDOMView){
				((IDOMView) v).onElementAttributeChanged(this, name, null);
			}
		}
		
	}
}
