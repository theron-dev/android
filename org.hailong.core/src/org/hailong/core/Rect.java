package org.hailong.core;

public final class Rect {
	
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
	
	public float getX(float defaultValue,float baseValue){
		return getValue(x,defaultValue,baseValue);
	}
	
	public float getY(float defaultValue,float baseValue){
		return getValue(y,defaultValue,baseValue);
	}
	
	public float getWidth(float defaultValue,float baseValue){
		return getValue(width,defaultValue,baseValue);
	}
	
	public float getHeight(float defaultValue,float baseValue){
		return getValue(height,defaultValue,baseValue);
	}
	
	public float getX(float defaultValue){
		return getValue(x,defaultValue);
	}
	
	public float getY(float defaultValue){
		return getValue(y,defaultValue);
	}
	
	public float getWidth(float defaultValue){
		return getValue(width,defaultValue);
	}
	
	public float getHeight(float defaultValue){
		return getValue(height,defaultValue);
	}

	public float getX(){
		return getValue(x,0.0f);
	}
	
	public float getY(){
		return getValue(y,0.0f);
	}
	
	public float getWidth(){
		return getValue(width,0.0f);
	}
	
	public float getHeight(){
		return getValue(height,0.0f);
	}
	
	public static float getValue(Object value,float defaultValue,float baseValue){
		
		if(value == null){
			return defaultValue;
		}
		
		if(value instanceof Number){
			return ((Number) value).floatValue();
		}
		
		String v = value.toString();
		
		if("auto".equals(v)){
			return Float.MAX_VALUE;
		}
		
		int index = v.indexOf("%");
		
		if(index >=0){
			String d = v.substring(index + 1);
			return (baseValue * Float.valueOf(v.substring(0, index)) / 100.0f) + ( d != null && d.length() >0? Float.valueOf(d) : 0.0f);
		}
		
		return Float.valueOf(v);
	}

	public static float getValue(Object value,float defaultValue){
		
		if(value == null){
			return defaultValue;
		}
		
		if(value instanceof Number){
			return ((Number) value).floatValue();
		}
		
		String v = value.toString();
		
		if("auto".equals(v)){
			return Float.MAX_VALUE;
		}
		
		return Float.valueOf(value.toString());
	}
}
