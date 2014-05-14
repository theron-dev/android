package org.hailong.core;

public final class Point {
	
	public Object x;
	public Object y;
	
	public Point(){
		
	}
	
	public Point(Object x,Object y){
		this.x = x;
		this.y = y;
	}
	
	public float getX(float defaultValue,float baseValue){
		return Rect.getValue(x,defaultValue,baseValue);
	}
	
	public float getY(float defaultValue,float baseValue){
		return Rect.getValue(y,defaultValue,baseValue);
	}
	
	public float getX(float defaultValue){
		return Rect.getValue(x,defaultValue);
	}
	
	public float getY(float defaultValue){
		return Rect.getValue(y,defaultValue);
	}

	public float getX(){
		return Rect.getValue(x,0);
	}
	
	public float getY(){
		return Rect.getValue(y,0);
	}

	
}
