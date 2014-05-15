package org.hailong.dom;

import org.hailong.core.Edge;
import org.hailong.core.Rect;
import org.hailong.core.Size;

public class DOMVScrollElement extends DOMContainerElement {
	
	public Size layoutChildren(Edge padding){
		
		Rect frame = getFrame();
		
		Size contentSize = new Size(frame.getWidth(),0);

		float width = frame.getWidth() - padding.getLeft() - padding.getRight();
		
		for(DOMElement element : getChilds()){
			
			if(element instanceof IDOMLayoutElement){
				
				IDOMLayoutElement layoutElement = (IDOMLayoutElement) element;
				
				Edge margin = layoutElement.getMargin();
				
				layoutElement.layout(new Size(width - margin.getLeft() - margin.getRight(),frame.getHeight()));
				
				Rect r = layoutElement.getFrame();
				
				r.x = padding.getLeft() + margin.getLeft();
				r.y = padding.getTop() + margin.getTop() + contentSize.getHeight();
				r.width = width - margin.getLeft() - margin.getRight();
				
				contentSize.height = contentSize.getHeight() + r.getHeight() + margin.getTop() + margin.getBottom();
				
			}
			
		}

		contentSize.height = contentSize.getHeight() + padding.getTop() + padding.getBottom();
		
		if(contentSize.getHeight() < frame.getHeight()){
			contentSize.height = frame.getHeight();
		}
		
		if(isViewLoaded()){
			
			float displayScale = getDocument().getBundle().displayScale();
			
			DOMContainerView contentView = getContentView();
			
			contentView.setContentSize((int) (contentSize.getWidth() * displayScale), (int) (contentSize.getHeight() * displayScale));
			
			reloadData(true);
		}
		
		return contentSize;
	}
	
}
