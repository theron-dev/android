package org.hailong.framework;

public class Rect {
	
	public Object x;
	public Object y;
	public Object width;
	public Object height;
	
	public Rect(){
		
	}
	
	public Rect(Object x,Object y,Object width,Object height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX(int defaultValue,int baseValue){
		return getValue(x,defaultValue,baseValue);
	}
	
	public int getY(int defaultValue,int baseValue){
		return getValue(y,defaultValue,baseValue);
	}
	
	public int getWidth(int defaultValue,int baseValue){
		return getValue(width,defaultValue,baseValue);
	}
	
	public int getHeight(int defaultValue,int baseValue){
		return getValue(height,defaultValue,baseValue);
	}
	
	public int getX(int defaultValue){
		return getValue(x,defaultValue);
	}
	
	public int getY(int defaultValue){
		return getValue(y,defaultValue);
	}
	
	public int getWidth(int defaultValue){
		return getValue(width,defaultValue);
	}
	
	public int getHeight(int defaultValue){
		return getValue(height,defaultValue);
	}

	public int getX(){
		return getValue(x,0);
	}
	
	public int getY(){
		return getValue(y,0);
	}
	
	public int getWidth(){
		return getValue(width,0);
	}
	
	public int getHeight(){
		return getValue(height,0);
	}
	
	public static int getValue(Object value,int defaultValue,int baseValue){
		
		if(value == null){
			return defaultValue;
		}
		
		if(value instanceof Number){
			return ((Number) value).intValue();
		}
		
		String v = value.toString();
		
		int index = v.indexOf("%");
		
		if(index >=0){
			String d = v.substring(index + 1);
			return (int) (baseValue * Double.valueOf(v.substring(0, index))) + ( d != null && d.length() >0?Integer.valueOf(d) : 0);
		}
		
		return Integer.valueOf(v);
	}

	public static int getValue(Object value,int defaultValue){
		
		if(value == null){
			return defaultValue;
		}
		
		if(value instanceof Number){
			return ((Number) value).intValue();
		}
		
		return Integer.valueOf(value.toString());
	}
}
