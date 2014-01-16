package org.hailong.framework.views;

import java.util.ArrayList;
import java.util.List;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;

public class Animation implements android.animation.ValueAnimator.AnimatorUpdateListener {

	private long _duration = 0;
	private AnimatorListener _listener = null;
	private List<AnimationItem<?>> _animationItems = null;
	private TimeInterpolator _timeInterpolator = null;
	
	public void addAnimationItem(AnimationItem<?> anim){
		
		if(anim != null){
			
			if(_animationItems == null){
				_animationItems = new ArrayList<AnimationItem<?>>(4);
			}
			
			_animationItems.add(anim);
		}

	}
	
	public void setListener(AnimatorListener listener){
		_listener = listener;
	}
	
	public void setDuration(long duration){
		_duration = duration;
	}
	
	public void setTimeInterpolator(TimeInterpolator timeInterpolator){
		_timeInterpolator = timeInterpolator;
	}
	
	public void translate(View view , float fromX,float toX,float fromY,float toY ){
		addAnimationItem( new AnimationItem<Float>(view,View.TRANSLATION_X, fromX, toX));
		addAnimationItem( new AnimationItem<Float>(view,View.TRANSLATION_Y, fromY, toY));
	}
	
	public void translateTo(View view , float toX,float toY ){
		translate(view,view.getX(),toX,view.getY(),toY);
		view.setX(toX);
		view.setY(toY);
	}
	
	public void scale(View view , float fromX,float toX,float fromY,float toY ){
		addAnimationItem( new AnimationItem<Float>(view,View.SCALE_X, fromX, toX));
		addAnimationItem( new AnimationItem<Float>(view,View.SCALE_Y, fromY, toY));
	}
	
	public void scaleTo(View view , float toX,float toY ){
		scale(view,view.getScaleX(),toX,view.getScaleY(),toY);
		view.setScaleX(toX);
		view.setScaleY(toY);
	}

	public void alpha(View view ,float fromAlpha,float toAlpha){
		addAnimationItem( new AnimationItem<Float>(view,View.ALPHA, fromAlpha, toAlpha));
	}
	
	public void alphaTo(View view,float toAlpha){
		alpha(view,view.getAlpha(),toAlpha);
		view.setAlpha(toAlpha);
	}
	
	public static class AnimationItem<T> {
		
		private View _view;
		private Property<View, T> _property;
		private T _fromValue;
		private T _toValue;
		
		public AnimationItem(View view,Property<View, T> property,T fromValue,T toValue){
			_view = view;
			_property = property;
			_fromValue = fromValue;
			_toValue = toValue;
		}
		
		public void setAnimationValue(float animationValue){
			_property.set(_view, getValue(animationValue));
		}
		
		@SuppressWarnings("unchecked")
		public T getValue(float animationValue){
			
			Class<T> type = _property.getType();
			
			if(type == Float.class){
				
				return (T) (Float) ( (Float)_fromValue + ((Float) _toValue -(Float)_fromValue) * animationValue);
			}
			else if(type == Integer.class){
				
				int fv = (Integer)_fromValue;
				int tv = (Integer)_toValue;
				
				return (T) (Integer) (int) ( fv + (tv - fv) * animationValue);
			}

			return _fromValue;
		}
	}

	public void submit(){
		ValueAnimator anim = ValueAnimator.ofFloat(0.0f,1.0f);
		anim.setDuration(_duration);
		anim.addUpdateListener(this);
		anim.addListener(_listener);
		anim.setInterpolator(_timeInterpolator);
		anim.start();
	}

	public void onAnimationUpdate(ValueAnimator valueAnimator) {
		if(_animationItems != null){
			for(AnimationItem<?> animationItem : _animationItems){
				animationItem.setAnimationValue((Float)valueAnimator.getAnimatedValue());
			}
		}
	}

	
}
