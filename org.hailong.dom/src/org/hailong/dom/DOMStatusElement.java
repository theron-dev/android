package org.hailong.dom;

import java.util.HashSet;
import java.util.Set;

import org.hailong.core.Color;

public class DOMStatusElement extends DOMActionElement {

	@Override
	public void setAttributeValue(String name,String value){
		super.setAttributeValue(name, value);
		if("status".equals(name)){
			for(DOMElement child : getChilds()){
				refreshStatusElement(child,value);
			}
			setNeedsDisplay();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void refreshStatusElement(DOMElement element,String status){
		
		Set<String> statusSet = (Set<String>) element.getValue("statusSet");
		
		if(statusSet == null){
			
			String s = element.getAttributeValue("status");
			
			statusSet = new HashSet<String>(1);
			
			if(s != null){
				String[] ss = s.split("[|, ;]");
				for(String sss :  ss){
					statusSet.add(sss);
				}
			}
			
			element.setValue("statusSet", statusSet);
			
		}
		
		if(status == null || statusSet.size() == 0 || statusSet.contains(status)){
			element.setAttributeValue("hidden", "false");
		}
		else {
			element.setAttributeValue("hidden", "true");
		}
	}
	
	@Override
	public void addChild(DOMElement element,int index){
		super.addChild(element, index);
		refreshStatusElement(element,getAttributeValue("status"));
	}
	
	public Color getBackgroundColor(){
		String status = getAttributeValue("status");
		Color c = null;
		
		if(status != null && status.length() >0){
			c = colorValue("background-color-" + status,null);
		}
		
		if(c == null){
			c = colorValue("background-color",new Color());
		}
		
		return c;
	}
}
