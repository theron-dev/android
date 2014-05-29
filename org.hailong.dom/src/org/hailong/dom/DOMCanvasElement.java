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

		onDrawElement(canvas);
		
		drawBorder(canvas);
		
	}
	
	protected void onDrawElement(Canvas canvas){
		
	}
	
	protected void drawBackground(Canvas canvas){
		
		Color backgroundColor = colorValue("background-color",new Color());
		
		if(backgroundColor.a != 0.0f){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			Rect r = getFrame();
			
			Paint paint = new Paint();
			
			paint.setAntiAlias(true);
			paint.setColor(backgroundColor.intValue());
			paint.setAlpha(backgroundColor.getAlpha());
			paint.setStyle(Style.FILL);
	
			float radius = getCornerRadius();
			
			if(radius == 0.0f){
				canvas.drawRect(new RectF(0,0,r.getWidth() * displayScale,r.getHeight() * displayScale), paint);
			}
			else {
				canvas.drawRoundRect(new RectF(0,0,r.getWidth() * displayScale,r.getHeight() * displayScale)
					, radius * displayScale, radius * displayScale, paint);
			}
			
		}
	}

	protected void drawBorder(Canvas canvas){
		
		Color borderColor = colorValue("border-color",new Color());
		
		float borderWidth =  floatValue("border-width",0.0f);
		
		if(borderWidth > 0.0f){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			Rect r = getFrame();
			
			float radius = getCornerRadius();
			
			Paint paint = new Paint();
			
			paint.setAntiAlias(true);
			paint.setColor(borderColor.intValue());
			paint.setAlpha(borderColor.getAlpha());
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(borderWidth);
			
			if(radius == 0.0f){
				canvas.drawRect(new RectF(0,0,r.getWidth() * displayScale,r.getHeight() * displayScale), paint);
			}
			else {
				canvas.drawRoundRect(new RectF(0,0,r.getWidth() * displayScale,r.getHeight() * displayScale)
					, radius * displayScale, radius * displayScale, paint);
			}
			
		}
		
	}

	@Override
	public boolean isHidden() {
		return booleanValue("hidden",false) || ! booleanValue("visable",true);
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
	
	@Override
	public void setAttributeValue(String name,String value){
		super.setAttributeValue(name, value);
		if("hidden".equals(name) || "visable".equals(name)){
			setNeedsDisplay();
		}
	}
}
