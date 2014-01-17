package org.hailong.framework.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.hailong.framework.Framework;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;

public class ViewLayout {
	
	private String _viewLayout;
	private int _layout;
	private LayoutInflater _layotuInflater;
	
	public ViewLayout(Context context,int layout){
		_layotuInflater = LayoutInflater.from(context);
		_viewLayout = null;
		_layout = layout;
	}
	
	public ViewLayout(Context context,String viewLayout){
		_layotuInflater = LayoutInflater.from(context);
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
						Log.d(Framework.TAG, Log.getStackTraceString(ex));
					}

				}
			}
		}
		
	}
	
	public View getView(){
		
		if(_viewLayout != null && _viewLayout.endsWith(".xml")){
			
			InputStream inputStream = null;
			if(_viewLayout.startsWith("/")){
				try {
					inputStream = new FileInputStream(_viewLayout);
				} catch (FileNotFoundException e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
			}
			else{
				try {
					inputStream = _layotuInflater.getContext().openFileInput(_viewLayout);
				} catch (FileNotFoundException e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
			}
			if(inputStream != null){
				try {
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(inputStream, "utf-8");
					return _layotuInflater.inflate(parser, null);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
			}
		}
		else if(_layout != 0){
			return _layotuInflater.inflate(_layout, null);
		}
		
		return null;
	}
}
