package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hailong.core.Value;

public class DOMDataBinder {

	private final DOMElement _element;
	private final List<Item> _items;
	
	public DOMDataBinder(DOMElement element){
		_element = element;
		_items = new ArrayList<Item>(4);
		_DOMDataBinder(_element,"*\\{*\\}*");
	}
	
	private void _DOMDataBinder(DOMElement element,String regexp){
		String v = element.getText();
		if(v != null && v.matches(regexp)){
			_items.add(new Item(element,v));
		}
		Map<String,String> attrs = element.getAttributes();
		if(attrs != null){
			for(String key : attrs.keySet()){
				v = attrs.get(key);
				if(v != null && v.matches(regexp)){
					_items.add(new Item(element,key,v));
				}
			}
		}
	}
	
	public DOMElement getElement(){
		return _element;
	}
	
	public void setDataObject(Object dataObject){
		
		if(_items != null){
			
			for(Item item : _items){
				item.setDataObject(dataObject);
			}
			
		}
	}
	
	public static String getStringValue(Object dataObject,String value){
		
		if(value == null){
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		int length = value.length();
		int s = 0;
		int index = 0;
		int b = 0;
		
		for(;index<length;index++){
			
			char c = value.charAt(index);
			
			if(s == 0){
				if(c == '{'){
					b = index;
					s = 1;
				}
				else {
					sb.append(c);
				}
			}
			else if(s == 1){
				if(c == '}'){
					String key = value.substring(b + 1, index);
					String v = Value.stringValueForKeyPath(dataObject, key);
					if(v != null){
						sb.append(v);
					}
					s = 0;
				}
			}
		}
		
		if(s == 1){
			sb.append(value.substring(b));
		}
		
		return sb.toString();
	}
	
	private static class Item {
		
		private final DOMElement _element;
		private final String _key;
		private final String _value;
		private final String _text;
		
		public Item(DOMElement element,String key,String value){
			_element = element;
			_key = key;
			_value = value;
			_text = null;
		}
		
		public Item(DOMElement element,String text){
			_element = element;
			_key = null;
			_value = null;
			_text = text;
		}
		
	
		public void setDataObject(Object dataObject){
			if(_key != null && _value != null){
				_element.setAttributeValue(_key, getStringValue(dataObject,_value));
			}
			else if(_text != null){
				_element.setText(getStringValue(dataObject,_text));
			}
		}
	}
}
