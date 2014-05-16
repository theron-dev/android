package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;
import org.hailong.core.Font;
import org.hailong.core.Color;
import android.graphics.Canvas;
import android.graphics.Paint;

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
	        
	        Size textSize = getTextSize(text,paint,maxWidth);
	        
	        float w = textSize.getWidth();
	        float h = textSize.getHeight();
	        
	        if(h > maxHeight){
	        	h = maxHeight;
	        }

	        float dy = 0.0f;
	        
        	String valign = stringValue("valign","top");
    		
    		if("center".equals(valign)){
    			dy = ( height - h ) / 2.0f;
    		}
    		else if("bottom".equals(valign)){
    			dy = ( height - h ) ;
    		}
    		
    		String align = stringValue("align","left");
    		
    		if("center".equals(align)){
    			
    			{
    				
    				float[] widths = new float[1];
    				
    				int start = 0;
    				int end = text.length();
    				int len;
    				
    				while(start < end ){
    					
    					len = paint.breakText(text, start, end, false, w, widths);

    					canvas.drawText(text, start, start + len, (width - widths[0]) / 2.0f, dy - paint.ascent(), paint);
    					
    					dy +=  - paint.ascent() + paint.descent();
    					
    					start += len;
    					
    				}
    			}

    		}
    		else if("right".equals("align")){
    			
    			{
    				
    				float[] widths = new float[1];
    				
    				int start = 0;
    				int end = text.length();
    				int len;
    				
    				while(start < end ){
    					
    					len = paint.breakText(text, start, end, false, w, widths);

    					canvas.drawText(text, start, start +  len, width - widths[0], dy - paint.ascent(), paint);
    					
    					dy +=  - paint.ascent() + paint.descent();
    					
    					start += len;
    					
    				}
    			}

    		}
    		else {
    			{
    				
    				float[] widths = new float[1];
    				
    				int start = 0;
    				int end = text.length();
    				int len;
    				
    				while(start < end ){
    					
    					len = paint.breakText(text, start, end, false, w, widths);

    					canvas.drawText(text, start, start +  len, 0.0f, dy - paint.ascent(), paint);
    					
    					dy +=  - paint.ascent() + paint.descent();
    					
    					start += len;
    					
    				}
    			}
    		}

	        
		}
		
	}
	
	public Size getTextSize(String text,Paint paint,float maxWidth){
		
		Size size = new Size();
		
		if(maxWidth == Float.MAX_VALUE){
			
			android.graphics.Rect bounds = new android.graphics.Rect();
			paint.getTextBounds(text, 0, text.length(), bounds);
			
			size.width = bounds.width();
			size.height = - paint.ascent() + paint.descent();
			
		}
		else {
			float[] widths = new float[1];
			
			int start = 0;
			int end = text.length();
			int len;
			
			while(start < end ){
				
				len = paint.breakText(text, start, end, false, maxWidth, widths);
	
				if(widths[0] > size.getWidth()){
					size.width = widths[0];
				}
				
				size.height = size.getHeight() - paint.ascent() + paint.descent();
				
				start += len;
			}
		}

		return size;
		
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
	      
	            paint.breakText(text, 0, 0, false, 0, null);
	            
		        float maxWidth = floatValue("max-width",r.getWidth());
		        float maxHeight = floatValue("max-height",r.getHeight());
		        
		        if(maxWidth != Float.MAX_VALUE){
		        	maxWidth *= displayScale;
		        }
		        
		        if(maxHeight != Float.MAX_VALUE){
		        	maxHeight *= displayScale;
		        }
		      
		        Size textSize = getTextSize(text,paint,maxWidth);
	            
		        float w = textSize.getWidth() / displayScale;
		        float h = textSize.getHeight() / displayScale;
		        
		        if(h > maxHeight){
		        	h = maxHeight;
		        }
		        
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
	                r.width = floatValue("min-width",0);;
	            }
	            
	            if(r.getHeight() == Float.MAX_VALUE){
	                r.height = floatValue("min-height",0);;
	            }
	        }
	        
	    }
	    return new Size(r.getWidth(),r.getHeight());
	}

}
