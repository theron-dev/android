package org.hailong.framework.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class LoadingView extends FrameLayout {

	protected boolean _animating;
	protected double _animationValue;
	
	public LoadingView(Context context) {
		super(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	public boolean isAnimating(){
		return _animating;
	}
	
	public void startAnimation(){
		
	}
	
	public void stopAnimation(){
		
	}
	
	public void setAnimationValue(double animationValue){
		_animationValue = animationValue;
	}
	
}
