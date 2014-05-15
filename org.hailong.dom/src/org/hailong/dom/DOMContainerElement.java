package org.hailong.dom;

import java.util.List;

import org.hailong.core.Edge;
import org.hailong.core.Size;
import org.hailong.view.ScrollView;

import android.content.Context;
import android.util.Log;

public class DOMContainerElement extends DOMViewElement {

	private List<DOMContainerItemView> _dequeueItemViews;
	
	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","org.hailong.view.ScrollView"));
			
			if( ! ScrollView.class.isAssignableFrom(clazz)){
				return ScrollView.class;
			}
	
		} catch (ClassNotFoundException e) {
			Log.e(DOM.TAG, Log.getStackTraceString(e));
		}
	
		return ScrollView.class;
	}
	
	public ScrollView getContentView(){
		return (ScrollView) getView();
	}
	
	protected void onViewEntityChanged(IDOMViewEntity viewEntity){
		super.onViewEntityChanged(viewEntity);
		reloadData();
	}
	
	public Size layoutChildren(Edge padding){
		
		Size contentSize = super.layoutChildren(padding);
		
		if(isViewLoaded()){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			ScrollView contentView = getContentView();
			
			contentView.setContentSize((int) (contentSize.getWidth() * displayScale), (int) (contentSize.getHeight() * displayScale));
			
			reloadData();
		}
		
		return contentSize;
	}
	
	public void reloadData(){

		if(isViewLoaded()){
			
			
			
		}

	}
	
	public static class DOMContainerItemView extends DOMDocumentView {

		public int index;
		public String reuse;
		
		public DOMContainerItemView(Context context) {
			super(context);
			
		}
		
	}
}
