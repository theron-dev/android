package org.hailong.service.views;

import java.io.File;

import org.hailong.service.tasks.IImageTask;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewTask extends ImageView implements IImageTask {

	private String _imageUrl = null;
	private boolean _forceDownload = false;
	private boolean _hasImage = false;
	private boolean _loading = false;
	private Object _source;
	
	
	public ImageViewTask(Context context) {
		super(context);

	}
	
	public ImageViewTask(Context context, AttributeSet attrs) {
		super(context, attrs);

	}


	public ImageViewTask(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}
	
	public Object getSource(){
		return _source;
	}
	
	public void setSource(Object source){
		_source = source;
	}
	
	@android.view.ViewDebug.ExportedProperty
	public String getImageUrl(){
		return _imageUrl;
	}
	
	public void setImageUrl(String imageUrl){
		_imageUrl = imageUrl;
		_hasImage = false;
	}
	
	public String getResourceUri() {
		return _imageUrl;
	}


	public boolean isNeedDownload() {
		return _imageUrl != null && _imageUrl.length() >0 && !_hasImage;
	}


	public Object setResourceLocalFile(File localUri) {
		if(localUri != null ){
			Drawable image = Drawable.createFromPath(localUri.getPath());
			setImageDrawable(image);
			_hasImage = true;
			return image;
		}
		return null;
	}
	
	public boolean isForceDownload(){
		return _forceDownload;
	}
	
	public void setForceDownload(boolean forceDownload){
		_forceDownload = forceDownload;
	}

	public void setResourceObject(Object obj) {
		if(obj != null){
			setImageDrawable((Drawable)obj);
			_hasImage = true;
		}
	}

	public void onException(Exception ex) {

	}
	
	public boolean isLoading(){
		return _loading;
	}
	
	public void setLoading(boolean loading){
		_loading = loading;
	}

}
