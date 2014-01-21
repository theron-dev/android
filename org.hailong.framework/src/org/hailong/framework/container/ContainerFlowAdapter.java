package org.hailong.framework.container;

import org.hailong.framework.Edge;
import org.hailong.framework.Rect;
import org.hailong.framework.Size;

public abstract class ContainerFlowAdapter extends ContainerAdapter {

	private Rect[] _itemRects;
	
	public Rect getItemRect(int position) {
		if(_itemRects != null && position >=0 && position < _itemRects.length){
			return _itemRects[position];
		}
		return null;
	}
	
	public abstract Edge getItemMargin(int position);
	
	public abstract Size getItemSize(int position);

	public Size layout(Rect rect) {
		
		int c = getCount();
		
		_itemRects = new Rect[c];

		int width = rect.getWidth();
		int height = rect.getHeight();
		int x = rect.getX();
		int y = rect.getY();
		
		int curX = 0;
		int curY = 0;
		int lineHeight = 0;
		int maxWidth = 0;
		
		for(int i=0;i<c;i++){
			
			Rect itemRect = new Rect();
			
			Edge margin = getItemMargin(i);
			Size size = getItemSize(i);
			
			int marginLeft = margin.getLeft(0, width);
			int marginTop = margin.getTop(0,height);
			int marginRight = margin.getRight(0, width);
			int marginBottom = margin.getBottom(0, height);
			
			int itemWidth = size.getWidth(0, width);
			int itemHeight = size.getHeight(0, height);
			
			if(curX + marginLeft + itemWidth + marginRight > width){
				curX = 0;
				curY += lineHeight;
				lineHeight = 0;
			}
			
			itemRect.x = curX + x + marginLeft;
			itemRect.y = curY + y + marginTop;
			itemRect.width = itemWidth;
			itemRect.height = itemHeight;
			
			curX += marginLeft + itemWidth + marginRight;
			
			if(curX > maxWidth){
				maxWidth = curX;
			}
			
			if(lineHeight < marginTop + itemHeight + marginBottom){
				lineHeight = marginTop + itemHeight + marginBottom;
			}
			
			_itemRects[i] = itemRect;
		}

		return new Size(maxWidth,curY + lineHeight);
	}

}
