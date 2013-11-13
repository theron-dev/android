package org.hailong.framework.data.predicate;

import org.hailong.framework.data.IDataEntityRawData;

class ExpOr extends ExpBase {

	public ExpOr(Exp left, Exp right) {
		super(left, right);
		
	}
	
	@Override
	public boolean result(IDataEntityRawData data){
		return _left.result(data) || _right.result(data);
	}
	
	@Override
	public String sql(String prefix,String suffix){
		return "("+_left.sql(prefix, suffix)+ " OR "+_right.sql(prefix, suffix)+")";
	}
}
