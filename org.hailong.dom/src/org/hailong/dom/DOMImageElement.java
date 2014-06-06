package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
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
			float displayScale = getDocument().getBundle().displayScale();
			float imageWidth = image.getIntrinsicWidth() * displayScale;
			float imageHeight = image.getIntrinsicHeight() * displayScale;
			float width = r.getWidth() * displayScale;
			float height = r.getHeight() * displayScale;
			
			float radius = getCornerRadius() * displayScale;
			float tx = 0,ty = 0,rx = 1.0f,ry = 1.0f;
			
			String gravity = stringValue("gravity","aspect-fill");

	        if("center".equals(gravity)){
	        	float dx = (imageWidth - width) / 2.0f;
	        	float dy = (imageHeight - height) / 2.0f;
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("resize".equals(gravity)){
	        	rx = width / imageWidth;
	        	ry =  height / imageHeight;
	        	image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        }
	        else if("top".equals(gravity)){
	        	float dx = (imageWidth - width) / 2.0f;
	        	float dy = 0;
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("bottom".equals(gravity)){
	        	float dx = (imageWidth - width) / 2.0f;
	        	float dy = (imageHeight - height);
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("left".equals(gravity)){
	        	float dx =0;
	        	float dy = (imageHeight - height) / 2.0f;
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("right".equals(gravity)){
	        	float dx = (imageWidth - width) ;
	        	float dy = (imageHeight - height) / 2.0f;
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("topleft".equals(gravity)){
	        	float dx = 0 ;
	        	float dy = 0;
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("topright".equals(gravity)){
	        	float dx = (imageWidth - width) ;
	        	float dy = 0;
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("bottomleft".equals(gravity)){
	        	float dx = 0 ;
	        	float dy = (imageHeight - height);
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("bottomright".equals(gravity)){
	        	float dx = (imageWidth - width) ;
	        	float dy = (imageHeight - height);
	        	image.setBounds((int) (dx  ), (int) (dy  )
	        			, (int) ( width  ), (int) (height  ));
	        }
	        else if("aspect".equals(gravity)){
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 > r1){
	        		
	        		rx = width / imageWidth;
	        		ry = width / imageWidth;
	        		
	        		imageHeight = width / r0;
	        		imageWidth = width;

	        		image.setBounds(0, (int) (imageHeight - height), (int) ( width  ), (int) (height  ));
	        	}
	        	else if(r0 < r1){
	        		
	        		rx = height / imageHeight;
	        		ry = height / imageHeight;
	        		
	        		imageWidth = height * r0;
	        		imageHeight = height;

	        		image.setBounds((int) (imageWidth - width),0, (int) ( width  ), (int) (height  ));
	        	}
	        }
	        else if("aspect-top".equals(gravity)){
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		rx = width / imageWidth;
	        		ry = height / imageHeight;
	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 < r1){

	        		rx = width / imageWidth;
	        		ry = width / imageWidth;
	        		
	        		imageHeight = width / r0;
	        		imageWidth = width;

	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 > r1){
	        		
	        		rx = height / imageHeight;
	        		ry = height / imageHeight;
	        		
	        		imageWidth = height * r0;
	        		imageHeight = height;
	        		image.setBounds(0,0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        }
	        else if("aspect-bottom".equals(gravity)){
	        	
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		rx = width / imageWidth;
	        		ry = height / imageHeight;
	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 < r1){
	        		
	        		rx = width / imageWidth;
	        		ry = width / imageWidth;
	        		
	        		imageHeight = width / r0;
	        		imageWidth = width;
	        		
	        		ty = (height - imageHeight)  ;

	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 > r1){
	        		
	        		
	        		rx = height / imageHeight;
	        		ry = height / imageHeight;
	        		
	        		imageWidth = height * r0;
	        		imageHeight = height;
	        		
	        		tx = (width - height)  ;

	        		image.setBounds(0 , 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	
	        }
	        else {
	        	
	        	float r0 = imageWidth / imageHeight;
	        	float r1 = width / height;
	        	if(r0 == r1){
	        		rx = width / imageWidth;
	        		ry = height / imageHeight;
	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 < r1){
	        		
	        		rx = width / imageWidth;
	        		ry = width / imageWidth;
	        		
	        		ty = (height - width / r0)   * 0.5f;
	        		
	        		image.setBounds(0, 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	else if(r0 > r1){

	        		rx = height / imageHeight;
	        		ry = height / imageHeight;
	        		
	        		tx = (width - height * r0)   * 0.5f;
	        		
	        		image.setBounds(0 , 0, (int) ( imageWidth  ), (int) (imageHeight  ));
	        	}
	        	
	        }
	        
	        if(radius != 0.0f && image instanceof BitmapDrawable){

	        	int x = (int) (width );  
	            int y = (int) (height );  
	            float[] mOuter = new float[] { radius, radius, radius, radius,  
	            		radius, radius, radius, radius };  
	 

	            // 新建一个矩形  
	            RectF outerRect = new RectF(0, 0, x, y);  
	            
	            Paint paint = new Paint();
	            
	            paint.setAntiAlias(true);
	            paint.setColor(0xffffffff);

	            canvas.saveLayer(outerRect, paint, Canvas.CLIP_SAVE_FLAG);
	            
	            Path mPath = new Path();  
	            // 创建一个圆角矩形路径  
	            mPath.addRoundRect(outerRect, mOuter, Path.Direction.CW);  
	      
	            canvas.clipPath(mPath);
	            
	            canvas.drawRoundRect(outerRect, radius, radius, paint);
	            
	            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	            
	            canvas.translate(tx, ty);
	        	canvas.scale(rx, ry);
	            
	            canvas.drawBitmap(((BitmapDrawable) image).getBitmap(), 0, 0, paint);

	            canvas.restore();
	            
			}
	        else{
	        	canvas.translate(tx, ty);
	        	canvas.scale(rx, ry);
	        	image.draw(canvas);
	        }
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
