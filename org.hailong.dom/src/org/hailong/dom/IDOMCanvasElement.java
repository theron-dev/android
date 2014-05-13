package org.hailong.dom;

import android.graphics.Canvas;

public interface IDOMCanvasElement extends IDOMLayoutElement{

	public boolean isHidden();
	
	public void draw(Canvas canvas);

	public float getCornerRadius();
	
}
