package org.hailong.framework.container;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ContainerView extends FrameLayout {

	public Container _container;
	
	public ContainerView(Context context) {
		super(context);

	}
	
	public ContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	
	public ContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	
	public Container getContainer(){
		
		if(_container == null){
			_container = new Container(this);
		}
		
		return _container;
	}

}
