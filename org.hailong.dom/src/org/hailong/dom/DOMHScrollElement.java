package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import org.hailong.dom.DOMHScrollElement.ScrollView.OnScrollListener;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class DOMHScrollElement extends DOMContainerElement {
	
	public Size onLayoutChildren(Edge padding){
		
		Rect frame = getFrame();
		
		Size contentSize = new Size(0,frame.getHeight());

		float height = frame.getHeight() - padding.getTop() - padding.getBottom();
		
		for(DOMElement element : getChilds()){
			
			if(element instanceof IDOMLayoutElement){
				
				IDOMLayoutElement layoutElement = (IDOMLayoutElement) element;
				
				Edge margin = layoutElement.getMargin();
				
				layoutElement.layout(new Size(frame.getWidth(), height - margin.getTop() - margin.getBottom()));
				
				Rect r = layoutElement.getFrame();
				
				r.y = padding.getTop() + margin.getTop();
				r.x = padding.getLeft() + margin.getLeft() + contentSize.getWidth();
				r.height = height - margin.getLeft() - margin.getRight();
				
				contentSize.width = contentSize.getWidth() + r.getWidth() + margin.getLeft() + margin.getRight();
				
			}
			
		}

		contentSize.width = contentSize.getWidth() + padding.getLeft() + padding.getRight();
		
		if(contentSize.getWidth() < frame.getWidth()){
			contentSize.width = frame.getWidth();
		}
		
		return contentSize;
	}
	
	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","org.hailong.dom.DOMHScrollElement$ScrollView"));
			
			if( ! ScrollView.class.isAssignableFrom(clazz)){
				return ScrollView.class;
			}
	
		} catch (ClassNotFoundException e) {
			Log.e(DOM.TAG, Log.getStackTraceString(e));
		}
	
		return ScrollView.class;
	}
	
	public void setView(View view){
		super.setView(view);
		
		if(view != null && view instanceof ScrollView){
			((ScrollView)view).setOnScrollListener(new OnScrollListener(){

				@Override
				public void onScroll(int scrollX, int scrollY) {
					setScroll(scrollX, scrollY);
				}});
		}
		
	}
	
	public static class ScrollView extends android.widget.HorizontalScrollView{

		private OnScrollListener _OnScrollListener;
		
		public ScrollView(Context context) {
			super(context);
			setHorizontalScrollBarEnabled(false);
		}
		
		public OnScrollListener getOnScrollListener(){
			return _OnScrollListener;
		}
		
		public void setOnScrollListener(OnScrollListener onScrollListener){
			_OnScrollListener = onScrollListener;
		}
		
		protected void onScrollChanged(int l,int t,int oldl,int oldt){
			super.onScrollChanged(l, t, oldl, oldt);
			
			if(_OnScrollListener != null){
				_OnScrollListener.onScroll(l, t);
			}
		}
		
		
		public static interface OnScrollListener {
			
			public void onScroll(int scrollX,int scrollY);
			
		}
		
	}
	
}
