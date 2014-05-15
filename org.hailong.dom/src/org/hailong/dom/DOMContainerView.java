package org.hailong.dom;

import org.hailong.view.ScrollView;
import android.content.Context;
import android.view.View;

public class DOMContainerView extends ScrollView {

	private OnScrollListener _onScrollListener;
	
	public DOMContainerView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	
		super.onLayout(changed, l, t, r, b);
		
		int c = getChildCount();
		
		for(int i=0;i<c;i++){
			View v = getChildAt(i);
			Object index = v.getTag(R.id.index);
			if(index != null && index instanceof Number){
				v.layout(v.getLeft(), v.getTop(), v.getRight() - v.getLeft(), v.getBottom() - v.getTop());
			}
		}
	
		
	}
	
	public OnScrollListener getOnScrollListener(){
		return _onScrollListener;
	}
	
	public void setOnScrollListener(OnScrollListener listener){
		_onScrollListener = listener;
	}
	
	@Override
	public void scrollTo(int x,int y){
		super.scrollTo(x, y);
		if(	_onScrollListener != null){
			_onScrollListener.onScroll(this	, x, y);
		}
	}
	
	public static interface OnScrollListener {
		
		public void onScroll(DOMContainerView containerView,int scrollX,int scrollY);
		
		
	}
}
