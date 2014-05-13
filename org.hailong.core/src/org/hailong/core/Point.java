package org.hailong.core;

public class Point {
	
	public Object x;
	public Object y;
	
	public Point(){
		
	}
	
	public Point(Object x,Object y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(int defaultValue,int baseValue){
		return Rect.getValue(x,defaultValue,baseValue);
	}
	
	public int getY(int defaultValue,int baseValue){
		return Rect.getValue(y,defaultValue,baseValue);
	}
	
	public int getX(int defaultValue){
		return Rect.getValue(x,defaultValue);
	}
	
	public int getY(int defaultValue){
		return Rect.getValue(y,defaultValue);
	}

	public int getX(){
		return Rect.getValue(x,0);
	}
	
	public int getY(){
		return Rect.getValue(y,0);
	}

	
}
