package org.hailong.core;

public class Edge {
	
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
	
	public int getLeft(int defaultValue,int baseValue){
		return Rect.getValue(left,defaultValue,baseValue);
	}
	
	public int getTop(int defaultValue,int baseValue){
		return Rect.getValue(top,defaultValue,baseValue);
	}
	
	public int getRight(int defaultValue,int baseValue){
		return Rect.getValue(right,defaultValue,baseValue);
	}
	
	public int getBottom(int defaultValue,int baseValue){
		return Rect.getValue(bottom,defaultValue,baseValue);
	}
	
	public int getLeft(int defaultValue){
		return Rect.getValue(left,defaultValue);
	}
	
	public int getTop(int defaultValue){
		return Rect.getValue(top,defaultValue);
	}
	
	public int getRight(int defaultValue){
		return Rect.getValue(right,defaultValue);
	}
	
	public int getBottom(int defaultValue){
		return Rect.getValue(bottom,defaultValue);
	}

	public int getLeft(){
		return Rect.getValue(left,0);
	}
	
	public int getTop(){
		return Rect.getValue(top,0);
	}
	
	public int getRight(){
		return Rect.getValue(right,0);
	}
	
	public int getBottom(){
		return Rect.getValue(bottom,0);
	}
}
