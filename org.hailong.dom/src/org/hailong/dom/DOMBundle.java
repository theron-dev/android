package org.hailong.dom;

import android.content.Context;

public class DOMBundle {

	private final Context _context;
	private final float _displayScale;
	
	public DOMBundle(Context context){
		_context = context;
		_displayScale = context.getResources().getDisplayMetrics().density;
	}
	
	public Context getContext(){
		return _context;
	}
	
	public float displayScale(){
		return _displayScale;
	}
}
