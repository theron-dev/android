package org.hailong.core;

public final class Size {
	
	public Object width;
	public Object height;
	
	public Size(){
		
	}
	
	public Size(Object width,Object height){
		this.width = width;
		this.height = height;
	}
	
	
	public float getWidth(float defaultValue,float baseValue){
		return Rect.getValue(width,defaultValue,baseValue);
	}
	
	public float getHeight(float defaultValue,float baseValue){
		return Rect.getValue(height,defaultValue,baseValue);
	}
	
	
	public float getWidth(float defaultValue){
		return Rect.getValue(width,defaultValue);
	}
	
	public float getHeight(float defaultValue){
		return Rect.getValue(height,defaultValue);
	}

	
	public float getWidth(){
		return Rect.getValue(width,0);
	}
	
	public float getHeight(){
		return Rect.getValue(height,0);
	}
}
