package org.hailong.framework.views;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class Animation implements android.view.animation.Animation.AnimationListener {

	private long _duration;
	private Listener _listener;
	private List<View> _views;
	private List<android.view.animation.Animation> _animations;
	private int _submitCount = 0;
	private int _startCount = 0;
	private int _endCount = 0;
	
	public void submit(){
		
		if(_submitCount == 0){
			
			_submitCount = 0;
			_startCount = 0;
			_startCount = 0;
			
			if(_views != null && _animations != null){
				
				int size = Math.min(_views.size(), _animations.size());
				
				for(int i=0;i<size;i++){
					
					View v = _views.get(i);
					
					android.view.animation.Animation anim = _animations.get(i);
					anim.setDuration(_duration);
					anim.setAnimationListener(this);
					
					v.startAnimation(anim);
					
					_submitCount ++;
				}
				
			}
		}
	}
	
	public void addAnimation(View view,android.view.animation.Animation anim){
		
		if(view != null && anim != null){
			
			if(_views == null){
				_views = new ArrayList<View>(4);
			}
			
			if(_animations == null){
				_animations = new ArrayList<android.view.animation.Animation>(4);
			}
			
			_views.add(view);
			_animations.add(anim);
		}

	}
	
	public void setListener(Listener listener){
		_listener = listener;
	}
	
	public void setDuration(long duration){
		_duration = duration;
	}
	
	public void translate(View view , float fromX,float toX,float fromY,float toY ){
		addAnimation(view, new TranslateAnimation(fromX, toX, fromY, toY));
	}
	
	public void scale(View view , float fromX,float toX,float fromY,float toY ){
		addAnimation(view, new ScaleAnimation(fromX, toX, fromY, toY));
	}
	
	public static interface Listener {
		
		public void onStart();
		
		public void onEnd();
		
	}

	public void onAnimationEnd(android.view.animation.Animation arg0) {
		
		_endCount ++;
		
		if(_endCount == _submitCount){
			if(_listener != null){
				_listener.onEnd();
			}
		}
		
	}

	public void onAnimationRepeat(android.view.animation.Animation arg0) {
		
	}

	public void onAnimationStart(android.view.animation.Animation arg0) {
		
		if(_startCount == 0){
			if(_listener != null){
				_listener.onStart();
			}
		}
		
		_startCount ++;
	}
}
