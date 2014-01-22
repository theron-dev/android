package org.hailong.framework.container;

import org.hailong.framework.Edge;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.Rect;
import org.hailong.framework.Size;

public abstract class ContainerListAdapter extends ContainerAdapter {

	public ContainerListAdapter(IServiceContext context) {
		super(context);
		
	}


	private Rect[] _itemRects;
	
	public Rect getItemRect(int position) {
		if(_itemRects != null && position >=0 && position < _itemRects.length){
			return _itemRects[position];
		}
		return null;
	}
	
	public abstract Edge getItemMargin(int position);
	
	public abstract int getItemHeight(int position,int width);


	public Size layout(Rect rect) {

		int c = getCount();
		
		_itemRects = new Rect[c];

		int width = rect.getWidth();
		int height = rect.getHeight();
		int x = rect.getX();
		int y = rect.getY();
		
		int top = 0;

		
		for(int i=0;i<c;i++){
			
			Rect itemRect = new Rect();
			
			Edge margin = getItemMargin(i);

			int marginLeft = margin.getLeft(0, width);
			int marginTop = margin.getTop(0,height);
			int marginRight = margin.getRight(0, width);
			int marginBottom = margin.getBottom(0, height);
			
			int itemWidth = width - marginLeft - marginRight;
			int itemHeight = getItemHeight(i,itemWidth );
			
			itemRect.x = x + marginLeft;
			itemRect.y = y + top + marginTop;
			itemRect.width = itemWidth;
			itemRect.height = itemHeight;
			
			top += marginTop + itemHeight + marginBottom;
			
			_itemRects[i] = itemRect;
		}

		return new Size(width,top);

	}

	

}
