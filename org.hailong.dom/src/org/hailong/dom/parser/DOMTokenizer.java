package org.hailong.dom.parser;

public class DOMTokenizer {

	private String _text;
	private int _index;
	
	public DOMTokenizer(String text){
		_text = text;
		_index = 0;
	}
	
	public char curChar(){
		if(_index < _text.length()){	
			return _text.charAt(_index);  
		}
		return 0;
	}
	
	public char nextChar(){
		
		if(_index < _text.length()){	
			return _text.charAt(_index ++);  
		}
		
		return 0;
	}
	
	public char backChar(){
		_index --;
		return nextChar();
	}
	
	public char nextCharExclude(char ... chars){
		
		char c ;
		
		while((c = nextChar()) != 0){
			
			boolean skip = false;
			for(char cc : chars){
				if(c == cc){
					skip = true;
					break;
				}
			}
			
			if(!skip){
				return c;
			}
		}
		
		return c;
	}
	
	public char nextCharTo(char ...toChars){
		
		char c ;
		
		while((c = nextChar()) != 0){
			
			for(char cc : toChars){
				if(c == cc){
					return c;
				}
			}
			
		}
		
		return c;
	}
	
	public String nextString(char ... toChars){
		
		StringBuilder sb = new StringBuilder();
		
		char c;
		char s = 0;
		
		while((c = nextChar()) != 0){
			
			if(s == 0){
				if(c == '\\'){
					s = 1;
				}
				else {
					for(char cc : toChars){
						if(c == cc){
							return sb.toString();
						}
					}
					sb.append(c);
				}
			}
			else {
				if(c == 'n'){
					sb.append('\n');
				}
				else if(c == 't'){
					sb.append('\t');
				}
				else if(c == '\\'){
					sb.append('\\');
				}
				else {
					sb.append(c);
				}
				s = 0;
			}
			
		}
		
		return sb.toString();
	}
}
