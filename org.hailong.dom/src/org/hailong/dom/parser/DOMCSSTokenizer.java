package org.hailong.dom.parser;

public class DOMCSSTokenizer extends DOMTokenizer {

	public DOMCSSTokenizer(String text) {
		super(text);
		
	}

	public char nextCharExcludeNote(){
		
		char c = nextChar();
		
		if(c == '/'){
			return nextCharTo('\n');
		}
		else if(c == '*'){
			
			while((c = nextCharTo('*')) == '*'){
				
				c = nextChar();
				
				if(c == '/'){
					return c;
				}
				
			}
		}
		
		return c;
	}
	
	public String nextKey(){
		return nextString(' ','\t','\r','\n',':',';','{','}');
	}
	
	public String nextValue(){
		return nextString(';','{','}');
	}
}
