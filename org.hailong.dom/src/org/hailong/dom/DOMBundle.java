package org.hailong.dom;

import java.io.InputStream;
import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DOMBundle {

	private final Context _context;
	private final float _displayScale;
	private final Class<?> _packageClass;
	
	public DOMBundle(Context context,Class<?> packageClass){
		_packageClass = packageClass;
		_context = context;
		_displayScale = context.getResources().getDisplayMetrics().density;
	}
	
	public Context getContext(){
		return _context;
	}
	
	public float displayScale(){
		return _displayScale;
	}
	
	public Drawable getImageForURI(String uri){
		
		if(uri != null){
			if(uri.startsWith("@")){
				
				int i = uri.lastIndexOf(".");
				
				if(i >=0){
					
					String name = uri.substring(i + 1);
					String className = uri.substring(1,i);
					
					i = className.lastIndexOf(".");
					
					if(i >=0){
						
						className = className.substring(0,i).concat("$").concat(className.substring(i + 1));
						
						try {
							
							Class<?> clazz = Class.forName(className);
							
							Field field = clazz.getField(name);
							
							int resId = field.getInt(null);
							
							return _context.getResources().getDrawable(resId);
							
						} catch (Exception e) {
							Log.d(DOM.TAG, Log.getStackTraceString(e));
						}
						
					}
					
					
				}
				else if(_packageClass != null){
					
					String srcName = uri.substring(1);
					
					try {
						InputStream in = _packageClass.getResourceAsStream(srcName);
						Drawable image = Drawable.createFromStream(in, srcName);
						in.close();
						return image;
					} catch (Throwable e) {
						Log.e(DOM.TAG, Log.getStackTraceString(e));
					}
				}
				
			}
			else if(uri.indexOf("://") >=0){
				
			}
			else {
				if(_packageClass != null){
					InputStream in = _packageClass.getResourceAsStream(uri);
					if(in != null){
						return Drawable.createFromStream(in, uri);
					}
				}
			}
		}
		
		return null;
	}
}
