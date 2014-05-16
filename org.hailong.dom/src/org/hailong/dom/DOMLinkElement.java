package org.hailong.dom;

import org.hailong.core.Color;
import org.hailong.core.Rect;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


public class DOMLinkElement extends DOMLabelElement implements
		IDOMControlElement {

	private boolean _touchInset;
	private View _actionView = null;

	@Override
	public boolean onTouch(IDOMViewEntity viewEntity, int action, float touchX,
			float touchY) {
	
		if(action == MotionEvent.ACTION_DOWN){
			
			Rect r = getFrame();
			float displayScale = getDocument().getBundle().displayScale();

			float width = r.getWidth() * displayScale;
			float height = r.getHeight() * displayScale;
			
			if(touchX >=0 && touchX < width && touchY >=0 && touchY < height){
				
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
		else if(action == MotionEvent.ACTION_MOVE){
			
			if(_actionView != null){
				
				Rect r = getFrame();
				
				float displayScale = getDocument().getBundle().displayScale();

				float width = r.getWidth() * displayScale;
				float height = r.getHeight() * displayScale;
				
				if(touchX >=0 && touchX < width && touchY >=0 && touchY < height){
					_actionView.setVisibility(View.VISIBLE);
					_touchInset = true;
				}
				else {
					_actionView.setVisibility(View.GONE);
					_touchInset = false;
				}
				
			}

		}
		else {
			

			if(_actionView != null){
				
				ViewParent parent = _actionView.getParent();
				if(parent != null && parent instanceof ViewGroup){
					((ViewGroup) parent).removeView(_actionView);
				}
				_actionView = null;
				
				if(action ==  MotionEvent.ACTION_UP){
					
					if(_touchInset){
					
						if(viewEntity != null){
							viewEntity.doAction(viewEntity, this);
						}
					}
					
				}
		
				_touchInset = false;
			}
			
		}
		
		return false;
	}


}
