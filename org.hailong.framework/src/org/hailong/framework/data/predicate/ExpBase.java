package org.hailong.framework.data.predicate;

class ExpBase extends Exp {

	protected Exp _left;
	protected Exp _right;
	
	protected ExpBase(Exp left,Exp right){
		_left =left;
		_right =right;
	}
	
	public Exp getLeft(){
		return _left;
	}
	
	public Exp getRight(){
		return _right;
	}
}
