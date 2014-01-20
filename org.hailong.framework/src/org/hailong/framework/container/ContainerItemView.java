package org.hailong.framework.container;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ContainerItemView extends FrameLayout implements IContainerView{

	public Container _container;
	
	public ContainerItemView(Context context) {
		super(context);
	}
	
	public ContainerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	public ContainerItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	
	public Container getContainer(){
		
		if(_container == null){
			_container = new Container(this);
		}
		
		return _container;
	}

	public void setContainer(Container container){
		_container = container;
	}
}
