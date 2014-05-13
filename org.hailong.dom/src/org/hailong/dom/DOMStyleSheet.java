package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;

public class DOMStyleSheet {

	private List<DOMStyle> _styles;
	
	public void addStyle(DOMStyle style){
		if(_styles == null){
			_styles = new ArrayList<DOMStyle>(4);
		}
		_styles.add(style);
	}
	
	public void removeStyle(DOMStyle style){
		if(_styles != null){
			_styles.remove(style);
		}
	}
	
	public void removeAllStyles(){
		if(_styles != null){
			_styles.clear();
		}
	}
	
	public DOMStyle selectorStyleName(String styleName){
		
		if(_styles != null && styleName != null){
			
			DOMStyle style = new DOMStyle();
			
			style.setName(styleName);
			
			String[] names = styleName.split(" ");
			
			for(String name : names){
				for(DOMStyle s : _styles){
					if(name.equals(s.getName())){
						for(String key : s.getValues().keySet()){
							style.setValue(key, s.getValue(key));
						}
					}
				}
			}
			
		}
		
		return null;
	}
}
