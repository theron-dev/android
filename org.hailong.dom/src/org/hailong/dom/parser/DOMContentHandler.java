package org.hailong.dom.parser;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

import org.hailong.dom.DOMActionElement;
import org.hailong.dom.DOMCanvasElement;
import org.hailong.dom.DOMElement;
import org.hailong.dom.DOMImageElement;
import org.hailong.dom.DOMLabelElement;
import org.hailong.dom.DOMLinkElement;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

@SuppressLint("DefaultLocale")
public class DOMContentHandler implements ContentHandler {

	private Map<String,String> _prefixMapping;
	private Map<String,Class<?>> _elementClasss;
	
	private DOMElement _curElement;
	
	private DOMElement _toElement;
	private int _atIndex;
	private StringBuilder _textBuilder;
	
	public DOMContentHandler(DOMElement toElement, int atIndex){
		_toElement = toElement;
		_atIndex = atIndex;
		_curElement = toElement;
		
		_elementClasss = new HashMap<String,Class<?>>(4);
		_elementClasss.put("img", DOMImageElement.class);
		_elementClasss.put("label", DOMLabelElement.class);
		_elementClasss.put("a", DOMLinkElement.class);
		_elementClasss.put("action", DOMActionElement.class);
		
	}
	
	@Override
	public void characters(char[] chars, int index, int length) throws SAXException {
		
		if(_textBuilder == null){
			_textBuilder=  new StringBuilder();
		}
	
		_textBuilder.append(chars,index,length);
	}

	@Override
	public void endDocument() throws SAXException {
		
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		if(_textBuilder != null && _textBuilder.length() >0 ){
			
			if(_curElement.getChildCount() ==0 ){
				_curElement.setText(_textBuilder.toString());
			}
			
			_textBuilder.delete(0, _textBuilder.length());
		}
		_curElement = _curElement.getParent();
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		
	}

	@Override
	public void ignorableWhitespace(char[] chars, int index, int length)
			throws SAXException {
		
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		Class<?> elementClass;
		
		if(uri != null && uri.length() > 0){
			
			String className = uri.concat(".").concat(localName);
			
			try {
				elementClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				elementClass = null;
			}
		}
		else {
			elementClass = _elementClasss.get(localName.toLowerCase());
		}
		
		if(elementClass == null){
			elementClass = DOMCanvasElement.class;
		}
		
		DOMElement element ;
		
		try {
			element = (DOMElement) elementClass.newInstance();
		} catch (Exception e) {
			throw new SAXException(e);
		} 
		
		element.setNamespace(uri);
		element.setName(localName);
		
		int length = attributes.getLength();
		
		for(int i=0;i<length;i++){
			element.setAttributeValue(attributes.getLocalName(i), attributes.getValue(i));
		}
		
		if(_curElement == _toElement){
			_toElement.addChild(element, _atIndex ++) ;
		}
		else {
			_curElement.addChild(element);
		}
		
		_curElement = element;
		
		if(_textBuilder != null){
			_textBuilder.delete(0, _textBuilder.length());
		}
		
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if(_prefixMapping == null){
			_prefixMapping = new HashMap<String,String>(4);
		}
		_prefixMapping.put(prefix, uri);
	}

}
