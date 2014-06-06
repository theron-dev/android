package org.hailong.app.controllers;

import java.io.File;
import java.io.Reader;
import org.hailong.app.App;
import org.hailong.app.AppContext;
import org.hailong.app.R;
import org.hailong.controller.C;
import org.hailong.controller.Controller;
import org.hailong.core.URL;
import org.hailong.dom.DOMBundle;
import org.hailong.dom.DOMDocument;
import org.hailong.dom.DOMDocumentView;
import org.hailong.dom.DOMElement;
import org.hailong.dom.DOMImageElement;
import org.hailong.dom.IDOMViewEntity;
import org.hailong.dom.parser.DOMParser;
import org.hailong.service.tasks.IImageTask;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DocumentController<T extends AppContext> extends Controller<T> implements DOMDocumentView.OnElementVisableListener,DOMDocumentView.OnElementActionListener{

	private DOMDocumentView _documentView;
	private DOMDocument _document;
	private DOMBundle _bundle;;
	private OnDocumentControllerListener<T> _OnDocumentControllerListener;
	
	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

        _documentView = (DOMDocumentView) getView().findViewById(R.id.documentView);
		_documentView.setOnElementVisableListener(this);
		_documentView.setOnElementActionListener(this);
		
		if(_documentView != null && _document != null){
			_documentView.setElement(_document.getRootElement());
			downloadImagesForView((View) _documentView);
		}
		
    }
	
	public DOMDocument getDocument(){
		return _document;
	}
	
	public void setDocument(DOMDocument document){
		_document = document;
	}
	
	public DOMBundle getBundle(){
		if(_bundle == null){
			_bundle = new DOMBundle(getActivity(),getClass());
		}
		return _bundle;
	}
	
	public void setBundle(DOMBundle bundle){
		_bundle = bundle;
	}
	
	public DOMDocumentView getDocumentView(){
		return _documentView;
	}
	
	public void loadXMLContent(Reader reader,URL documentURL){
		
		if(getActivity() != null){
		
			DOMDocument document = new DOMDocument(getBundle());
	
			document.setDocumentURL(documentURL);
			
			DOMParser parser = DOMParser.defaultParser();
			
			try {
				parser.parseHTML(reader, document);
			} catch (Exception e) {
				Log.d(App.TAG, Log.getStackTraceString(e));
			}
			
			document.setStyleSheet(getServiceContext().getStyleSheet());
	
			setDocument(document);
			
			if(_documentView != null){
				_documentView.setElement(_document.getRootElement());
				downloadImagesForView((View) _documentView);
			}
		}
		else {
			Log.e(App.TAG, "loadXMLContent not foud Activity");
		}
	}

	@Override
	public void onElementVisable(DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element) {
		
		if(viewEntity instanceof View){
			downloadImagesForView((View) viewEntity);
		}
		
		downloadImagesForElement(element,viewEntity);
	}

	public void downloadImagesForElement(DOMElement element,IDOMViewEntity viewEntity){
		
		if(!element.isViewEntity(viewEntity)){
			return;
		}
		
		if(element instanceof DOMImageElement){
			
			DOMImageElement imageElement = (DOMImageElement) element;
			
			IImageTask imageTask = (IImageTask) imageElement.getImageLoader();
			
			if(imageTask == null && imageElement.getImage() == null){
				
				String src = imageElement.getAttributeValue("src");
				
				if(src != null && src.indexOf("://") >=0){
					imageTask = new DOMImageElementTask(imageElement);
					imageElement.setImageLoader(imageTask);
				}
			}
			
			if(imageTask != null && ! imageTask.isLoading()){
				
				try {
					imageTask.setSource(this);
					getServiceContext().handle(IImageTask.class, imageTask,0);
				} catch (Exception e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
				
			}
		}
		
		for(DOMElement el : element.getChilds()){
			downloadImagesForElement(el,viewEntity);
		}
		
	}

	public void cancelDownloadImagesForElement(DOMElement element,IDOMViewEntity viewEntity){
		
		if(!element.isViewEntity(viewEntity)){
			return;
		}
		
		if(element instanceof DOMImageElement){
			
			DOMImageElement imageElement = (DOMImageElement) element;
			
			IImageTask imageTask = (IImageTask) imageElement.getImageLoader();
			
			if(imageTask != null && imageTask.isLoading()){
				
				try {
					getServiceContext().cancelHandle(IImageTask.class, imageTask);
				} catch (Exception e) {
					Log.d(C.TAG, Log.getStackTraceString(e));
				}
				
			}
			
			imageElement.setImageLoader(null);
			
		}
		
		for(DOMElement el : element.getChilds()){
			cancelDownloadImagesForElement(el,viewEntity);
		}

	}
	
	@Override
	public void onElementAction(DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element) {
		if(_OnDocumentControllerListener != null){
			_OnDocumentControllerListener.onElementAction(this, documentView, viewEntity, element);
		}
	}
	
	public OnDocumentControllerListener<T> getOnDocumentControllerListener(){
		return _OnDocumentControllerListener;
	}
	
	public void setOnDocumentControllerListener(OnDocumentControllerListener<T> onDocumentControllerListener){
		_OnDocumentControllerListener = onDocumentControllerListener;
	}
	
	public static interface OnDocumentControllerListener<T extends AppContext> {
		
		public void onElementAction(DocumentController<T> documentController,DOMDocumentView documentView,
			IDOMViewEntity viewEntity, DOMElement element);
		
	}
	
	public static class DOMImageElementTask implements IImageTask {

		private final DOMImageElement _element;
		private boolean _loading;
		private Object _source;
		
		public DOMImageElementTask(DOMImageElement element){
			_element = element;
		}
		
		@Override
		public String getResourceUri() {
			return _element.getAttributeValue("src");
		}

		@Override
		public boolean isNeedDownload() {
			return _element.getImage() == null;
		}

		@Override
		public boolean isForceDownload() {
			return false;
		}

		@Override
		public boolean isLoading() {
			return _loading;
		}

		@Override
		public void setLoading(boolean loading) {
			_loading = loading;
		}

		public Object setResourceLocalFile(File localUri) {
			if(localUri != null ){
				Drawable image = Drawable.createFromPath(localUri.getPath());
				_element.setImage(image);
				return image;
			}
			return null;
		}
		
		public void setResourceObject(Object obj) {
			if(obj != null){
				_element.setImage((Drawable)obj);
			}
		}

		public void onException(Exception ex) {

		}
		
		public Object getSource(){
			return _source;
		}
		
		public void setSource(Object source){
			_source = source;
		}
		
	}
}
