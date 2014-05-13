package org.hailong.dom;

import org.hailong.core.Rect;
import org.hailong.core.Size;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class DOMDocumentView extends ViewGroup implements IDOMViewEntity{

	private DOMElement _element;

	
	public DOMDocumentView(Context context) {
		super(context);

	}
	
	public DOMDocumentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	public DOMDocumentView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DOMElement getElement(){
		return _element;
	}
	
	public void setElement(DOMElement element){
		if(_element != element){
			
			if(_element != null && _element.getViewEntity() == this){
				_element.setViewEntity(null);
			}
			
			_element = element;
			
			if(_element != null){
				_element.setViewEntity(this);
			}
		}
	}
	
	protected void drawElement(Canvas canvas,Size size,DOMElement element){
		
		Rect r = null;
		
		if(element instanceof IDOMLayoutElement){
			
			if(element instanceof IDOMCanvasElement){

				if(((IDOMCanvasElement) element).isHidden()){
					canvas.restore();
					return;
				}
			
			}
				
			r = ((IDOMLayoutElement) element).getFrame();
			
			int left = r.getX() ;
			int top = r.getY();
			int right = left + r.getWidth();
			int bottom = top + r.getHeight();
			
			right = Math.min(right, size.getWidth());
			bottom = Math.min(bottom, size.getHeight());
			
			int width = right - left;
			int height = bottom - top;
			
			if(width >0 && height >0){
			
				canvas.save();
				
				canvas.translate(left, top);
				
				if(element instanceof IDOMCanvasElement){
					
					float radius = ((IDOMCanvasElement)element).getCornerRadius();
					
					if(radius == 0.0f){
						canvas.clipRect(0, 0, width, height);
					}
					else {
						Path path = new Path();
						path.addRoundRect(new RectF(0,0,width,height), radius, radius, Path.Direction.CW);
						canvas.clipPath(path);
					}

					((IDOMCanvasElement) element).draw(canvas);
					
				}
				else {
					canvas.clipRect(0, 0, width, height);
				}
				
				Size s = new Size(width,height);
				
				for(DOMElement child : element.getChilds()){
					drawElement(canvas,s, child);
				}
				
				canvas.restore();
			}
			
		}
		
		
	}
	

	public void draw(Canvas canvas){
		super.draw(canvas);
		
		if(_element != null){
			drawElement(canvas,new Size(getWidth(),getHeight()),_element);
		}
	}

	@Override
	public void doAction(DOMElement element) {
		
	}

	@Override
	public void doNeedsDisplay(DOMElement element) {
		invalidate();
	}
	
	public Rect converRect(Rect rect, DOMElement ofElement,DOMElement toElement) {
		
		DOMElement el = ofElement;
	    
	    Rect rs = rect;
	    
	    while(el != null && el != toElement){
	        
	    	if(el instanceof IDOMLayoutElement){
	    	
		    	Rect r = ((IDOMLayoutElement) el).getFrame();
		    	
		    	rs = new Rect(rs.getX() + r.getX(),rs.getY() + r.getY(),rs.getWidth(),rs.getHeight());
		    	
	    	}
	    	
	    	el = el.getParent();
	    }
	    
	    return el != null ? rs : rect;
	}
	
	@Override
	public View elementViewOf(DOMElement element, Class<?> viewClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		
	}
}
