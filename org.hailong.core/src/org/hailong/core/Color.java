package org.hailong.core;

public class Color {

	public float r;
	public float g;
	public float b;
	public float a;
	
	public Color(){
		this.r = this.g = this.b = this.a = 0.0f;
	}
	
	public Color(int color){
		r = (float)  (color >> 16) / 255.0f;
		g = (float)  ((color & 0x00ffff) >> 8) / 255.0f;
		b = (float)  (color & 0x00ff) / 255.0f;
		a = 1.0f;
	}
	
	public Color(float r,float g ,float b,float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public int intValue(){
		int rr = (int) ( r * 0xff);
		int gg = (int) ( g * 0xff);
		int bb = (int) ( b * 0xff);
		return (rr <<16) | (gg << 8) | bb;
	}
	
	public int getAlpha(){
		return (int) (a * 0xff);
	}
	
	public static Color valueOf(String string){
		
		if(string.startsWith("#") && string.length() >= 7){
			Color c = new Color(0);
			c.r = (float) Integer.valueOf(string.substring(1, 3), 16) / 255.0f;
			c.g = (float) Integer.valueOf(string.substring(3, 5), 16) / 255.0f;
			c.b = (float) Integer.valueOf(string.substring(5, 7), 16) / 255.0f;
			if(string.length() > 7){
				String[] vs = string.split(" ");
				if(vs.length >1){
					c.a = Float.valueOf(vs[1]);
				}
			}
			return c;
		}
		
		return new Color();
	}
}
