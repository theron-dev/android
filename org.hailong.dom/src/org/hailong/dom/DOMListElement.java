package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DOMListElement extends DOMViewElement implements ListAdapter , DOMDocumentView.OnActionListener{

	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","android.widget.ListView"));
			
			if( ! ListView.class.isAssignableFrom(clazz)){
				return ListView.class;
			}
	
		} catch (ClassNotFoundException e) {
			Log.e(DOM.TAG, Log.getStackTraceString(e));
		}
	
		return ListView.class;
	}
	
	public void setView(View view){
		
		ListView v = getContentView();
		
		if(v != null){
			v.setAdapter(null);
		}
		
		super.setView(view);
		
		v = getContentView();
		
		if(v != null){
			
			v.setScrollbarFadingEnabled(true);
			v.setScrollContainer(true);
			v.setVerticalScrollBarEnabled(true);

			v.setDividerHeight(0);
			v.setEnabled(true);
			v.setAdapter(this);
		}
	}

	
	public ListView getContentView(){
		return (ListView) getView();
	}
	
	public Size layoutChildren(Edge padding){
		
		Rect frame = getFrame();
		
		Size contentSize = new Size(frame.getWidth(),0);

		float width = frame.getWidth();
		
		for(DOMElement element : getChilds()){
			
			if(element instanceof IDOMLayoutElement){
				
				IDOMLayoutElement layoutElement = (IDOMLayoutElement) element;
				
				layoutElement.layout(new Size(width,frame.getHeight()));
				
				Rect r = layoutElement.getFrame();
				
				r.x = padding.getLeft();
				r.y = padding.getTop() + contentSize.getHeight();
				r.width = width;
				
				contentSize.height = contentSize.getHeight() + r.getHeight() ;
				
			}
			
		}

		contentSize.height = contentSize.getHeight();
		
		if(contentSize.getHeight() < frame.getHeight()){
			contentSize.height = frame.getHeight();
		}
		
		if(isViewLoaded()){
			
			ListView contentView = getContentView();
			
			contentView.setAdapter(this);
			
		}
		
		return contentSize;
	}
	

	@Override
	public int getCount() {
		return getChildCount();
	}

	@Override
	public Object getItem(int index) {
		return getChildAt(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public int getItemViewType(int index) {
		return 0;
	}

	@Override
	public View getView(int index, View contentView, ViewGroup viewGroup) {
	
		if(contentView == null){
			contentView = new DOMDocumentView(viewGroup.getContext());
		}

		DOMDocumentView documentView = (DOMDocumentView) contentView;
	
		DOMElement element = getChildAt(index);
		
		if(element instanceof IDOMLayoutElement){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			IDOMLayoutElement layoutElement = (IDOMLayoutElement) element;
			
			Rect r = layoutElement.getFrame();
			
			documentView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (r.getHeight() * displayScale)));
		
			documentView.setElement(element);
			
			documentView.setOnActionListener(this);
			
		}
		else {
			documentView.setLayoutParams(null);
			documentView.setElement(null);
			documentView.setOnActionListener(null);
		}
		
		return contentView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return getChildCount() ==0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int index) {
		return false;
	}

	@Override
	public void onAction(DOMDocumentView documentView, DOMElement element) {
		getViewEntity().doAction(element);
	}
	

}
