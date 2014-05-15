package org.hailong.dom;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;

public class DOMDocumentView extends ViewGroup implements IDOMViewEntity{

	private DOMElement _element;
	private boolean _allowAutoLayout;
	private Size _layoutSize;
	private Map<String,View> _viewsById;
	private Map<String,List<View>> _viewsByReuse;
	private Map<String,View> _creatorViewsById;
	private Map<String,List<View>> _creatorViewsByReuse;
	private Map<String,View> _dequeueViewsById;
	private Map<String,List<View>> _dequeueViewsByReuse;
	private OnActionListener _onActionListener;
	
	private void DOMDocumentViewInit(Context context,AttributeSet attrs){
		
		setWillNotDraw(false);
		
		if(attrs != null){
			_allowAutoLayout = attrs.getAttributeBooleanValue(null, "allowAutoLayout", false);
		}
		
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if(_element != null){
					float displayScale = _element.getDocument().getBundle().displayScale();
					return elementOnTouch(_element,view,event,0.0f,0.0f,displayScale);
				}
				return false;
			}
			
		});
	}
	
	public DOMDocumentView(Context context) {
		super(context);
		DOMDocumentViewInit(context,null);
	}
	
	public DOMDocumentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		DOMDocumentViewInit(context,attrs);
	}

	
	public DOMDocumentView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		DOMDocumentViewInit(context,attrs);
	}

	public DOMElement getElement(){
		return _element;
	}
	
	protected void resetEntityView(){
		if(_element != null){
			
			_creatorViewsById = new HashMap<String,View>(4);
			_creatorViewsByReuse = new HashMap<String,List<View>>(4);
			
			_dequeueViewsById = _viewsById;
			_dequeueViewsByReuse = _viewsByReuse;
			
			_viewsById = null;
			_viewsByReuse = null;
			
			_element.setViewEntity(this);
			
			if(_dequeueViewsById != null){
				
				for(View v : _dequeueViewsById.values()){
					removeView(v);
				}
				
				_dequeueViewsById = null;
			}
			
			if(_dequeueViewsByReuse != null){
				for(List<View> views : _dequeueViewsByReuse.values()){
					for(View v : views){
						removeView(v);
					}
				}
				_dequeueViewsByReuse = null;
			}
			
			_viewsById = _creatorViewsById;
			_viewsByReuse = _creatorViewsByReuse;
			
			_creatorViewsById = null;
			_creatorViewsByReuse = null;
			
		}
	}
	
	public void setElement(DOMElement element){
		if(_element != element){
			
			if(_element != null && _element.getViewEntity() == this){
				_element.setViewEntity(null);
			}
			
			_element = element;
			
			if(_element != null){
				
				_layoutSize = null;
				
				if(! _allowAutoLayout){
					resetEntityView();
				}

			}
			
			
			invalidate();
		}
	}
	
	protected boolean elementOnTouch(DOMElement element, View view,MotionEvent event,float dx,float dy,float displayScale){
		
		if(element != null && element instanceof IDOMLayoutElement 
				&& ((IDOMLayoutElement) element).isLayouted()){
			
			if(element instanceof IDOMCanvasElement && ((IDOMCanvasElement) element).isHidden()){
				return false;
			}
			
			Rect r = ((IDOMLayoutElement) element).getFrame();
			
			float x = dx + (element == _element ? 0.0f : r.getX() * displayScale);
			float y = dy + (element == _element ? 0.0f : r.getY() * displayScale);

			float px = event.getX();
			float py = event.getY();
			
			int action = event.getAction();
			
			if(action == MotionEvent.ACTION_DOWN){
				
				float width = r.getWidth() * displayScale;
				float height = r.getHeight() * displayScale;

				if(px >= x && px < x + width && py >= y && py < y + height){
					
					int count = element.getChildCount();
					
					boolean rs = false;
					
					for(int i=count-1;i >=0;i--){

						rs = elementOnTouch(element.getChildAt(i),view,event,x,y,displayScale);
						
						if(rs){
							break;
						}
						
					}

					if(! rs && element instanceof IDOMControlElement){
						 return ((IDOMControlElement) element).onTouch(this, action,px - x,py - y);
					}
					
					return rs;
				}
				
				return false;
			}
			else {
				
				if(element instanceof IDOMControlElement){
					((IDOMControlElement) element).onTouch(this, action,px - x,py - y);
				}
				
				int count = element.getChildCount();

				for(int i=0;i < count;i++){

					elementOnTouch(element.getChildAt(i),view,event,x,y,displayScale);
					
				}
				
				return true;
			}
			
		}
		
		return false;
	}
	
	protected void drawElement(Canvas canvas,Size size,DOMElement element,float displayScale){
		
		Rect r = null;
		
		if(element instanceof IDOMLayoutElement && ((IDOMCanvasElement) element).isLayouted() ){
			
			if(element instanceof IDOMCanvasElement){

				if(((IDOMCanvasElement) element).isHidden()){
					return;
				}
			
			}
				
			r = ((IDOMLayoutElement) element).getFrame();
			
			float left = element == _element ? 0.0f :r.getX() ;
			float top = element == _element ? 0.0f : r.getY();
			float right = left + r.getWidth();
			float bottom = top + r.getHeight();
			
			right = Math.min(right, size.getWidth());
			bottom = Math.min(bottom, size.getHeight());
			
			float width = right - left;
			float height = bottom - top;
			
			if(width >0 && height >0){
			
				canvas.save();
				
				canvas.translate(left * displayScale, top * displayScale);
				
				if(element instanceof IDOMCanvasElement){
					
					float radius = ((IDOMCanvasElement)element).getCornerRadius();
					
					if(radius == 0.0f){
						canvas.clipRect(0, 0, width * displayScale, height * displayScale);
					}
					else {
						Path path = new Path();
						path.addRoundRect(new RectF(0,0,width * displayScale,height * displayScale), radius * displayScale, radius * displayScale, Path.Direction.CW);
						canvas.clipPath(path);
					}

					((IDOMCanvasElement) element).draw(canvas);
					
				}
				else {
					canvas.clipRect(0, 0, width * displayScale, height * displayScale);
				}
				
				Size s = new Size(width,height);
				
				for(DOMElement child : element.getChilds()){
					drawElement(canvas,s, child,displayScale);
				}
				
				canvas.restore();
			}
			
		}
		
		
	}
	

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		if(_element != null){
			float displayScale = _element.getDocument().getBundle().displayScale();
			drawElement(canvas,new Size(getWidth() / displayScale,getHeight() / displayScale),_element,displayScale);
		}
		
	}
	
	

	@Override
	public void doAction(DOMElement element) {
		
	}

	@Override
	public void doNeedsDisplay(DOMElement element) {
		invalidate();
	}
	
	@Override
	public View elementViewOf(DOMElement element, Class<?> viewClass) {
		
		String id = element.stringValue("id", null);
		String reuse = element.stringValue("reuse", null);
		
		View view = null;
		
		if(id != null && _dequeueViewsById != null){
			view = _dequeueViewsById.get(id);
			if(view != null){
				if(view.getClass() == viewClass){
					_dequeueViewsById.remove(id);
				}
				else {
					view = null;
				}
			}
		}
		
		if(view == null && reuse != null && _dequeueViewsByReuse != null){
			
			List<View> views = _dequeueViewsByReuse.get(reuse);
			
			if(views != null){
				
				int index = 0;
				
				for(View v : views){
					
					if(v.getClass() == viewClass){
						view = v;
						break;
					}
					
					index ++;
				}
				
				views.remove(index);
			}
			
		}
		
		if(view == null){
		
			try {
				
				Constructor<?> constructor =  viewClass.getConstructor(Context.class);
				
				view = (View) constructor.newInstance(getContext());
				
			} catch (Exception e) {
				Log.d(DOM.TAG, Log.getStackTraceString(e));
			} 

		}
		
		if(view == null){
			view = new View(getContext());
		}
		
		if(id != null && _creatorViewsById != null){
			
			_creatorViewsById.put(id, view);
			
		}
		else if(reuse != null && _creatorViewsByReuse != null){
			
			List<View> views = _creatorViewsByReuse.get(reuse);
			
			if(views == null){
				views = new ArrayList<View>(4);
			}
			
			views.add( view );
		}
		
		float x = 0.0f,y = 0.0f,width = Float.MAX_VALUE,height = Float.MAX_VALUE;
		
		DOMElement el = element;
		
		while(el != null && el != _element){
			
			if(el instanceof IDOMLayoutElement){
				
				Rect r = ((IDOMLayoutElement) el).getFrame();
				
				x += r.getX();
				y += r.getY();
				
				if(width == Float.MAX_VALUE){
					width = r.getWidth();
				}
				
				if(height == Float.MAX_VALUE){
					height = r.getHeight();
				}
				
			}
			
			el = el.getParent();
		}
		
		float displayScale = _element.getDocument().getBundle().displayScale();

		view.setLeft((int) (x * displayScale));
		view.setTop((int) (y * displayScale));
		view.setRight((int) ((x + width) * displayScale));
		view.setBottom((int) ((y + height) * displayScale));
		
		addView(view);
		
		return view;
	}

	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	      
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
	  
	    measureChildren(widthMeasureSpec, heightMeasureSpec);   
	    
	    setMeasuredDimension(widthSize, heightSize);  
	}  

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if(_allowAutoLayout && _element != null ){
			
			float displayScale = _element.getDocument().getBundle().displayScale();
			float width = (r - l) / displayScale;
			float height = (b - t) / displayScale;
			
			if(_layoutSize == null || _layoutSize.getWidth() != width 
					|| _layoutSize.getHeight() != height){
			
				if(_layoutSize == null){
					_layoutSize = new Size(width,height);
				}
				else {
					_layoutSize.width = width;
					_layoutSize.height = height;
				}
				
				if(_element instanceof IDOMLayoutElement){
					((IDOMLayoutElement) _element).layout(_layoutSize);
				}
				
				resetEntityView();
			}

		}
		
		int c = getChildCount();
		
		for(int i=0;i<c;i++){
			View v = getChildAt(i);
			v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		}
	
	}
	
	@ExportedProperty
	public boolean isAllowAutoLayout(){
		return _allowAutoLayout;
	}
	
	public void setAllowAutoLayout(boolean allowAutoLayout){
		_allowAutoLayout = allowAutoLayout;
	}

	public OnActionListener getOnActionListener(){
		return _onActionListener;
	}
	
	public void setOnActionListener(OnActionListener onActionListener){
		_onActionListener = onActionListener;
	}
	
	public static interface OnActionListener {
		
		public void onAction(DOMDocumentView documentView , DOMElement element);
		
	}
	
}
