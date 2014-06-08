package org.hailong.core;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public final class URL {

	private Map<String,String> _queryValues;
	private String _url;
	private String _scheme;
	private String _host;
	private int _port;
	private String _path;
	private String _query;
	private String _token;
	
	protected void _URL(String url){
		
		_url = url;
		
		int index = 0;
		int i = url.indexOf("://",index);
		
		if(i >=0){
			
			_scheme = url.substring(0,i);

			index = i + 3;
			
			i = url.indexOf("/",index);
			
			if(i >= 0){
				
				_host = url.substring(index,i );
				
				index = i;
				
				i = _host.indexOf(":");
				
				if(i >=0){
					_port = Integer.valueOf(_host.substring(i + 1));
					_host = _host.substring(0,i);
				}
				
				i = url.indexOf("?",index);
				
				if(i >= 0){
					
					_path = url.substring(index,i);
					
					index = i + 1;
					
					i = url.indexOf("#",index);
					
					if(i >=0){
						_query = url.substring(index,i);
						_token = url.substring(i + 1);
					}
					else{
						_query = url.substring(index);
					}
				}
				else{
					i = url.indexOf("#",index);
					if(i >=0){
						_path = url.substring(index, i);
						_token = url.substring(i + 1);
					}
					else{
						_path = url.substring(index);
					}
				}
			}
			else{
				_host = url.substring(index);
				
				i = _host.indexOf(":");
				
				if(i >=0){
					_port = Integer.valueOf(_host.substring(i + 1));
					_host = _host.substring(0,i);
				}
				
				_path = "/";
			}
		}
		
	}
	
	protected void _URL(String url,URL baseURL){
		
		if(baseURL != null){
			
			int index = url.indexOf("://");
			
			if(index <0){
				
				if(url.startsWith("/")){
					
					String b = baseURL.getScheme() + "://" +baseURL.getHost();
					
					if(baseURL.getPort() != 0){
						b += ":" + baseURL.getPort();
					}
					
					url = b + url;
				}
				else{
					
					String query = null;
					String path = null;
					
					index = url.indexOf("?");
					
					if(index >=0){
						path = url.substring(0,index);
						query = url.substring(index);
					}
					else {
						index = url.indexOf("#");
						if(index >=0){
							path = url.substring(0,index);
							query = url.substring(index);
						}
						else{
							path = url;
						}
					}
					
					String[] paths = path.split("/");
					String[] basePaths = baseURL.getPath().split("/");
					
					int length = basePaths.length -1;
					
					index = 0;
					
					for(int i=0;i<paths.length;i++){
						
						path = paths[i];
						
						if(".".equals(path)){
							index ++;
						}
						else if("..".equals(path)){
							length --;
							index ++;
						}
						else{
							break;
						}
					}
					
					url = "";
					
					for(int i=1;i<length;i++){
						url += "/" + basePaths[i];
					}
					
					for(int i=index;i<paths.length;i++){
						url += "/" + paths[i];
					}
					
					if(query != null){
						url = url + query;
					}
					
					String b = baseURL.getScheme() + "://" +baseURL.getHost();
					
					if(baseURL.getPort() != 0){
						b += ":" + baseURL.getPort();
					}
					
					url = b + url;
				}
			}
			
		}
		
		_URL(url);
	}
	
	protected void _URL(String url,URL baseURL,Map<String,String> queryValues){
		
		if(queryValues != null){
			
			int index = url.indexOf("#");
			String token = null;
			
			if(index >=0){
				token = url.substring(index);
				url = url.substring(0,index);
			}
			
			index = url.indexOf("?");
			
			for(String key : queryValues.keySet()){
				
				if(index >= 0){
					url += "&" ;
				}
				else{
					url += "?";
					index = 0;
				}
				
				url += key + "=" + encodeQueryValue(queryValues.get(key));
			}
			
			if(token != null){
				url += token;
			}
		}

		_URL(url,baseURL);
	}
	
	public URL(String url){
		_URL(url);
	}
	
	public URL(String url,URL baseURL){
		_URL(url,baseURL);
	}
	
	public URL(String url,URL baseURL,Map<String,String> queryValues){
		_URL(url,baseURL,queryValues);
	}
	
	public String getScheme(){
		return _scheme;
	}
	
	public String getHost(){
		return _host;
	}
	
	public int getPort(){
		return _port;
	}
	
	public String getPath(){
		return _path;
	}
	
	public String getQuery(){
		return _query;
	}
	
	public String getToken(){
		return _token;
	}
	
	public Map<String,String> getQueryValues(){
		
		if(_queryValues == null && _query != null){
			
			_queryValues = new HashMap<String,String>(4);
			
			String[] vs = _query.split("&");
			
			for(String vk : vs){
				
				if(vk.length() >0){
					
					String[] v = vk.split("=");
					
					if(v.length > 1){
						_queryValues.put(v[0], decodeQueryValue(v[1]));
					}
					else{
						_queryValues.put(v[0], "");
					}
					
				}
				
			}
			
		}
		
		return _queryValues;
	}
	
	@Override
	public String toString(){
		return _url;
	}
	
	public static String encodeQueryValue(String value){
		
		if(value == null){
			return "";
		}
		
		try {
			return java.net.URLEncoder.encode(value,"utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		
		return value;
	}
	
	public static String decodeQueryValue(String value){
		
		if(value == null){
			return "";
		}
		
		try {
			return java.net.URLDecoder.decode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		
		return value;
	}
	
	public String firstPathComponent( String basePath){
		
		String path = getPath();
		
		if(!basePath.endsWith("/")){
			basePath = basePath + "/";
		}
		
		if(path.startsWith(basePath)){
			path = path.substring(basePath.length());
			int index = path.indexOf("/");
			if(index >=0){
				return path.substring(0,index);
			}
			return path;
		}

		return null;
	}
	
	public static String stringAddPathComponent(String basePath,String pathComponent){
		
		if(basePath != null){
			
			if(pathComponent != null){
				if(basePath.endsWith("/")){
					return basePath + pathComponent;
				}
				else{
					return basePath + "/" + pathComponent;
				}
			}
			
			return basePath;
		}
		return null;
	}
}
