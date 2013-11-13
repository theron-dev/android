package org.hailong.framework.data.predicate;

import org.hailong.framework.data.IDataEntityRawData;

class ExpNot extends Exp {

	private Exp _exp;
	
	public ExpNot(Exp exp){
		_exp = exp;
	}
	
	@Override
	public boolean result(IDataEntityRawData data){
		return !_exp.result(data);
	}
	
	@Override
	public String sql(String prefix,String suffix){
		return "( NOT "+_exp.sql(prefix, suffix)+")";
	}
}
