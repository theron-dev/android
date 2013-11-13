package org.hailong.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class PLIST {
	
	public static String getNodeAttribute(Node node, String name){
		Node attr = node.getAttributes().getNamedItem(name);
		return attr != null ? attr.getNodeValue(): null;
	}
	
	public static String getNodeContentString(Node node){
		NodeList childs = node.getChildNodes();
		int length = childs.getLength();
		for(int i=0;i<length;i++){
			Node child = childs.item(i);
			if(child.getNodeType() == Node.TEXT_NODE){
				return child.getNodeValue();
			}
		}
		return null;
	}
	
	public static Object parseXmlObject(Node element){
		String nodeName = element.getNodeName();
		if("dict".equals(nodeName)){
			Map<String,Object> dict = new HashMap<String,Object>();
			NodeList childs = element.getChildNodes();
			String key = null;
			for(int j=0;j<childs.getLength();j++){
				Node node = childs.item(j);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					if(key == null){
						if("key".equals(node.getNodeName())){
							key = getNodeContentString(node);
						}
					}
					else{
						dict.put(key, parseXmlObject(node));
						key = null;
					}
				}
			}
			return dict;
		}
		else if("array".equals(nodeName)){
			List<Object> array = new ArrayList<Object>();
			NodeList childs = element.getChildNodes();
			for(int j=0;j<childs.getLength();j++){
				Node node = childs.item(j);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					array.add(parseXmlObject(node));
				}
			}
			return array;
		}
		else if("string".equals(nodeName)){
			return getNodeContentString(element);
		}
		else if("true".equals(nodeName)){
			return new Boolean(true);
		}
		else if("false".equals(nodeName)){
			return new Boolean(false);
		}
		else if("integer".equals(nodeName)){
			return Integer.valueOf(getNodeContentString(element));
		}
		else if("real".equals(nodeName)){
			return Double.valueOf(getNodeContentString(element));
		}
		return null;
	}
	
	
}
