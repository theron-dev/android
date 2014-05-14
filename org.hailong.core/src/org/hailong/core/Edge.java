package org.hailong.core;

public final class Edge {
	
	public Object left;
	public Object top;
	public Object right;
	public Object bottom;
	
	public Edge(){
		
	}
	
	public Edge(Object value){
		this.left = value;
		this.top = value;
		this.right = value;
		this.bottom = value;
	}
	
	public Edge(Object left,Object top,Object right,Object bottom){
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public float getLeft(float defaultValue,float baseValue){
		return Rect.getValue(left,defaultValue,baseValue);
	}
	
	public float getTop(float defaultValue,float baseValue){
		return Rect.getValue(top,defaultValue,baseValue);
	}
	
	public float getRight(float defaultValue,float baseValue){
		return Rect.getValue(right,defaultValue,baseValue);
	}
	
	public float getBottom(float defaultValue,float baseValue){
		return Rect.getValue(bottom,defaultValue,baseValue);
	}
	
	public float getLeft(float defaultValue){
		return Rect.getValue(left,defaultValue);
	}
	
	public float getTop(float defaultValue){
		return Rect.getValue(top,defaultValue);
	}
	
	public float getRight(float defaultValue){
		return Rect.getValue(right,defaultValue);
	}
	
	public float getBottom(float defaultValue){
		return Rect.getValue(bottom,defaultValue);
	}

	public float getLeft(){
		return Rect.getValue(left,0);
	}
	
	public float getTop(){
		return Rect.getValue(top,0);
	}
	
	public float getRight(){
		return Rect.getValue(right,0);
	}
	
	public float getBottom(){
		return Rect.getValue(bottom,0);
	}
}
