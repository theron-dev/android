package org.hailong.framework.container;

import org.hailong.framework.Edge;
import org.hailong.framework.Rect;
import org.hailong.framework.Size;

public abstract class ContainerColumnAdapter extends ContainerAdapter {

	private Rect[] _itemRects;
	private Object[] _columnWidths;
	
	public ContainerColumnAdapter(){
		this("100%");
	}
	
	public ContainerColumnAdapter(Object ... columnWidths){
		if(columnWidths != null && columnWidths.length > 0){
			_columnWidths = columnWidths;
		}
		else{
			_columnWidths = new Object[] {"100%"};
		}
	}
	
	public Object[] getColumnWidths(){
		return _columnWidths;
	}
	
	public void setColumnWidths(Object ... columnWidths){
		if(columnWidths != null && columnWidths.length > 0){
			_columnWidths = columnWidths;
		}
		else{
			_columnWidths = new Object[] {"100%"};
		}
	}
	
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
		int columnCount = _columnWidths.length;
		
		_itemRects = new Rect[c];

		int width = rect.getWidth();
		int height = rect.getHeight();
		int x = rect.getX();
		int y = rect.getY();
		
		int[] columnWidths = new int[columnCount];
		int[] tops = new int[columnCount];
		
		
		for(int i=0;i<columnCount;i++){
			tops[i] = 0;
			columnWidths[i] = Rect.getValue(_columnWidths[i], 0,width);
		}
		
		for(int i=0;i<c;i++){
			
			Rect itemRect =  new Rect();
			
			int columnIndex = 0;
			int top = tops[columnIndex];
			int columnWidth = columnWidths[columnIndex];
			int left = 0;
			
			for(int ii=0;ii<columnCount;ii++){
				
				if(tops[ii] < top){
					columnIndex = ii;
					top = tops[ii];
					columnWidth = columnWidths[ii];
				}
				
			}
			
			for(int ii=0;ii<columnIndex;ii++){
				left += columnWidths[ii];
			}
			
			Edge margin = getItemMargin(i);
			
			int marginLeft = margin.getLeft(0, width);
			int marginTop = margin.getTop(0,height);
			int marginRight = margin.getRight(0, width);
			int marginBottom = margin.getBottom(0, height);
			
			int itemHeight = getItemHeight(i, columnWidth - marginLeft - marginRight);
			
			itemRect.x = x + left + marginLeft;
			itemRect.y = y + top + marginTop;
			itemRect.width = columnWidth - marginLeft - marginRight;
			itemRect.height = itemHeight;
	
			tops[columnIndex] = top + marginTop + itemHeight + marginBottom;
			
			_itemRects[i] = itemRect;
			
		}
		
		int top = tops[0];

		for(int i=0;i<columnCount;i++){
			
			if(tops[i] > top){
				top = tops[i];
			}
			
		}
		
		return new Size (width,top);
	}

	
}
