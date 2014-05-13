package org.hailong.dom;

import org.hailong.core.Color;
import org.hailong.core.Rect;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class DOMCanvasElement extends DOMLayoutElement implements IDOMCanvasElement{

	@Override
	public void draw(Canvas canvas) {
		
		drawBackground(canvas);

		drawBorder(canvas);
		
	}
	
	protected void drawBackground(Canvas canvas){
		
		Color backgroundColor = colorValue("background-color",new Color());
		
		if(backgroundColor.a != 0.0f){
			
			Rect r = getFrame();
			
			Paint paint = new Paint();
			
			paint.setColor(backgroundColor.intValue());
			paint.setAlpha(backgroundColor.getAlpha());
			paint.setStyle(Style.FILL);
	
			canvas.drawRect(new RectF(0,0,r.getWidth(),r.getHeight()), paint);
		}
	}

	protected void drawBorder(Canvas canvas){
		
		Color borderColor = colorValue("border-color",new Color());
		
		float borderWidth =  floatValue("border-width",0.0f);
		
		if(borderWidth > 0.0f){
			
			Rect r = getFrame();
			
			float radius = getCornerRadius();
			
			Paint paint = new Paint();
			
			paint.setColor(borderColor.intValue());
			paint.setAlpha(borderColor.getAlpha());
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(borderWidth);
			
			if(radius == 0.0f){
				canvas.drawRect(new RectF(0,0,r.getWidth(),r.getHeight()), paint);
			}
			else {
				canvas.drawRoundRect(new RectF(0,0,r.getWidth(),r.getHeight()), radius, radius, paint);
			}
			
		}
		
	}

	@Override
	public boolean isHidden() {
		return booleanValue("hidden",true) || booleanValue("visable",false);
	}

	@Override
	public float getCornerRadius() {
		return floatValue("corner-radius",0.0f);
	}

	@Override
	public void setText(String text){
		super.setText(text);
		setNeedsDisplay();
	}
	
	@Override
	public void setStyle(DOMStyle style){
		super.setStyle(style);
		setNeedsDisplay();
	}
}
