package org.hailong.framework.value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Value {

	public static String stringValue(Object object,String defaultValue){
		
		if(object != null){
			
			if(object instanceof String){
				return (String)object;
			}

			return object.toString();
		}
		
		return defaultValue;
	}
	
	public static String stringValue(Object object){
		return stringValue(object,null);
	}
	
	public static int intValue(Object object,int defaultValue){
		
		if(object != null){
			
			if(object instanceof String){
				return Integer.valueOf((String) object);
			}
			else if(object instanceof Number){
				return ((Number) object).intValue();
			}
		}

		return defaultValue;
	}
	
	public static int intValue(Object object){
		return intValue(object,0);
	}
	
	public static long longValue(Object object,long defaultValue){
		
		if(object != null){
			
			if(object instanceof String){
				return Long.valueOf((String) object);
			}
			else if(object instanceof Number){
				return ((Number) object).longValue();
			}

		}

		return defaultValue;
	}
	
	public static long longValue(Object object){
		return longValue(object,0);
	}

	public static float floatValue(Object object,float defaultValue){
		
		if(object != null){
			
			if(object instanceof String){
				return Float.valueOf((String) object);
			}
			else if(object instanceof Number){
				return ((Number) object).floatValue();
			}

		}

		return defaultValue;
	}
	
	public static float floatValue(Object object){
		return floatValue(object,0.0f);
	}
	
	public static double doubleValue(Object object,double defaultValue){
		
		if(object != null){
			
			if(object instanceof String){
				return Double.valueOf((String) object);
			}
			else if(object instanceof Number){
				return ((Number) object).doubleValue();
			}

		}

		return defaultValue;
	}
	
	public static double doubleValue(Object object){
		return doubleValue(object,0.0);
	}
	
	public static boolean booleanValue(Object object,boolean defaultValue){
		
		if(object != null){
			
			if(object instanceof String){
				return Boolean.valueOf((String) object);
			}
			else if(object instanceof Number){
				return ((Number) object).intValue() == 0 ? false : true;
			}

		}

		return defaultValue;
	}
	
	public static boolean booleanValue(Object object){
		return booleanValue(object,false);
	}
	
	public static Map<?,?> mapValue(Object object){
		
		if(object != null){
			if(object instanceof Map){
				return ( Map<?,?>) object;
			}
		}
		return null;
	}
	
	public static List<?> listValue(Object object){
		
		if(object != null){
			if(object instanceof List){
				return (List<?>) object;
			}
		}
		
		return null;
	}
	
	public static Object objectValueForKey(Object object,String key){
		
		if(object != null && key != null){
			
			if(object instanceof Map){
				return ((Map<?, ?>) object).get(key);
			}
			else if(object instanceof JSONObject){
				try {
					return ((JSONObject) object).get(key);
				} catch (JSONException e) {
				}
			}
			else if(object instanceof JSONArray){
				
				if(key.equals("@last")){
					int size = ((JSONArray) object).length();
					if(size >0){
						try {
							return ((JSONArray) object).get(size -1);
						} catch (JSONException e) {
						}
					}
				}
				else if(key.equals("@first")){
					int size = ((JSONArray) object).length();
					if(size >0){
						try {
							return ((JSONArray) object).get(0);
						} catch (JSONException e) {
						}
					}
				}
				else if(key.equals("@joinString")){
					
					StringBuilder sb = new StringBuilder();
					
					int size = ((JSONArray) object).length();
					
					for(int i=0;i<size;i++){
						
						Object v = null;
						
						try {
							v = ((JSONArray) object).get(i);
						} catch (JSONException e) {
						}
						
						String s = Value.stringValue(v,null);
						
						if(s != null){
							if(sb.length() == 0){
								sb.append(s);
							}
							else{
								sb.append(",").append(s);
							}
						}
						
					}
					
					return sb.toString();
				}
				else if(key.startsWith("@")){
					int index = Integer.valueOf(key.substring(1));
					int size = ((JSONArray) object).length();
					if(index >=0 && index < size){
						try {
							return ((JSONArray) object).get(index);
						} catch (JSONException e) {
						}
					}
				}
			}
			else if(object instanceof List){
				if(key.equals("@last")){
					int size = ((List<?>) object).size();
					if(size >0){
						return ((List<?>) object).get(size - 1);
					}
				}
				else if(key.equals("@first")){
					if(((List<?>) object).size() >0){
						return ((List<?>) object).get(0);
					}
				}
				else if(key.equals("@joinString")){
					
					StringBuilder sb = new StringBuilder();
					
					for(Object item : ((List<?>) object)){
						
						String s = Value.stringValue(item, null);
						
						if(s != null){
							if(sb.length() == 0){
								sb.append(s);
							}
							else{
								sb.append(",").append(s);
							}
						}
						
					}
					
					return sb.toString();
				}
				else if(key.startsWith("@")){
					int index = Integer.valueOf(key.substring(1));
					if(index >=0 && index < ((List<?>) object).size()){
						return ((List<?>) object).get(index);
					}
				}
			}
			else if(key.length() > 0){
				
				String symbol = key.substring(0,1).toUpperCase(Locale.US) + key.substring(1);
				
				Method method = null;
				
				try {
					method = object.getClass().getMethod("get" + symbol);
				} catch (NoSuchMethodException e) {
			
				}
				
				if(method == null){
					
					try {
						method = object.getClass().getMethod("is" + symbol);
					} catch (NoSuchMethodException e) {
				
					}
				}
				
				if(method != null){
					try {
						return method.invoke(object);
					} catch (Exception e) {

					}
				}
				
				Field field = null;
				
				try {
					field = object.getClass().getField(key);
				} catch (Exception e) {
			
				}
				
				if(field != null){
					try {
						return field.get(object);
					} catch (Exception e) {
					} 
				}
			}
			
		}
		
		return null;
	}
	
	
	
	public static Object objectValueForKeyPath(Object object,String[] keyPath,int index){
		
		Object v = Value.objectValueForKey(object, keyPath[index]);
		
		if(v != null){
			if(index + 1 < keyPath.length){
				return Value.objectValueForKeyPath(v, keyPath, index + 1);
			}
			return v;
		}
		
		return null;
	}
	
	public static Object objectValueForKeyPath(Object object,String keyPath){
		
		if(object != null && keyPath != null){
			
			String[] keys = keyPath.split("\\.");
			
			if(keys.length >0){
				return objectValueForKeyPath(object,keys,0);
			}
		}
		
		return null;
	}
	
	public static String stringValueForKey(Object object,String key,String defaultValue){
		return stringValue(objectValueForKey(object,key),defaultValue);
	}
	
	public static String stringValueForKey(Object object,String key){
		return stringValue(objectValueForKey(object,key),null);
	}
	
	public static int intValueForKey(Object object,String key,int defaultValue){
		return intValue(objectValueForKey(object,key),defaultValue);
	}
	
	public static int intValueForKey(Object object,String key){
		return intValue(objectValueForKey(object,key),0);
	}
	
	public static long longValueForKey(Object object,String key,long defaultValue){
		return longValue(objectValueForKey(object,key),defaultValue);
	}
	
	
	public static long longValueForKey(Object object,String key){
		return longValue(objectValueForKey(object,key),0);
	}
	
	
	public static float floatValueForKey(Object object,String key,float defaultValue){
		return floatValue(objectValueForKey(object,key),defaultValue);
	}
	
	
	public static float floatValueForKey(Object object,String key){
		return floatValue(objectValueForKey(object,key),0.0f);
	}
	
	public static double doubleValueForKey(Object object,String key,double defaultValue){
		return doubleValue(objectValueForKey(object,key),defaultValue);
	}
	
	public static double doubleValueForKey(Object object,String key){
		return doubleValue(objectValueForKey(object,key),0.0);
	}
	
	public static boolean booleanValueForKey(Object object,String key,boolean defaultValue){
		return booleanValue(objectValueForKey(object,key),defaultValue);
	}
	
	public static boolean booleanValueForKey(Object object,String key){
		return booleanValue(objectValueForKey(object,key),false);
	}
	
	
	
	public static String stringValueForKeyPath(Object object,String keyPath,String defaultValue){
		return stringValue(objectValueForKeyPath(object,keyPath),defaultValue);
	}
	
	public static String stringValueForKeyPath(Object object,String keyPath){
		return stringValue(objectValueForKeyPath(object,keyPath),null);
	}
	
	public static int intValueForKeyPath(Object object,String keyPath,int defaultValue){
		return intValue(objectValueForKeyPath(object,keyPath),defaultValue);
	}
	
	public static int intValueForKeyPath(Object object,String keyPath){
		return intValue(objectValueForKeyPath(object,keyPath),0);
	}
	
	public static long longValueForKeyPath(Object object,String keyPath,long defaultValue){
		return longValue(objectValueForKeyPath(object,keyPath),defaultValue);
	}
	
	
	public static long longValueForKeyPath(Object object,String keyPath){
		return longValue(objectValueForKeyPath(object,keyPath),0);
	}
	
	
	public static float floatValueForKeyPath(Object object,String keyPath,float defaultValue){
		return floatValue(objectValueForKeyPath(object,keyPath),defaultValue);
	}
	
	
	public static float floatValueForKeyPath(Object object,String keyPath){
		return floatValue(objectValueForKeyPath(object,keyPath),0.0f);
	}
	
	public static double doubleValueForKeyPath(Object object,String keyPath,double defaultValue){
		return doubleValue(objectValueForKeyPath(object,keyPath),defaultValue);
	}
	
	public static double doubleValueForKeyPath(Object object,String keyPath){
		return doubleValue(objectValueForKeyPath(object,keyPath),0.0);
	}
	
	public static boolean booleanValueForKeyPath(Object object,String keyPath,boolean defaultValue){
		return booleanValue(objectValueForKeyPath(object,keyPath),defaultValue);
	}
	
	public static boolean booleanValueForKeyPath(Object object,String keyPath){
		return booleanValue(objectValueForKeyPath(object,keyPath),false);
	}

	public static Map<?,?> mapValueForKey(Object object,String key){
		return mapValue(objectValueForKey(object,key));
	}
	
	public static Map<?,?> mapValueForKeyPath(Object object,String keyPath){
		return mapValue(objectValueForKeyPath(object,keyPath));
	}
	
	public static List<?> listValueForKey(Object object,String key){
		return listValue(objectValueForKey(object,key));
	}
	
	public static List<?> listValueForKeyPath(Object object,String keyPath){
		return listValue(objectValueForKeyPath(object,keyPath));
	}
}
