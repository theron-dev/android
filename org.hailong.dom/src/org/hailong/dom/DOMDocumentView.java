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
import android.view.ViewParent;

public class DOMDocumentView extends ViewGroup implements IDOMViewEntity{

	private DOMElement _element;
	private boolean _allowAutoLayout;
	private Size _layoutSize;
	
	private ViewContainer _viewContainer;
	private ViewContainer _creatorViewContainer;
	private ViewContainer _dequeueViewContainer;
	
	private OnElementVisableListener _onElementVisableListener;
	private OnElementActionListener _onElementActionListener;
	
	private void DOMDocumentViewInit(Context context,AttributeSet attrs){
		
		setWillNotDraw(false);

		if(attrs != null){
			_allowAutoLayout = attrs.getAttributeBooleanValue(null, "allowAutoLayout", false);
		}
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
	
	public boolean onTouchEvent (MotionEvent event){
		if(_element != null){
			float displayScale = _element.getDocument().getBundle().displayScale();
			if(elementOnTouch(_element,this,event,0.0f,0.0f,displayScale)){
				return true;
			}
		}
		return super.onTouchEvent(event);
	}
	
	
	public void setElement(DOMElement element){
		if(_element != element){
			
			if(_element != null && _element.getViewEntity() == this){
				_element.setViewEntity(null);
			}
			
			_element = element;
			
			if(_element != null){
				
				if(_layoutSize == null){
					_layoutSize = new Size(getMeasuredWidth(),getMeasuredHeight());
				}
				else {
					_layoutSize.width = getMeasuredWidth();
					_layoutSize.height = getMeasuredHeight();
				}


				if(_allowAutoLayout && _element instanceof IDOMLayoutElement){
					((IDOMLayoutElement) _element).layout(_layoutSize);
				}
				
				_creatorViewContainer = new ViewContainer();
		
				_dequeueViewContainer = _viewContainer;
	
				_viewContainer = null;
				
				_element.setViewEntity(this);
				
				if(_dequeueViewContainer != null){
					
					_dequeueViewContainer.removeAllView();
					
					_dequeueViewContainer = null;
				}
				
				_viewContainer = _creatorViewContainer;
				
				_creatorViewContainer = null;

			}
			
			
			invalidate();
		}
	}
	
	protected boolean elementOnTouch(DOMElement element, View view,MotionEvent event,float dx,float dy,float displayScale){
		
		if(element != null && element.isViewEntity(this) 
				&& element instanceof IDOMLayoutElement 
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
		
		if(element != null  && element.isViewEntity(this)  
				&& element instanceof IDOMLayoutElement && ((IDOMLayoutElement) element).isLayouted() ){
			
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
						path.addRoundRect(new RectF(0,0,width * displayScale,height * displayScale)
							, radius * displayScale, radius * displayScale, Path.Direction.CW);
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
			drawElement(canvas,new Size(getMeasuredWidth() / displayScale,getMeasuredHeight() / displayScale),_element,displayScale);
		}
		
	}
	
	public class ActionRunnable implements Runnable{

		private IDOMViewEntity _viewEntity;
		private DOMElement _element;
		
		public ActionRunnable(IDOMViewEntity viewEntity,DOMElement element){
			_viewEntity = viewEntity;
			_element = element;
		}
		
		@Override
		public void run() {
			if(_onElementActionListener != null){
				_onElementActionListener.onElementAction(DOMDocumentView.this, _viewEntity, _element);
			}
		}
		
	}

	@Override
	public void doAction(IDOMViewEntity viewEntity, DOMElement element) {
		if(_onElementActionListener != null){
			getHandler().postDelayed(new ActionRunnable(viewEntity, element), 10);
		}
	}

	@Override
	public void doNeedsDisplay(DOMElement element) {
		invalidate();
	}
	
	public Rect elementFrameConvert(DOMElement element){
		
		Rect r = null;
		
		DOMElement el = element;
		
		while(el != null && el != _element){
			
			if(el instanceof IDOMLayoutElement){
				
				Rect frame = ((IDOMLayoutElement) el).getFrame();
				
				if(r == null){
					r = new Rect(0.0f,0.0f,frame.getWidth(),frame.getHeight());
				}
				
				r.x = r.getX() + frame.getX();
				r.y = r.getY() + frame.getY();
				
			}
			
			el = el.getParent();
		}
		
		if(r == null && el == _element){
			if(el instanceof IDOMLayoutElement){
				Rect frame = ((IDOMLayoutElement) el).getFrame();
				r = new Rect(0.0f,0.0f,frame.getWidth(),frame.getHeight());
			}
		}
		
		if(r == null){
			r = new Rect(0.0f,0.0f,0.0f,0.0f);
		}
		
		return r;
	}
	
	@Override
	public View elementViewOf(DOMElement element, Class<?> viewClass) {
		
		String id = element.stringValue("id", null);
		String reuse = element.stringValue("reuse", null);
		
		View view = null;
		
		if(view == null && _dequeueViewContainer != null){
			view = _dequeueViewContainer.getView(id, reuse, viewClass);
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
		
		if(_creatorViewContainer != null){
			_creatorViewContainer.addView(view, id, reuse);
		}
		
		Rect r = elementFrameConvert(element);
		
		float displayScale = _element.getDocument().getBundle().displayScale();

		LayoutParams layoutParams = new LayoutParams((int) (r.getX() * displayScale), (int) (r.getY() * displayScale)
				, (int) ((r.getX() + r.getWidth()) * displayScale), (int) ((r.getY() + r.getHeight()) * displayScale));
		
		view.setLayoutParams(layoutParams);
		
		if(view.getParent() == null){
			addView(view, layoutParams);
		}
		
		return view;
	}

	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	      
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	    measureChildren(widthMeasureSpec, heightMeasureSpec);   

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

			}

		}
		
		int c = getChildCount();
		
		for(int i=0;i<c;i++){
			
			View v = getChildAt(i);
		 	
			ViewGroup.LayoutParams params = v.getLayoutParams();
			
		 	if(v.getVisibility() != GONE && params != null && params instanceof LayoutParams){
			
		 		LayoutParams layoutParams = (LayoutParams) params;
		 		v.layout(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
		 		
		 	}
		}

	}
	
	@ExportedProperty
	public boolean isAllowAutoLayout(){
		return _allowAutoLayout;
	}
	
	public void setAllowAutoLayout(boolean allowAutoLayout){
		_allowAutoLayout = allowAutoLayout;
	}

	public OnElementActionListener getOnElementActionListener(){
		return _onElementActionListener;
	}
	
	public void setOnElementActionListener(OnElementActionListener listener){
		_onElementActionListener = listener;
	}
	
	public OnElementVisableListener getOnElementVisableListener(){
		return _onElementVisableListener;
	}
	
	public void setOnElementVisableListener(OnElementVisableListener listener){
		_onElementVisableListener = listener;
	}
	
	
	public static interface OnElementActionListener {
		
		public void onElementAction(DOMDocumentView documentView ,IDOMViewEntity viewEntity, DOMElement element);
		
	}
	
	public static interface OnElementVisableListener {
		
		public void onElementVisable(DOMDocumentView documentView ,IDOMViewEntity viewEntity, DOMElement element);
		
	}

	@Override
	public void elementLayoutView(DOMElement element, View view) {

		Rect r = elementFrameConvert(element);
		
		float displayScale = _element.getDocument().getBundle().displayScale();

		LayoutParams layoutParams = new LayoutParams((int) (r.getX() * displayScale), (int) (r.getY() * displayScale)
				, (int) ((r.getX() + r.getWidth()) * displayScale), (int) ((r.getY() + r.getHeight()) * displayScale));
		
		view.setLayoutParams(layoutParams);
		
	}

	@Override
	public void elementDetach(DOMElement element) {
		if(_element == element){
			_element = null;
		}
	}

	public static class LayoutParams extends ViewGroup.LayoutParams{

		public final int left;
		public final int top;
		public final int right;
		public final int bottom;
		
		public LayoutParams(int left, int top ,int right,int bottom) {
			super(right - left, bottom - top);
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
		
	}

	@Override
	public void elementVisable(IDOMViewEntity viewEntity, DOMElement element) {
		if(_onElementVisableListener != null){
			_onElementVisableListener.onElementVisable(this, viewEntity, element);
		}
	}
	
	private static class ViewContainer extends Object {
		
		private Map<String,View> _viewsById;
		private Map<String,List<View>> _viewsByReuse;
		private List<View> _views;
		
		public void addView(View view, String id,String reuse){
			
			if(id != null){
				if(_viewsById == null){
					_viewsById = new HashMap<String,View>(4);
				}
				View v = _viewsById.put(id, view);
				if(v != null){
					if(_views == null){
						_views = new ArrayList<View>(4);
					}
					_views.add(v);
				}
			}
			else if(reuse != null){
				
				List<View> vs = null;
				if(_viewsByReuse == null){
					_viewsByReuse = new HashMap<String,List<View>>(4);
				}
				else {
					vs = _viewsByReuse.get(reuse);
				}
				
				if(vs == null){
					vs = new ArrayList<View>(4);
					_viewsByReuse.put(reuse, vs);
				}
				
				vs.add(view);
			}
			else {
				if(_views == null){
					_views = new ArrayList<View>(4);
				}
				_views.add(view);
			}
		}
		
		public View getView(String id, String reuse,Class<?> viewClass){
			
			View v = null;
			
			if(id != null && _viewsById != null){
				v = _viewsById.get(id);
				if(v != null && v.getClass() != viewClass){
					v = null;
				}
				else {
					_viewsById.remove(id);
				}
			}
			
			if(v == null && reuse != null && _viewsByReuse != null){
				
				List<View> vs = _viewsByReuse.get(reuse);
				
				if(vs != null ){
					
					int i = 0;
					
					for(View vv : vs){
						
						if(vv.getClass() == viewClass){
							v = vv;
							vs.remove(i);
							break;
						}
						i ++;
					}

				}
				
			}
			
			if(v == null && _views != null){
				
				int i = 0;
				
				for(View vv : _views){
					
					if(vv.getClass() == viewClass){
						v = vv;
						_views.remove(i);
						break;
					}
					i ++;
				}
			}
			
			return v;
		}
		
		public void removeAllView(){
			
			if(_viewsById != null){
				
				for(View v : _viewsById.values()){
					ViewParent parent = v.getParent();
					if(parent != null && parent instanceof ViewGroup){
						((ViewGroup)parent).removeView(v);
					}
				}
				
				_viewsById = null;
			}
			
			if(_viewsByReuse != null){
				
				for(List<View> vs : _viewsByReuse.values()){
					for(View v : vs){
						ViewParent parent = v.getParent();
						if(parent != null && parent instanceof ViewGroup){
							((ViewGroup)parent).removeView(v);
						}
					}
				}
				
				_viewsByReuse = null;
			}
			
			if(_views != null){
				
				for(View v : _views){
					ViewParent parent = v.getParent();
					if(parent != null && parent instanceof ViewGroup){
						((ViewGroup)parent).removeView(v);
					}
				}
				
				_views = null;
			}
			
		}
		
	}

}
