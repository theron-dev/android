package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import org.hailong.view.ScrollView;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


public class DOMContainerElement extends DOMViewElement implements DOMDocumentView.OnActionListener ,DOMContainerView.OnScrollListener{

	private List<DOMContainerItemView> _dequeueItemViews;
	
	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","org.hailong.dom.DOMContainerView"));
			
			if( ! DOMContainerView.class.isAssignableFrom(clazz)){
				return DOMContainerView.class;
			}
	
		} catch (ClassNotFoundException e) {
			Log.e(DOM.TAG, Log.getStackTraceString(e));
		}
	
		return DOMContainerView.class;
	}
	
	public void setView(View view){
		
		DOMContainerView v = getContentView();
		
		if(v != null){
			v.setOnScrollListener(null);
		}
		
		super.setView(view);
		
		v = getContentView();
		
		if(v != null){
			v.setOnScrollListener(this);
		}
	}
	
	public DOMContainerView getContentView(){
		return (DOMContainerView) getView();
	}
	
	protected void onViewEntityChanged(IDOMViewEntity viewEntity){
		super.onViewEntityChanged(viewEntity);
		reloadData(true);
	}
	
	public Size layoutChildren(Edge padding){
		
		Size contentSize = super.layoutChildren(padding);
		
		if(isViewLoaded()){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			ScrollView contentView = getContentView();
			
			contentView.setContentSize((int) (contentSize.getWidth() * displayScale), (int) (contentSize.getHeight() * displayScale));
			
			reloadData(true);
		}
		
		return contentSize;
	}
	
	public void reloadData(boolean reload){

		if(isViewLoaded()){
			
			ScrollView contentView = getContentView();
			
			DOMBundle bundle = getDocument().getBundle();
			
			Context context = bundle.getContext();
			
			float displayScale = bundle.displayScale();
			
			float offsetX = contentView.getContentOffsetX();
			float offsetY = contentView.getContentOffsetY();
			
			SparseArray<DOMContainerItemView> itemViews = new SparseArray<DOMContainerItemView>();
			
			int c = contentView.getChildCount();
			int index = 0;
			
			for(index=0;index<c;index++){
				
				View v = contentView.getChildAt(index);
				
				if(v instanceof DOMContainerItemView){
					itemViews.put(((DOMContainerItemView) v).getIndex(), (DOMContainerItemView) v);
				}
				
			}
			
			if(_dequeueItemViews == null){
				_dequeueItemViews = new ArrayList<DOMContainerItemView>(4);
			}
			
			c = getChildCount();
			
			index = 0;
			

			for(DOMElement element : getChilds()){
				
				if(element instanceof IDOMLayoutElement){
					
					Rect r = ((IDOMLayoutElement) element).getFrame();
					
					if(isVisableRect(r, offsetX, offsetY,displayScale)){
						
						DOMContainerItemView itemView = itemViews.get(index);
						
						if(itemView == null){
							
							String reuse = element.getAttributeValue("reuse");
							
							int i = 0;
							
							for(DOMContainerItemView v :_dequeueItemViews){
								
								if(reuse == v.reuse || (reuse!= null && reuse.equals(v.reuse))){
									itemView = v;
									_dequeueItemViews.remove(i);
									break;
								}
								
								i ++;
								
							}

						}
						
						if(itemView == null){
							itemView = new DOMContainerItemView(context);
							itemView.setOnActionListener(this);
						}
						
						itemView.setLeft((int) (r.getX() * displayScale));
						itemView.setTop((int) (r.getY() * displayScale));
						itemView.setRight((int) ((r.getX() + r.getWidth()) * displayScale));
						itemView.setBottom((int) ((r.getY() + r.getHeight()) * displayScale));
						
						if(itemView.getParent() == null){
							contentView.addView(itemView);
						}
						
						if(itemView.getElement() != element){
							itemView.setElement(element);
						}
						else if(reload){
							itemView.invalidate();
						}
						
						onElementVisable(element,index,itemView);
					}
					else {
						
						DOMContainerItemView itemView = itemViews.get(index);
		             
		                if(itemView != null){
		                	itemView.setIndex(-1);
		                	itemView.setElement(null);
		                	_dequeueItemViews.add(itemView);
		                	itemViews.remove(index);
		                }
					}
					index ++;
				}
			}
			

			for(DOMContainerItemView itemView :_dequeueItemViews){
				itemView.removeFromParent();
			}
			
			c = itemViews.size();
			
			for(int i=0;i<c;i++){
				
				DOMContainerItemView itemView = itemViews.valueAt(i);
				itemView.setIndex(-1);
				itemView.setElement(null);
				_dequeueItemViews.add(itemView);
				itemView.removeFromParent();
			}

		}

	}
	
	public static class DOMContainerItemView extends DOMDocumentView {

		public String reuse;
		
		public DOMContainerItemView(Context context) {
			super(context);
			
		}
		
		public void removeFromParent(){
			ViewParent parent = getParent();
			if(parent instanceof ViewGroup){
				((ViewGroup) parent).removeView(this);
			}
		}
		
		public int getIndex(){
			Integer v = (Integer) getTag(R.id.index);
			return v == null ? -1 : v;
		}
		
		public void setIndex(int index){
			setTag(R.id.index, index);
		}
	}
	
	protected boolean isVisableRect(Rect frame,float offsetX,float offsetY,float displayScale){

		Rect r = getFrame();
		
		float left = Math.max(offsetX, frame.getX() * displayScale);
		float top = Math.max(offsetY, frame.getY() * displayScale);
		float right = Math.min(offsetX + r.getWidth() * displayScale, (frame.getX() + frame.getWidth()) * displayScale);
		float bottom = Math.min(offsetY + r.getHeight() * displayScale, (frame.getY() + frame.getHeight()) * displayScale);
		
		return right - left > 0.0f && bottom - top >0.0f;
	}

	@Override
	public void onAction(DOMDocumentView documentView, DOMElement element) {
		
		getViewEntity().doAction(element);
		
	}

	protected void onElementVisable(DOMElement element,int index,DOMContainerItemView itemView){
		
	}

	@Override
	public void onScroll(DOMContainerView containerView, int scrollX,
			int scrollY) {
		reloadData(false);
	}

}
