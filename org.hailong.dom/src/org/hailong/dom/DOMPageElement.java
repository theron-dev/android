package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;

import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class DOMPageElement extends DOMViewElement implements DOMDocumentView.OnElementActionListener, DOMDocumentView.OnElementVisableListener{

	private PagerAdapter _pagerAdapter;
	private Runnable _loopsRunnable;
	private Handler _handler;
	
	public Class<?> getViewClass(){
		
		try {
			
			Class<?> clazz = Class.forName(stringValue("viewClass","android.support.v4.view.ViewPager"));
			
			if( ! ViewPager.class.isAssignableFrom(clazz)){
				return ViewPager.class;
			}
	
		} catch (ClassNotFoundException e) {
			Log.e(DOM.TAG, Log.getStackTraceString(e));
		}
	
		return ViewPager.class;
	}
	
	protected PagerAdapter getPagerAdapter(){
		if(_pagerAdapter == null){
			
			_pagerAdapter = new PagerAdapter() {
				
				private List<DOMDocumentView> _documentViews;
				
				@Override
				public boolean isViewFromObject(View view, Object element) {
					DOMDocumentView documentView = (DOMDocumentView) view; 
					return documentView.getElement() == element;
				}
				
	            public void destroyItem(ViewGroup container, int position,Object element) {
					
					if(_documentViews != null) {
						for(DOMDocumentView documentView : _documentViews){
							if(documentView.getElement() == element){
								if(documentView.getParent() == container){
									container.removeView(documentView);
								}
								documentView.setElement(null);
								documentView.setOnElementActionListener(null);
								documentView.setOnElementVisableListener(null);
							}
						}
					}
				}
				
				@Override  
	            public Object instantiateItem(ViewGroup container, int position) {  
					
					DOMElement element = getChildAt(position);
					
					DOMDocumentView documentView = null;
					
					if(_documentViews != null) {
						
						for(DOMDocumentView docView : _documentViews){
							
							if(docView.getElement() == null){
								documentView = docView;
								break;
							}

						}
						
					}
					
					if(documentView == null){
						documentView = new DOMDocumentView(container.getContext());
						if(_documentViews == null){
							_documentViews = new ArrayList<DOMDocumentView>(4);
						}
						_documentViews.add(documentView);
					}

					documentView.setLayoutParams(new ViewPager.LayoutParams());
					
					documentView.setElement(element);
					
					documentView.setOnElementActionListener(DOMPageElement.this);
					
					documentView.setOnElementVisableListener(DOMPageElement.this);
					
					IDOMViewEntity entity = getViewEntity();
					
					if(entity != null){
						entity.elementVisable(documentView, element);
					}
					
					container.addView(documentView);
					
	                return element;
	            }  
				
				@Override
				public int getCount() {
					return getChildCount();
				}
			};
			
		}
		return _pagerAdapter;
	}
	
	public void setView(View view){
		
		ViewPager v = getContentView();
		
		if(v != null){
			v.setAdapter(null);
		}
		
		super.setView(view);
		
		v = getContentView();
		
		if(v != null){
			
			v.setOffscreenPageLimit(1);
			v.setEnabled(true);
			
			if(isLayouted()){
				v.setAdapter(getPagerAdapter());
			}
			
			double loops = doubleValue("loops", 0);
			
			if(loops > 0.0){
				
				if(_handler == null){
					_handler = new Handler();
				}
				
				if(_loopsRunnable == null){
					_loopsRunnable = new Runnable() {
						
						@Override
						public void run() {
							
							ViewPager v = getContentView();
							
							if(v != null){
								
								int cur = v.getCurrentItem();
								
								if(cur + 1 < getChildCount()){
									cur ++;
								}
								else {
									cur = 0;
								}
								
								v.setCurrentItem(cur, true);
								
								double loops = doubleValue("loops", 0);
								
								if(loops > 0.0){
									_handler.postDelayed(_loopsRunnable, (long) (loops * 1000));
								}
							}
						}
					};
				}
				
				_handler.postDelayed(_loopsRunnable, (long) (loops * 1000));
			}
		}
	}

	
	public ViewPager getContentView(){
		return (ViewPager) getView();
	}
	
	public Size layoutChildren(Edge padding){
		
		Rect frame = getFrame();
		
		float width = frame.getWidth();
		float height = frame.getHeight();
		
		Size contentSize = new Size(0,height);

		for(DOMElement element : getChilds()){
			
			if(element instanceof IDOMLayoutElement){
				
				IDOMLayoutElement layoutElement = (IDOMLayoutElement) element;
				
				layoutElement.layout(new Size(width,height));
				
				Rect r = layoutElement.getFrame();
				
				r.x = contentSize.width;
				r.y = 0;
				r.width = width;
				r.height = height;
				
				contentSize.width = contentSize.getWidth() + width ;
				
			}
			
		}
		
		if(contentSize.getWidth() < width){
			contentSize.width = width;
		}

		if(isViewLoaded()){
	
			ViewPager contentView = getContentView();
			
			if(contentView.getAdapter() == null){
				contentView.setAdapter(getPagerAdapter());
			}
			else {
				contentView.getAdapter().notifyDataSetChanged();
			}
		}
		
		return contentSize;
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
