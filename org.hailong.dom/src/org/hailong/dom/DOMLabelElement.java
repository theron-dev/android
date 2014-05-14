package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import org.hailong.core.Font;
import org.hailong.core.Color;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public class DOMLabelElement extends DOMCanvasElement {

	public Font getFont(){
		return fontValue("font",new Font(floatValue("font-size",14)));
	}

	public Color getTextColor(){
		return colorValue("color",new Color(0));
	}

	@Override
	protected void onDrawElement(Canvas canvas){
		super.onDrawElement(canvas);
		
		String text = getText();
		
		if(text != null && text.length() > 0){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			Font font = getFont();
	        Color textColor = getTextColor();
			
	        Paint paint = new Paint();
	        
	        paint.setAntiAlias(true);
	        paint.setTextSize(font.fontSize * displayScale);
	        paint.setFakeBoldText(font.isFontStyleBold());
	        paint.setColor(textColor.intValue());
	        paint.setAlpha(textColor.getAlpha());
	        
	        Rect r = getFrame();
	        
	        float width = r.getWidth() * displayScale;
	        float height = r.getHeight() * displayScale;
	        float maxWidth = floatValue("max-width",r.getWidth()) * displayScale;
	        float maxHeight = floatValue("max-height",r.getHeight()) * displayScale;
	        
	        android.graphics.Rect bounds = new android.graphics.Rect(0,0
	        		, (int) (maxWidth + 0.999999f)
	        		, (int) (maxHeight + 0.999999f));
	        
	        paint.getTextBounds(text, 0, text.length(), bounds);
	        
	        FontMetrics metrics = paint.getFontMetrics();
	        
	        float w = bounds.width();
	        float h = bounds.height() + metrics.descent;
	        
	        float dx = w - width,dy = h - height;
	        
	        if((dx > 0.0f || dy >0.0f ) && booleanValue("font-scale",false)){
	        	
	        	float scale = 1.0f;
	        	
	        	if(dx >= dy){
	        		
	        		scale = width /  w;
	        		dx = 0.0f;
	        		dy = h * scale;
	        		
	        		String valign = stringValue("valign","top");
	        		
	        		if("center".equals(valign)){
	        			dy = ( height - dy ) / 2.0f;
	        		}
	        		else if("bottom".equals(valign)){
	        			dy = ( height - dy ) ;
	        		}
	        		else {
	        			dy = 0.0f;
	        		}
	        		
	        	}
	        	else {
	        		
	        		scale = height /  h;
	        		dy = 0.0f;
	        		dx = h * scale;
	        		
	        		String align = stringValue("align","left");
	        		
	        		if("center".equals(align)){
	        			dx = ( width - dx ) / 2.0f;
	        		}
	        		else if("right".equals("align")){
	        			dx = ( width - dx ) ;
	        		}
	        		else {
	        			dx = 0.0f;
	        		}
	        	}
	        	
	        	canvas.scale(scale, scale);
	        	canvas.drawText(text, dx, dy + h - metrics.descent, paint);
	        	
	        }
	        else {
	        	
	        	String valign = stringValue("valign","top");
        		
        		if("center".equals(valign)){
        			dy = ( height - h ) / 2.0f;
        		}
        		else if("bottom".equals(valign)){
        			dy = ( height - h ) ;
        		}
        		else {
        			dy = 0.0f;
        		}
        		
        		String align = stringValue("align","left");
        		
        		if("center".equals(align)){
        			dx = ( width - w ) / 2.0f;
        		}
        		else if("right".equals("align")){
        			dx = ( width - w ) ;
        		}
        		else {
        			dx = 0.0f;
        		}

        		canvas.drawText(text, dx, dy + h - metrics.descent, paint);
	        }
	        
		}
		
	}
	
	@Override
	public Size layoutChildren(Edge padding){
		
		Rect r = getFrame();
	    
	    if(r.getWidth() == Float.MAX_VALUE || r.getHeight() == Float.MAX_VALUE){

	    	String text = getText();

	        if(text != null && text.length() > 0){
	            
	        	float displayScale = getDocument().getBundle().displayScale();
	        	
	            Font font = getFont();
	            
	            Paint paint = new Paint();
	            
	            paint.setTextSize(font.fontSize * displayScale);
	            paint.setFakeBoldText(font.isFontStyleBold());
	            
		        float maxWidth = floatValue("max-width",r.getWidth());
		        float maxHeight = floatValue("max-height",r.getHeight());
		        
		        if(maxWidth != Float.MAX_VALUE){
		        	maxWidth *= displayScale;
		        }
		        
		        if(maxHeight != Float.MAX_VALUE){
		        	maxHeight *= displayScale;
		        }
		      
	            android.graphics.Rect bounds = new android.graphics.Rect(0,0
	            		, (int) (maxWidth + 0.999999f)
	            		, (int) (maxHeight +  0.999999f));
	            
	            FontMetrics metrics = paint.getFontMetrics();
	            
	            paint.getTextBounds(text, 0, text.length(), bounds);
	            
		        float w = bounds.width() / displayScale;
		        float h = (bounds.height() + metrics.descent) / displayScale;
		        
	            if(r.getWidth() == Float.MAX_VALUE){
	                r.width = w + padding.getLeft() + padding.getRight();
	                float max = floatValue("max-width",r.getWidth());
	                float min = floatValue("min-width",r.getWidth());
	                if(r.getWidth() > max){
	                    r.width = max;
	                }
	                if(r.getWidth() <  min){
	                    r.width = min;
	                }
	            }
	            
	            if(r.getHeight() == Float.MAX_VALUE){
	                
	            	r.height = h + padding.getTop() + padding.getBottom();
	                
	            	float max = floatValue("max-height",r.getHeight());
	                float min = floatValue("min-height",r.getHeight());
	                
	                if(r.getHeight() > max){
	                    r.height = max;
	                }
	                if(r.getHeight() <  min){
	                    r.height = min;
	                }
	                
	            }
	        }
	        else{
	            if(r.getWidth() == Float.MAX_VALUE){
	                r.width = 0;
	            }
	            
	            if(r.getHeight() == Float.MAX_VALUE){
	                r.height = 0;
	            }
	        }
	        
	    }
	    return new Size(r.getWidth(),r.getHeight());
	}

}
