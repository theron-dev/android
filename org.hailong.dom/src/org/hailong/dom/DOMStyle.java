package org.hailong.dom;

import java.util.HashMap;
import java.util.Map;

import org.hailong.core.Color;

public class DOMStyle {
	
	private String _name;
	private Map<String,String> _values;
	
	public String getName(){
		return _name;
	}
	
	public void setName(String name){
		_name = name;
	}
	
	public Map<String,String> getValues(){
		return _values;
	}
	
	public String getValue(String name){
		return _values != null ? _values.get(name) : null;
	}
	
	public void setValue(String name,String value){
		if(_values == null){
			_values = new HashMap<String,String>(4);
		}
		_values.put(name, value);
	}
	
	public void removeValue(String name){
		if(_values != null){
			_values.remove(name);
		}
	}
	
	public long longValue(String name,long defaultValue){
		
		String v = getValue(name);
		
		if(v != null){
			return Long.valueOf(v);
		}
		
		return defaultValue;
	}
	
	public int intValue(String name,int defaultValue){

		String v = getValue(name);
		
		if(v != null){
			return Integer.valueOf(v);
		}
		
		return defaultValue;
	}
	
	public double doubleValue(String name,double defaultValue){

		String v = getValue(name);
		
		if(v != null){
			return Double.valueOf(v);
		}
		
		return defaultValue;
	}
	
	public float floatValue(String name,float defaultValue){

		String v = getValue(name);
		
		if(v != null){
			return Float.valueOf(v);
		}
		
		return defaultValue;
	}
	
	public String stringValue(String name,String defaultValue){
		
		String v = getValue(name);
		
		if(v != null){
			return v;
		}
		
		return defaultValue;
	}
	
	public boolean booleanValue(String name,boolean defaultValue){
		
		String v = getValue(name);
		
		if(v != null){
			return !"false".equals(v) &&  ! "no".equals(v) &&  "".equals(v) && ! "0".equals(v);
		}
		
		return defaultValue;
	}
	
	public Color colorValue(String name,Color defaultValue){
		
		String v = getValue(name);
		
		if(v != null){
			return Color.valueOf(v);
		}
		
		return defaultValue;
		
	}
}
