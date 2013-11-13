package org.hailong.framework.data.predicate;

import org.hailong.framework.data.IDataEntityRawData;


class ExpValue extends Exp{
	
	protected Value _left;
	protected Value _right;
	
	protected ExpValue(Value left,Value right){
		_left =left;
		_right =right;
	}
	
	public Value getLeft(){
		return _left;
	}
	
	public Value getRight(){
		return _right;
	}

	public double compare(IDataEntityRawData data) {
	
		Object v1 = _left.getValue(data);
		Object v2 = _right.getValue(data);
		
		return Value.compare(v1,v2);
	}
	
}
