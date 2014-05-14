package org.hailong.dom;

import org.hailong.core.Rect;
import org.hailong.core.Color;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DOMActionElement extends DOMCanvasElement implements IDOMControlElement{

	private boolean _touchInset;
	private View _actionView = null;

	@Override
	public boolean onTouch(IDOMViewEntity viewEntity, int action, float touchX,
			float touchY) {
	
		if(action == MotionEvent.ACTION_DOWN){
			
			Rect r = getFrame();
			
			if(touchX >=0 && touchX < r.getWidth() && touchY >=0 && touchY < r.getHeight()){
				
				if(_actionView != null) {
					ViewParent parent = _actionView.getParent();
					if(parent != null && parent instanceof ViewGroup){
						((ViewGroup) parent).removeView(_actionView);
					}
					_actionView = null;
				}
				
				_actionView = getViewEntity().elementViewOf(this, View.class);
				
				Color color = colorValue("action-color",new Color(1.0f,1.0f,1.0f,0.4f));
				
				_actionView.setBackgroundColor(color.intValue());
				
				_touchInset = true;
				
				return true;
			}
			
		}
		else if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
			
			if(_actionView != null){
				
				ViewParent parent = _actionView.getParent();
				if(parent != null && parent instanceof ViewGroup){
					((ViewGroup) parent).removeView(_actionView);
				}
				_actionView = null;
				
				if(action ==  MotionEvent.ACTION_UP){
					
					if(_touchInset){
						getViewEntity().doAction(this);
					}
					
				}
		
				_touchInset = false;
			}
			
			
		}
		else {
			
			if(_actionView != null){
				
				Rect r = getFrame();
				
				if(touchX >=0 && touchX < r.getWidth() && touchY >=0 && touchY < r.getHeight()){
					_actionView.setVisibility(View.VISIBLE);
					_touchInset = true;
				}
				else {
					_actionView.setVisibility(View.GONE);
					_touchInset = false;
				}
				
			}
			
		}
		
		return false;
	}


}
