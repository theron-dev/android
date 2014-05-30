package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class DOMImageElement extends DOMCanvasElement {

	private Drawable _image;
	private Drawable _defaultImage;
	private Object _imageLoader;
	
	public Drawable getImage(){
		
		if(_image == null && getDocument() !=null){
			_image = getDocument().getBundle().getImageForURI(getAttributeValue("src"));
		}
		
		return _image;
	}
	
	public void setImage(Drawable image){
		_image = image;
		setNeedsDisplay();
	}
	
	public Drawable getDefaultImage(){
		
		if(_defaultImage == null && getDocument() !=null){
			_defaultImage = getDocument().getBundle().getImageForURI(getAttributeValue("default-src"));
		}
		
		return _defaultImage;
	}
	
	public void setDefaultImage(Drawable defaultImage){
		_defaultImage = defaultImage;
	}
	
	public void setAttributeValue(String name,String value){
		super.setAttributeValue(name, value);
		
		if("default-src".equals("name")){
			_defaultImage = null;
			setNeedsDisplay();
		}
		else if("src".equals(name)){
			_image = null;
			_imageLoader = null;
			setNeedsDisplay();
		}
	}
	
	public Object getImageLoader(){
		return _imageLoader;
	}
	
	public void setImageLoader(Object imageLoader){
		_imageLoader = imageLoader;
	}
	
	@Override
	protected void onDrawElement(Canvas canvas){
		super.onDrawElement(canvas);
		
		Drawable image = getImage();
		
		if(image == null){
			image = getDefaultImage();
		}
		
		if(image != null){
			
			
			
			Rect r = getFrame();
			float imageWidth = image.getIntrinsicWidth();
			float imageHeight = image.getIntrinsicHeight();
			float width = r.getWidth();
			float height = r.getHeight();
			float displayScale = getDocument().getBundle().displayScale();
			
			float radius = getCornerRadius();
			
			if(radius == 0.0f){
				canvas.clipRect(0, 0, width * displayScale, height * displayScale);
			}
			else {
				Path path = new Path();
				path.addRoundRect(new RectF(0,0,width * displayScale,height * displayScale)
					, radius * displayScale, radius * displayScale, Path.Direction.CW);
				canvas.clipPath(path);
			}
		
			String gravity = stringValue("gravity","aspect-fill");

	        if("center".equals(gravity)){
	        	float dx = (imageWidth - width) / 2.0f;
	        	float dy = (imageHeight - height) / 2.0f;
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("resize".equals(gravity)){
	        	canvas.scale(width / imageWidth, height / imageHeight);
	        	image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        }
	        else if("top".equals(gravity)){
	        	float dx = (imageWidth - width) / 2.0f;
	        	float dy = 0;
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("bottom".equals(gravity)){
	        	float dx = (imageWidth - width) / 2.0f;
	        	float dy = (imageHeight - height);
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("left".equals(gravity)){
	        	float dx =0;
	        	float dy = (imageHeight - height) / 2.0f;
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("right".equals(gravity)){
	        	float dx = (imageWidth - width) ;
	        	float dy = (imageHeight - height) / 2.0f;
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("topleft".equals(gravity)){
	        	float dx = 0 ;
	        	float dy = 0;
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("topright".equals(gravity)){
	        	float dx = (imageWidth - width) ;
	        	float dy = 0;
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("bottomleft".equals(gravity)){
	        	float dx = 0 ;
	        	float dy = (imageHeight - height);
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("bottomright".equals(gravity)){
	        	float dx = (imageWidth - width) ;
	        	float dy = (imageHeight - height);
	        	image.setBounds((int) (dx * displayScale), (int) (dy * displayScale)
	        			, (int) ( width * displayScale), (int) (height * displayScale));
	        }
	        else if("aspect".equals(gravity)){
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 > r1){
	        		imageHeight = width / r0;
	        		imageWidth = width;
	        		canvas.scale(width / imageWidth, width / imageWidth);
	        		image.setBounds(0, (int) (imageHeight - height), (int) ( width * displayScale), (int) (height * displayScale));
	        	}
	        	else if(r0 < r1){
	        		imageWidth = height * r0;
	        		imageHeight = height;
	        		canvas.scale(height / imageHeight, height / imageHeight);
	        		image.setBounds((int) (imageWidth - width),0, (int) ( width * displayScale), (int) (height * displayScale));
	        	}
	        }
	        else if("aspect-top".equals(gravity)){
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		canvas.scale(width / imageWidth, height / imageHeight);
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 < r1){
	        		imageHeight = width / r0;
	        		imageWidth = width;
	        		canvas.scale(width / imageWidth, width / imageWidth);
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 > r1){
	        		imageWidth = height * r0;
	        		imageHeight = height;
	        		canvas.scale(height / imageHeight, height / imageHeight);
	        		image.setBounds(0,0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        }
	        else if("aspect-bottom".equals(gravity)){
	        	
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		canvas.scale(width / imageWidth, height / imageHeight);
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 < r1){
	        		imageHeight = width / r0;
	        		imageWidth = width;
	        		canvas.translate(0,  (int) ((height - imageHeight) * displayScale));
	        		canvas.scale(width / imageWidth, width / imageWidth);
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 > r1){
	        		imageWidth = height * r0;
	        		imageHeight = height;
	        		canvas.translate((int) ((width - height) * displayScale),0);
	        		canvas.scale(height / imageHeight, height / imageHeight);
	        		image.setBounds(0 , 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	
	        }
	        else {
	        	
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		canvas.scale(width / imageWidth, height / imageHeight);
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 < r1){
	        		imageHeight = width / r0;
	        		imageWidth = width;
	        		canvas.translate(0,  (int) ((height - imageHeight) * displayScale * 0.5));
	        		canvas.scale(width / imageWidth, width / imageWidth);
	        		image.setBounds(0, 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	else if(r0 > r1){
	        		imageWidth = height * r0;
	        		imageHeight = height;
	        		canvas.translate((int) ((width - height) * displayScale * 0.5),0);
	        		canvas.scale(height / imageHeight, height / imageHeight);
	        		image.setBounds(0 , 0, (int) ( imageWidth * displayScale), (int) (imageHeight * displayScale));
	        	}
	        	
	        }
			
			image.draw(canvas);
			
		}
		
	}
	
	@Override
	public Size layoutChildren(Edge padding){
		
		Rect r = getFrame();
	    
	    if(r.getWidth() == Float.MAX_VALUE || r.getHeight() == Float.MAX_VALUE){

	    	Drawable image = getImage();
	
	        if(image != null ){
	            
	        	float displayScale = getDocument().getBundle().displayScale();
	        	
	        	float width = image.getIntrinsicWidth();
	        	float height = image.getIntrinsicHeight();
	        
	        	if(r.getWidth() == Float.MAX_VALUE){
	                r.width = width / displayScale;
	            }
	            
	        	if(r.getHeight() == Float.MAX_VALUE){
		            r.height = height / displayScale;
		        }
	        	 
	        }
	        else{
	            if(r.getWidth() == Float.MAX_VALUE){
	                r.width = floatValue("min-width",0);
	            }
	            
	            if(r.getHeight() == Float.MAX_VALUE){
	                r.height = floatValue("min-height",0);;
	            }
	        }
	        
	    }
	    return new Size(r.getWidth(),r.getHeight());
	}
	
}
