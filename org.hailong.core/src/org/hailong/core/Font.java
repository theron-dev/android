package org.hailong.core;

public final class Font {

	public final String fontName;
	public final String fontStyle;
	public final float fontSize;
	
	public Font(){
		this.fontSize = 14;
		this.fontStyle = null;
		this.fontName = null;
	}
	
	public Font(float fontSize){
		this.fontSize = fontSize;
		this.fontStyle = null;
		this.fontName = null;
	}
	
	public Font(float fontSize,String fontStyle){
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontName = null;
	}
	
	public Font(float fontSize,String fontStyle,String fontName){
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontName = fontName;
	}
	
	public boolean isFontStyleBold(){
		return fontStyle != null && "bold".equals(fontStyle);
	}
	
	public static Font valueOf(String value){
		
		if(value == null){
			return new Font();
		}
		
		String vs[] = value.split(" ");
		
		if(vs.length >= 3){
			return new Font(Float.valueOf(vs[1]),vs[2],vs[0]);
		}
		else if(vs.length >= 2){
			return new Font(Float.valueOf(vs[0]),vs[1]);
		}
		else if(vs.length >= 1){
			return new Font(Float.valueOf(vs[0]));
		}
		
		return new Font();
	}
}
