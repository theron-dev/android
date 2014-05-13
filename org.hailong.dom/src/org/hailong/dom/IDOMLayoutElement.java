package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;

public interface IDOMLayoutElement {

	public Rect getFrame();
	
	public void setFrame(Rect frame);

	public Size getContentSize();
	
	public void setContentSize(Size contentSize);
	
	public Edge getPadding();
	
	public void setPadding(Edge padding);
	
	public Edge getMargin();
	
	public void setMargin(Edge margin);
	
	public Size layoutChildren(Edge padding);
	
	public Size layout(Size size);
	
	public Size layout();
}
