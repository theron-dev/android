package org.hailong.dom.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hailong.dom.DOMDocument;
import org.hailong.dom.DOMElement;
import org.hailong.dom.DOMLayoutElement;
import org.hailong.dom.DOMStyle;
import org.hailong.dom.DOMStyleSheet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class DOMParser {

	private static DOMParser _defaultParser;
	
	public static DOMParser defaultParser(){
		if(_defaultParser == null){
			_defaultParser = new DOMParser();
		}
		return _defaultParser;
	}
	
	public static void setDefaultParser(DOMParser parser){
		_defaultParser = parser;
	}
	
	private Map<String,Class<?>> _elementClasss;
	
	public void putElementClass(String name,Class<?> elementClass){
		if(_elementClasss == null){
			_elementClasss = new HashMap<String,Class<?>>(4);
		}
		_elementClasss.put(name, elementClass);
	}
	
	public void parseHTML(Reader reader, DOMElement toElement,int atIndex) throws SAXException, IOException, ParserConfigurationException{
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser newSAXParser = saxParserFactory.newSAXParser();
		XMLReader xml = newSAXParser.getXMLReader();
		
		DOMContentHandler contentHandler = new DOMContentHandler(toElement, atIndex);
		
		if(_elementClasss != null){
			for(String key : _elementClasss.keySet()){
				contentHandler.putElementClass(key, _elementClasss.get(key));
			}
		}
		
		xml.setContentHandler(new DOMContentHandler(toElement, atIndex));
		xml.parse(new InputSource(reader));

	}
	
	public void parseHTML(Reader reader, DOMDocument document) throws SAXException, IOException, ParserConfigurationException {
		
		DOMElement element = new DOMLayoutElement();
		
		element.setAttributeValue("width", "100%");
		element.setAttributeValue("height", "100%");

		parseHTML(reader,element,0);
		
		if(element.getChildCount() ==1){
			
			element = element.getChildAt(0);
			element.removeFromParent();
			
			document.setRootElement(element);
		}
		else {
			document.setRootElement(element);
		}
		
		
	}
	
	public void parseCSS(String css, DOMStyleSheet styleSheet) {
		
		DOMCSSTokenizer tokenizer = new DOMCSSTokenizer(css);
		
		char s = 0;
		char c;
		
		DOMStyle style = null;
		String key = null;
		String value = null;
		
		while((c = tokenizer.nextCharExclude(' ','\t','\n','\r')) != 0){
			
			if(s == 0){
				
				if(c == '/'){
					tokenizer.nextCharExcludeNote();
				}
				else {
					
					tokenizer.backChar();
					
					String name = tokenizer.nextKey();

					if(tokenizer.backChar() != '{'){
	
						if(tokenizer.nextCharTo('{') == '{'){
						
							style = new DOMStyle();
							
							style.setName(name);
							
							s = 1;
							
						}
					}
					
				}
				
			}
			else if(s == 1){
				
				if(c == ':'){
					key = "";
					s = 2;
				}
				else if(c == '/'){
					tokenizer.nextCharExcludeNote();
				}
				else {
					
					key = tokenizer.nextKey();
					
					c = tokenizer.backChar();
					
					if(c == ' ' || c == '\r' || c == '\n'){
						c = tokenizer.nextCharTo(':',';','}');
					}
					
					if(c == ':'){
						s = 2;
					}
					else if(c == ';'){
						style.setValue(key, "");
					}
					else if(c == '}'){
						styleSheet.addStyle(style);
						s = 0;
					}
					
				}
				
			}
			else if(s == 2){
				
				if(c == ';'){
					style.setValue(key, "");
					s = 1;
				}
				else if(c == '}'){
					style.setValue(key, "");
					styleSheet.addStyle(style);
					s = 0;
				}
				else {
					
					value = tokenizer.nextValue();
					
					c = tokenizer.backChar();
					
					if(c == ';'){
						style.setValue(key, value);
						s = 1;
					}
					else if(c == '}'){
						style.setValue(key, value);
						styleSheet.addStyle(style);
						s = 0;
					}
					
				}
			}
			
		}
		
	}
	
}
