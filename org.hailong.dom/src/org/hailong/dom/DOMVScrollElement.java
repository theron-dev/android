package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import org.hailong.dom.DOMVScrollElement.ScrollView.OnScrollListener;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class DOMVScrollElement extends DOMContainerElement  {
	
	public Size onLayoutChildren(Edge padding){
		
		Rect frame = getFrame();
		
		Size contentSize = new Size(frame.getWidth(),0);

		float width = frame.getWidth() - padding.getLeft() - padding.getRight();
		
		for(DOMElement element : getChilds()){
			
			if(element instanceof IDOMLayoutElement){
				
				IDOMLayoutElement layoutElement = (IDOMLayoutElement) element;
				
				Edge margin = layoutElement.getMargin();
				
				layoutElement.layout(new Size(width - margin.getLeft() - margin.getRight(),frame.getHeight()));
				
				Rect r = layoutElement.getFrame();
				
				r.x = padding.getLeft() + margin.getLeft();
				r.y = padding.getTop() + margin.getTop() + contentSize.getHeight();
				r.width = width - margin.getLeft() - margin.getRight();
				
				contentSize.height = contentSize.getHeight() + r.getHeight() + margin.getTop() + margin.getBottom();
				
			}
			
		}

		contentSize.height = contentSize.getHeight() + padding.getTop() + padding.getBottom();
		
		if(contentSize.getHeight() < frame.getHeight()){
			contentSize.height = frame.getHeight();
		}
		
		setContentSize(contentSize);
		
		return contentSize;
	}
	
	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","org.hailong.dom.DOMVScrollElement$ScrollView"));
			
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
	
	public static class ScrollView extends android.widget.ScrollView{

		private OnScrollListener _OnScrollListener;
		
		public ScrollView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
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
