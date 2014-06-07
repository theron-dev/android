package org.hailong.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.FrameLayout;

public class ActionView extends FrameLayout{

	private View _actionView = null;
	private int _actionColor = 0x55ffffff;
	private boolean _actionInset = false;
	private OnActionListener _OnActionListener;
	

	protected void _ActionView(Context context,AttributeSet attrs){
		
		 TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ActionView);

		 setActionColor(typedArray.getColor(R.styleable.ActionView_actionColor, _actionColor));
	    
		 typedArray.recycle();
	}

	public ActionView(Context context) {
		super(context);
	}

	public ActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		_ActionView(context,attrs);
		
	}

	public ActionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_ActionView(context,attrs);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN){
			_actionInset =true;
			if(_actionView == null){
				_actionView = new View(getContext());
				_actionView.setBackgroundColor(_actionColor);
				_actionView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
				addView(_actionView);
			}
			_actionView.setVisibility(View.VISIBLE);
			return true;
		}
		else if(action == MotionEvent.ACTION_MOVE){
			float x = event.getX();
			float y = event.getY();
			if(x >=0 && y >=0 && x < getMeasuredWidth() && y < getMeasuredHeight()){
				_actionInset = true;
				if(_actionView != null){
					_actionView.setVisibility(View.VISIBLE);
				}
				
			}
			else {
				_actionInset = false;
				if(_actionView != null){
					_actionView.setVisibility(View.GONE);
				}
			}
		}
		else if(action == MotionEvent.ACTION_UP){
			if(_actionInset){
				getHandler().post(new Runnable(){

					@Override
					public void run() {
						if(_OnActionListener != null){
							_OnActionListener.onAction(ActionView.this);
						}
					}});
			}
			_actionInset = false;
			if(_actionView != null){
				_actionView.setVisibility(View.GONE);
			}
		}
		else {
			_actionInset = false;
			if(_actionView != null){
				_actionView.setVisibility(View.GONE);
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	@ExportedProperty
	public int getActionColor(){
		return _actionColor;
	}
	
	public void setActionColor(int actionColor){
		_actionColor = actionColor;
		if(_actionView != null){
			_actionView.setBackgroundColor(_actionColor);
		}
	}
	
	public OnActionListener getOnActionListener(){
		return _OnActionListener;
	}
	
	public void setOnActionListener(OnActionListener actionListener){
		_OnActionListener = actionListener;
	}
	
	public static interface OnActionListener {
		public void onAction(ActionView actionView);
	}
}
