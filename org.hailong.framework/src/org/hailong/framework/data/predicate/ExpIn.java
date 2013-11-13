package org.hailong.framework.data.predicate;

import org.hailong.framework.data.IDataEntityRawData;

class ExpIn extends Exp {
	
	private Field _field;
	private Value[] _values;
	
	public ExpIn(Field field , Value...values){
		_field = field;
		_values = values;
	}
	
	public Field getField(){
		return _field;
	}
	
	public Value[] getValues(){
		return _values;
	}
	
	@Override
	public boolean result(IDataEntityRawData data){
		Object value = _field.getValue(data);
		if(value == null){
			return false;
		}
		for(int i=0;i<_values.length;i++){
			if(value.equals(_values[i].getValue(data))){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String sql(String prefix,String suffix){
		StringBuffer sb = new StringBuffer();
		sb.append("(").append(_field.sql(prefix, suffix)).append(" IN [");
		boolean first = true;
		for(Value value : _values){
			if(first){
				first = false;
			}
			else{
				sb.append(",");
			}
			sb.append(value.sql(prefix, suffix));
		}
		sb.append("] )");
		return sb.toString();
	}
}
