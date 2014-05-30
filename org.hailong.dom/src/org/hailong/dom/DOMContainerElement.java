package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DOMContainerElement extends DOMViewElement implements DOMDocumentView.OnElementActionListener,DOMDocumentView.OnElementVisableListener {

	private DOMContainerView.Adapter _adapter = null;
	private int _scrollX = 0;
	private int _scrollY = 0;
	private DOMContainerView _containerView = null;
	
	protected DOMContainerView.Adapter getAdapter(){
		if(_adapter == null){
			_adapter = new DOMContainerView.Adapter(){

				private List<DOMContainerItemView> _documentViews;
				
				@Override
				public int getCount() {
					return getChildCount();
				}

				@Override
				public Object getItem(int position) {
					return getChildAt(position);
				}

				@Override
				public Rect getItemRect(int position, Object object) {
	
					if(object instanceof IDOMLayoutElement){
						float displayScale = getDocument().getBundle().displayScale();
						Rect frame = ((IDOMLayoutElement) object).getFrame();
						return new Rect(frame.getX() * displayScale,frame.getY() * displayScale
								,frame.getWidth() * displayScale,frame.getHeight() * displayScale);
					}
					
					return new Rect();
				}


				@Override
				public boolean isViewFromObject(View view,int position, Object element) {
					DOMContainerItemView documentView = (DOMContainerItemView) view; 
					return documentView.getElement() == element;
				}
				
	            public void destroyItem(ViewGroup container, int position,Object element) {
					
					if(_documentViews != null) {
						for(DOMContainerItemView documentView : _documentViews){
							if(documentView.getElement() == element){
								documentView.setElement(null);
								documentView.setOnElementActionListener(null);
								documentView.setOnElementVisableListener(null);
							}
						}
					}
				}
				
				@Override  
	            public View instantiateItemView(ViewGroup container, int position,Object object) {  
					
					DOMElement element = (DOMElement) object;
					
					DOMContainerItemView documentView = null;
					
					String reuse = element.getAttributeValue("reuse");
					
					if(_documentViews != null) {
						
						for(DOMContainerItemView docView : _documentViews){
							
							if(docView.getElement() == null 
									&& (docView.reuse == reuse || (reuse != null && reuse.equals(docView.reuse)))){
								documentView = docView;
								break;
							}

						}
						
					}
					
					if(documentView == null){
						documentView = new DOMContainerItemView(container.getContext());
						documentView.reuse = reuse;
						if(_documentViews == null){
							_documentViews = new ArrayList<DOMContainerItemView>(4);
						}
						_documentViews.add(documentView);
					}
					
					documentView.setElement(element);
					
					documentView.setOnElementActionListener(DOMContainerElement.this);
					
					documentView.setOnElementVisableListener(DOMContainerElement.this);
					
					IDOMViewEntity entity = getViewEntity();
					
					if(entity != null){
						entity.elementVisable(documentView, element);
					}
					
	                return documentView;
	            }

				@Override
				public boolean isVisable(Rect rect) {
					float displayScale = getDocument().getBundle().displayScale();
					Rect frame = getFrame();
					int left = _scrollX;
					int top = _scrollY;
					int right = left + (int) (frame.getWidth() * displayScale +0.999999f);
					int bottom = top + (int) (frame.getHeight() * displayScale +0.999999f);
			
					left = Math.max(left,(int) (rect.getX() + 0.999999f));
					top = Math.max(top,(int) (rect.getY() + 0.999999f));
					right = Math.min(right,(int) (rect.getX() + rect.getWidth() + 0.999999f));
					bottom = Math.min(bottom,(int) (rect.getY() + rect.getHeight() + 0.999999f));
					
					return right - left > 0 && bottom - top > 0;
				}

				
				};
		}
		return _adapter;
	}
	
	public void setScroll(int scrollX,int scrollY){
		_scrollX = scrollX;
		_scrollY = scrollY;
		DOMContainerView v = getContentView();
		if(v != null){
			v.reloadData(false);
		}
	}
	public void setView(View view){
		
		DOMContainerView v = getContentView();
		
		if(v != null){
			v.setAdapter(null);
			_containerView = null;
			_scrollX = _scrollY = 0;
		}
		
		super.setView(view);
		
		v = getContentView();
		
		if(v != null){
			
			if(isLayouted()){
				v.setAdapter(getAdapter());
			}
		}
	}
	
	public DOMContainerView getContentView(){
		if(_containerView == null){
			View v = getView();
			if(v instanceof ViewGroup){
				_containerView = new DOMContainerView(v.getContext());
				((ViewGroup)v).addView(_containerView);
			}
		}
		return _containerView;
	}
	
	protected Size onLayoutChildren(Edge padding){
		return super.layoutChildren(padding);
	}
	
	public Size layoutChildren(Edge padding){
		
		Size contentSize = onLayoutChildren(padding);
		
		if(isViewLoaded()){
			
			DOMContainerView v = getContentView();
			
			if(v != null){
				if(v.getAdapter() == null){
					v.setAdapter(getAdapter());
				}
				else {
					v.getAdapter().notifyDataSetChanged();
				}
			}
		}
		
		return contentSize;
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
		
		@Override
		protected void drawElement(Canvas canvas,Size size,DOMElement element,float displayScale){
			super.drawElement(canvas, size, element, displayScale);
		}
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

}
