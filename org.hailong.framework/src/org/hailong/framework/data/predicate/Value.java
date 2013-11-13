package org.hailong.framework.data.predicate;

import org.hailong.framework.data.IDataEntityRawData;

public class Value {

	private Object _value;
	
	protected Value(){
		
	}
	
	public Value(Integer value){
		_value = value;
	}
	
	public Value(Long value){
		_value = value;
	}
	
	public Value(Boolean value){
		_value = value;
	}
	
	public Value(Double value){
		_value = value;
	}
	
	public Value(Float value){
		_value = value;
	}
	
	public Value(String value){
		_value = value;
	}
	
	public Object getValue(IDataEntityRawData data){
		return _value;
	}
	
	/**
	 * 等于
	 * @param value
	 * @return
	 */
	public Exp eq(Value value){
		return new ExpEqual(this,value);
	}
	
	/**
	 * 不等于
	 * @param value
	 * @return
	 */
	public Exp notEq(Value value){
		return new ExpNotEqual(this,value);
	}
	
	/**
	 * 大于
	 * @param value
	 * @return
	 */
	public Exp g(Value value){
		return new ExpGreater(this,value);
	}
	
	/**
	 * 大于等于
	 * @param value
	 * @return
	 */
	public Exp ge(Value value){
		return new ExpGreaterEqual(this,value);
	}
	
	/**
	 * 小于
	 * @param value
	 * @return
	 */
	public Exp l(Value value){
		return new ExpLess(this,value);
	}
	
	/**
	 * 小于等于
	 * @param value
	 * @return
	 */
	public Exp le(Value value){
		return new ExpLess(this,value);
	}
	
	
	private static boolean isNumber(Object v){
		Class<?> type = v.getClass();
		return type == int.class || type == Integer.class
				|| type == short.class || type == Short.class
				|| type == float.class || type == Float.class
				|| type == double.class || type == Double.class
				|| type == long.class || type == Long.class;
	}
	
	public static double compare(Object v1 ,Object v2){
		if(v1 ==v2){
			return 0;
		}
		else if(v1 ==null){
			if(isNumber(v2)){
				return (0 - Double.valueOf(v2.toString()));
			}
			else{
				return -1;
			}
		}
		else if(v2 == null) {
			if(isNumber(v1)){
				return Double.valueOf(v1.toString()) - 0;
			}
			else{
				return 1;
			}
		}
		else{
			if(isNumber(v1) || isNumber(v2)){
				return Double.valueOf(v1.toString()) - Double.valueOf(v2.toString());
			}
			else{
				return String.valueOf(v1).compareTo(String.valueOf(v2));
			}
		}
	}
	
	public String sql(String prefix,String suffix){
		if(_value == null){
			return "NULL";
		}
		if(_value instanceof String){
			String str = (String)_value;
			StringBuffer sb = new StringBuffer();
			sb.append("'");
			for(int i=0;i<str.length();i++){
				char c = str.charAt(i);
				if(c == '\n'){
					sb.append("\\n");
				}
				else if(c == '\r'){
					sb.append("\\r");
				}
				else if(c == '\t'){
					sb.append("\\t");
				}
				else if(c == '\''){
					sb.append("''");
				}	
				else{
					sb.append(c);
				}
			}
			sb.append("'");
			return sb.toString();
		}
		else{
			return String.valueOf(_value);
		}
	}
}
