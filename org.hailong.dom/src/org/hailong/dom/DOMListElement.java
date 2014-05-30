package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class DOMListElement extends DOMViewElement implements ListAdapter , DOMDocumentView.OnElementActionListener, DOMDocumentView.OnElementVisableListener{

	private List<DataSetObserver> _dataSetObservers;
	
	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","org.hailong.dom.DOMListElement$ListView"));
			
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
			
			v.setDividerHeight(0);
			v.setEnabled(true);
			
			if(isLayouted()){
				v.setAdapter(this);
			}
			
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
			
			if(contentView.getAdapter() == null){
				contentView.setAdapter(this);
			}
			else {
				onChanged();
			}
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
			
			Rect frame = getFrame();
			
			documentView.setLayoutParams(new ListView.LayoutParams((int) (frame.getWidth() * displayScale), (int) (r.getHeight() * displayScale)));
			
			documentView.setElement(element);
			
			documentView.setOnElementActionListener(this);
			documentView.setOnElementVisableListener(this);
			
		}
		else {
			documentView.setLayoutParams(null);
			documentView.setElement(null);
			documentView.setOnElementActionListener(null);
			documentView.setOnElementVisableListener(null);
		}

		IDOMViewEntity entity = getViewEntity();
		
		if(entity != null){
			entity.elementVisable(documentView, element);
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

	
	public void onChanged(){
		
		if(_dataSetObservers != null){
			for(DataSetObserver observer : _dataSetObservers){
				observer.onChanged();
			}
		}
		
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) {	
		
		if(_dataSetObservers == null){
			_dataSetObservers = new ArrayList<DataSetObserver>(4);
		}
		
		_dataSetObservers.add(dataSetObserver);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
		if(_dataSetObservers != null){
			_dataSetObservers.remove(dataSetObserver);
		}
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int index) {
		return true;
	}


	@Override
	public void onElementAction(DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element) {
		
		IDOMViewEntity entity = getViewEntity();
		
		if(entity != null){
			entity.doAction(viewEntity,element);
		}
		
	}

	@Override
	public void onElementVisable(DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element) {
		
		IDOMViewEntity entity = getViewEntity();
		
		if(entity != null){
			entity.elementVisable(viewEntity, element);
		}
	}
	
	
	public static class ListView extends android.widget.ListView{

		public ListView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		
	}
}
