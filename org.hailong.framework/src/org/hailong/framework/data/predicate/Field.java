package org.hailong.framework.data.predicate;

import org.hailong.framework.data.IDataEntityRawData;
import org.hailong.framework.data.annotation.DataField;

public class Field extends Value {

	private DataField _field;
	
	public Field(DataField field){
		_field = field;
	}
	
	public DataField getField(){
		return _field;
	}
	
	@Override
	public Object getValue(IDataEntityRawData data){
		if(_field != null && data !=null){
			return data.getValue(_field);
		}
		return null;
	}
	
	public Exp in(Value...values){
		return new ExpIn(this,values);
	}
	
	public Exp notIn(Value ...values){
		return new ExpNotIn(this,values);
	}
	
	@Override
	public String sql(String prefix,String suffix){
		return prefix+_field.value()+suffix;
	}
}
