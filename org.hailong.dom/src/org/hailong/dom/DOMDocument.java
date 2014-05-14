package org.hailong.dom;

import java.util.HashMap;
import java.util.Map;

import org.hailong.core.URL;

public class DOMDocument {

	private final DOMBundle _bundle;
	private DOMElement _rootElement;
	private URL _documentURL;
	private Map<String,DOMElement> _elementsById;
	private DOMStyleSheet _styleSheet;
	
	public DOMDocument(DOMBundle bundle){
		_bundle = bundle;
	}
	
	public DOMBundle getBundle(){
		return _bundle;
	}
	
	public DOMElement getRootElement(){
		return _rootElement;
	}
	
	private void putElementsById(DOMElement element){
		
		String id = element.getAttributeValue("id");
		
		if(id != null){
			_elementsById.put(id, element);
		}
		
		for(DOMElement child : element.getChilds()){
			putElementsById(child);
		}
	}
	
	public void setRootElement(DOMElement rootElement){
		if(_rootElement != rootElement){
			
			if(_rootElement != null){
				_rootElement.setDocument(null);
			}
			
			_rootElement = rootElement;
		
			if(_elementsById != null){
				_elementsById.clear();
			}
			
			if(_rootElement != null){
				
				_rootElement.setDocument(this);
				
				if(_elementsById == null){
					_elementsById = new HashMap<String,DOMElement>(4);
				}
				
				putElementsById(_rootElement);
				
				applySheetStyle(_rootElement);
			}
			
		}
	}
	
	public URL getDocumentURL(){
		return _documentURL;
	}
	
	public void setDocumentURL(URL documentURL){
		_documentURL = documentURL;
	}
	
	public void applySheetStyle(DOMElement element){
		if(element != null && _styleSheet != null){
			String styleName = element.getAttributeValue("class");
			if(styleName != null){
				element.setStyle(_styleSheet.selectorStyleName(styleName));
			}
			for(DOMElement child : element.getChilds()){
				applySheetStyle(child);
			}
		}
	}
	
	public void applySheetStyle(){
		applySheetStyle(_rootElement);
	}
	
	public DOMStyleSheet getStyleSheet(){
		return _styleSheet;
	}
	
	public void setStyleSheet(DOMStyleSheet styleSheet){
		if(_styleSheet != styleSheet){
			_styleSheet = styleSheet;
			applySheetStyle();
		}
	}
}
