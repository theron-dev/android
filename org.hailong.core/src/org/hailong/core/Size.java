package org.hailong.core;

public class Size {
	
	public Object width;
	public Object height;
	
	public Size(){
		
	}
	
	public Size(Object width,Object height){
		this.width = width;
		this.height = height;
	}
	
	
	public int getWidth(int defaultValue,int baseValue){
		return Rect.getValue(width,defaultValue,baseValue);
	}
	
	public int getHeight(int defaultValue,int baseValue){
		return Rect.getValue(height,defaultValue,baseValue);
	}
	
	
	public int getWidth(int defaultValue){
		return Rect.getValue(width,defaultValue);
	}
	
	public int getHeight(int defaultValue){
		return Rect.getValue(height,defaultValue);
	}

	
	public int getWidth(){
		return Rect.getValue(width,0);
	}
	
	public int getHeight(){
		return Rect.getValue(height,0);
	}
}
