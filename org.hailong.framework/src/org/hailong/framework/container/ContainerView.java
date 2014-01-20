package org.hailong.framework.container;

import org.hailong.framework.views.AbsScrollView;
import org.hailong.framework.views.LoadingView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Adapter;

public class ContainerView extends AbsScrollView implements IContainerView {

	
	private Adapter _adapter;
	private LoadingView _topLoadingView;
	private LoadingView _bottomLoadingView;
	private Container _container;
	
	public ContainerView(Context context) {
		super(context);
		
	}
	
	public ContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	public void setAdapter(Adapter adpater){
		_adapter = adpater;
	}
	
	public Adapter getAdapter(){
		return _adapter;
	}
	
	public LoadingView getTopLoadingView(){
		return _topLoadingView;
	}
	
	public void setTopLoadingView(LoadingView loadingView){
		if(_topLoadingView != loadingView){
			if(_topLoadingView != null){
				ViewParent v = _topLoadingView.getParent();
				if(v != null && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_topLoadingView);
				}
			}
			_topLoadingView = loadingView;
			if(_topLoadingView != null){
				ViewParent v = _topLoadingView.getParent();
				if(v != null && v != this && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_topLoadingView);
				}
				else if(v == null || v != this){
					addView(_topLoadingView, 0);
				}
			}
		}
	}
	
	public LoadingView getBottomLoadingView(){
		return _bottomLoadingView;
	}
	
	public void setBottomLoadingView(LoadingView loadingView){
		
		if(_bottomLoadingView != loadingView){
		
			if(_bottomLoadingView != null){
				ViewParent v = _bottomLoadingView.getParent();
				if(v != null && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_bottomLoadingView);
				}
			}
			
			_bottomLoadingView = loadingView;
			
			if(_bottomLoadingView != null){
				ViewParent v = _bottomLoadingView.getParent();
				if(v != null && v != this && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_bottomLoadingView);
				}
				else if(v == null || v != this){
					addView(_bottomLoadingView,0);
				}
			}
		}
	
	}

	public Container getContainer(){
		
		if(_container == null){
			_container = new Container(this);
		}
		
		return _container;
	}
	 
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	
		int width = r - l;
		int height = b - t;
		
		if(_topLoadingView != null){
			int h =  _topLoadingView.getMeasuredHeight();
			int top = t  - h;
			_topLoadingView.layout(0, top, width, top + h);
		}
		
		if(_bottomLoadingView != null){
			int h =  _bottomLoadingView.getMeasuredHeight();
			int top = getContentSizeHeight();
			if(top < height){
				_bottomLoadingView.layout(0, 0, 0, 0);
			}
			else{
				_bottomLoadingView.layout(0, top, width, top + h);
			}
		}
	}
	
}
