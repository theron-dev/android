package org.hailong.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import org.xmlpull.v1.XmlPullParser;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewLayout {
	
	private String _viewLayout;
	private int _layout;
	
	public ViewLayout(int layout){
		_viewLayout = null;
		_layout = layout;
	}
	
	public ViewLayout(String viewLayout){
		_viewLayout = viewLayout;
		_layout = 0;
		
		if(!viewLayout.endsWith(".xml")){
			int index = viewLayout.lastIndexOf(".");
			
			if(index >0){
			
				try {
					Class<?> c = Class.forName(viewLayout.substring(0,index));
					Field field = c.getField(viewLayout.substring(index +1));
					_layout = field.getInt(null);
				} catch (Exception e) {
					
					index = viewLayout.lastIndexOf(".layout");
				
					try {
						Class<?> c = Class.forName(viewLayout.substring(0,index));
						
						for(Class<?> sub : c.getClasses()){
							if("layout".equals(sub.getSimpleName())){
								Field field = sub.getField(viewLayout.substring(viewLayout.lastIndexOf(".") +1));
								_layout = field.getInt(null);
								break;
							}
						}
					} catch (Exception ex) {
						Log.d(C.TAG, Log.getStackTraceString(ex));
					}

				}
			}
		}
		
	}
	
	public View getView(LayoutInflater inflater,ViewGroup parent){
		
		if(_viewLayout != null && _viewLayout.endsWith(".xml")){
			
			InputStream inputStream = null;
			if(_viewLayout.startsWith("/")){
				try {
					inputStream = new FileInputStream(_viewLayout);
				} catch (FileNotFoundException e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
			}
			else{
				try {
					inputStream = inflater.getContext().openFileInput(_viewLayout);
				} catch (FileNotFoundException e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
			}
			if(inputStream != null){
				try {
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(inputStream, "utf-8");
					return inflater.inflate(parser, parent);
				} catch (Exception e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
			}
		}
		else if(_layout != 0){
			return inflater.inflate(_layout, parent);
		}
		
		return null;
	}
}
