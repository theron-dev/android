package org.hailong.dom;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class DOMImageElement extends DOMCanvasElement {

	private Drawable _image;
	private Drawable _defaultImage;
	
	public Drawable getImage(){
		return _image;
	}
	
	public void setImage(Drawable image){
		_image = image;
	}
	
	public Drawable getDefaultImage(){
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
			
			image.draw(canvas);
			
		}
		
	}
	
}
