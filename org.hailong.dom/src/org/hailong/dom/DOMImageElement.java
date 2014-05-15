package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class DOMImageElement extends DOMCanvasElement {

	private Drawable _image;
	private Drawable _defaultImage;
	
	public Drawable getImage(){
		
		if(_image == null && getDocument() !=null){
			_image = getDocument().getBundle().getImageForURI(getAttributeValue("src"));
		}
		
		return _image;
	}
	
	public void setImage(Drawable image){
		_image = image;
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
			setNeedsDisplay();
		}
		
	}
	
	@Override
	protected void onDrawElement(Canvas canvas){
		super.onDrawElement(canvas);
		
		Drawable image = getImage();
		
		if(image == null){
			image = getDefaultImage();
		}
		
		if(image != null){
			
//			String gravity = stringValue("gravity","aspect-fill");
//	        image.getBounds()
//	        if("center".equals(gravity)){
//	        	canvas.drawPicture(image);
//	        }
//	        else if([gravity isEqualToString:@"resize"]){
//	            layer.contentsGravity = kCAGravityResize;
//	        }
//	        else if([gravity isEqualToString:@"top"]){
//	            layer.contentsGravity = kCAGravityTop;
//	        }
//	        else if([gravity isEqualToString:@"bottom"]){
//	            layer.contentsGravity = kCAGravityBottom;
//	        }
//	        else if([gravity isEqualToString:@"left"]){
//	            layer.contentsGravity = kCAGravityLeft;
//	        }
//	        else if([gravity isEqualToString:@"right"]){
//	            layer.contentsGravity = kCAGravityRight;
//	        }
//	        else if([gravity isEqualToString:@"topleft"]){
//	            layer.contentsGravity = kCAGravityTopLeft;
//	        }
//	        else if([gravity isEqualToString:@"topright"]){
//	            layer.contentsGravity = kCAGravityTopRight;
//	        }
//	        else if([gravity isEqualToString:@"bottomleft"]){
//	            layer.contentsGravity = kCAGravityBottomLeft;
//	        }
//	        else if([gravity isEqualToString:@"bottomright"]){
//	            layer.contentsGravity = kCAGravityBottomRight;
//	        }
//	        else if([gravity isEqualToString:@"aspect"]){
//	            layer.contentsGravity = kCAGravityResizeAspect;
//	        }
//	        else{
//	            layer.contentsGravity = kCAGravityResizeAspectFill;
//	        }
			
			
			Rect r = getFrame();
			float displayScale = getDocument().getBundle().displayScale();
			
			image.setBounds(0, 0, (int) ( r.getWidth() * displayScale), (int) (r.getHeight() * displayScale));
			
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
