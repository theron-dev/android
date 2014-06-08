package org.hailong.view;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hailong.core.Value;

import android.view.View;
import android.view.ViewGroup;

public class Container {

	private List<DataBindProperty> _propertys;
	
	public Container(View view){
		_propertys = new ArrayList<DataBindProperty>(4);
		loadView(view);
	}
	
	protected void loadView(View view){
		
		Class<?> clazz = view.getClass();
	
		CharSequence content = view.getContentDescription();
		
		if(content != null){
			
			String[] items = content.toString().split(",");
			
			for(String item : items){
				
				String[] kv = item.split(":");
				
				if(kv.length > 1 && kv[0].length() >0){
					
					String symbol = kv[0].substring(0,1).toUpperCase(Locale.US) + kv[0].substring(1);
					
					Method method = null;
					
					try {
						method = clazz.getMethod("get" + symbol);
				
					} catch (Exception e1) {
					} 
					
					if(method == null){
						
						try {
							method = clazz.getMethod("is" + symbol);
						} catch (Exception e1) {
						} 
						
					}
					
					if(method != null){
						
						Class<?> returnType = method.getReturnType();
						
						try {
							method = clazz.getMethod("set" + symbol,returnType);
						} catch (Exception e1) {
						} 
						
						if(method != null){
							DataBindProperty dataBind = new DataBindProperty(view, method, kv[1]);
							_propertys.add(dataBind);
						}
						
					}
					
				}
				
			}
			
		}

		
		try {
			
			Method method = clazz.getMethod("getText");
			
			String keyPath = (String) method.invoke(view).toString();
			
			if(keyPath.startsWith("{") && keyPath.endsWith("}")){
				
				String getKey = method.getName();

				if(getKey.startsWith("get")){
					String setKey = "set" + getKey.substring(3);
					Method setMethod = clazz.getMethod(setKey, method.getReturnType());
					DataBindProperty dataBind = new DataBindProperty(view, setMethod, keyPath.substring(1,keyPath.length() -1));
					_propertys.add(dataBind);
				}
				
			}
			
		} catch (Exception e1) {
		} 
		
		
		if(view instanceof ViewGroup){
			ViewGroup viewGroup = (ViewGroup) view;
			int c = viewGroup.getChildCount();
			for(int i=0;i<c;i++){
				loadView(viewGroup.getChildAt(i));
			}
		}
		
	}
	
	public void setDataObject(Object dataObject){
		if(_propertys != null){
			for(DataBindProperty property : _propertys){
				property.setDataObject(dataObject);
			}
		}
	}
	

	private static class DataBindProperty{
		
		private View _view;
		private String _keyPath;
		private Method _property;
		
		public DataBindProperty(View view,Method property,String keyPath){
			_view = view;
			_keyPath = keyPath;
			_property = property;
		}
		
		public void setDataObject(Object dataObject){
			Class<?> type = _property.getParameterTypes()[0];
			if(type == String.class || type == CharSequence.class){
				try {
					_property.invoke(_view, Value.stringValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {

				}
			}
			else if(type == Integer.class || type == int.class) {
				try {
					_property.invoke(_view,  (Integer) Value.intValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {
					
				} 
			}
			else if(type == Long.class || type == long.class) {
				try {
					_property.invoke(_view,  (Long) Value.longValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {

				} 
			}
			else if(type == Float.class || type == float.class) {
				try {
					_property.invoke(_view,  (Float) Value.floatValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {

				} 
			}
			else if(type == Double.class || type == double.class) {
				try {
					_property.invoke(_view,  (Double) Value.doubleValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {

				} 
			}
			else if(type == Boolean.class || type == boolean.class) {
				try {
					_property.invoke(_view,  (Boolean) Value.booleanValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {
					
				} 
			}
			else {
				try {
					_property.invoke(_view,  Value.objectValueForKeyPath(dataObject, _keyPath));
				} catch (Exception e) {
					
				}
			}
		}
	}
}
