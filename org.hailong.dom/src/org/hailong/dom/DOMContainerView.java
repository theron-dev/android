package org.hailong.dom;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.Edge;
import org.hailong.core.Rect;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class DOMContainerView extends ViewGroup{

	private List<Rect> _rects;
	private DataSetObserver _dataSetObserver;
	private Adapter _adapter;
	private int _contentWidth;
	private int _contentHeight;
	private SparseArray<View> _itemViews;
	private Edge _padding;
	
	
	public DOMContainerView(Context context) {
		super(context);
	}
	
	public void setAdapter(Adapter adapter){
		
		if(_adapter != null && _dataSetObserver != null){
			_adapter.unregisterDataSetObserver(_dataSetObserver);
		}
		
		_adapter = adapter;
		
		if(_adapter != null ){
			
			if(_dataSetObserver == null){
				
				_dataSetObserver = new DataSetObserver() {
					public void onChanged() {
						reloadData(true);
					}
				};
			}
			
			_adapter.registerDataSetObserver(_dataSetObserver);
		
		}
		
		reloadData(true);
	}
	
	public Edge getPadding(){
		return _padding;
	}
	
	public void setPadding(Edge padding){
		_padding = padding;
	}
	
	public Adapter getAdapter(){
		return _adapter;
	}
	
	public void reloadData(boolean reloadData){
		
		if(reloadData){
			
			float width = 0,height = 0;
			
			if(_rects == null){
				_rects = new ArrayList<Rect>(4);
			}
			else {
				_rects.clear();
			}
			
			int count = _adapter == null ? 0 : _adapter.getCount();
			
			for(int i=0;i<count;i++){
				Object object = _adapter.getItem(i);
				Rect rect = _adapter.getItemRect(i, object);
				_rects.add(rect);
				
				float right = rect.getX() + rect.getWidth();
				float bottom = rect.getY() + rect.getHeight(); 
				
				if(width < right){
					width = right;
				}
				
				if(height < bottom){
					height = bottom;
				}
				
			}
			
			float paddingBottom = _padding == null? 0: _padding.getBottom();
			float paddingRight = _padding == null? 0: _padding.getRight();
			
			_contentWidth = (int) (width + paddingRight + 0.999999f);
			_contentHeight = (int) (height + paddingBottom + 0.999999f);
			
		}
	
		if(_rects != null){
			
			int index = 0;
			
			for(Rect rect : _rects){
				
				View itemView = _itemViews == null ? null : _itemViews.get(index);
				
				Object object = _adapter.getItem(index);
				
				if(_adapter.isVisable(rect)){
					
					if(itemView != null && ! _adapter.isViewFromObject(itemView, index, object)){
						_adapter.destroyItem(this, index, object);
						removeView(itemView);
						if(_itemViews != null){
							_itemViews.remove(index);
						}
						itemView = null;
					}
					
					if(itemView == null){
						itemView = _adapter.instantiateItemView(this, index, object);

						if(_itemViews == null){
							_itemViews = new SparseArray<View>(4);
 						}
						_itemViews.put(index, itemView);
					}
					
					itemView.setLayoutParams(new LayoutParams(rect));
					
					if(itemView.getParent() == null){
						addView(itemView);
					}

				}
				else if(itemView != null){
					_adapter.destroyItem(this, index, object);
					removeView(itemView);
					if(_itemViews != null){
						_itemViews.remove(index);
					}
				}
				
				index ++;
			}
			
		}
	}
	
//	protected boolean isVisable(Rect rect){
//		int left = _contentOffsetX;
//		int top = _contentOffsetY;
//		int right = left + (_size == null ? getMeasuredWidth() : (int) _size.getWidth());
//		int bottom = top + (_size == null ? getMeasuredHeight() : (int) _size.getHeight());
//
//		left = Math.max(left,(int) (rect.getX() + 0.999999f));
//		top = Math.max(top,(int) (rect.getY() + 0.999999f));
//		right = Math.min(right,(int) (rect.getX() + rect.getWidth() + 0.999999f));
//		bottom = Math.min(bottom,(int) (rect.getY() + rect.getHeight() + 0.999999f));
//		
//		return right - left > 0 && bottom - top > 0;
//	}
	
	public static abstract class Adapter {

		private List<DataSetObserver> _dataSetObservers;
		
		public abstract int getCount();
		public abstract Object getItem(int position);
		public abstract Rect getItemRect(int position,Object object);
		public abstract boolean isViewFromObject(View view, int position, Object object);
        public abstract void destroyItem(ViewGroup container, int position,Object object);
        public abstract View instantiateItemView(ViewGroup container, int position,Object object);
        public abstract boolean isVisable(Rect rect);
        
		public void registerDataSetObserver(DataSetObserver dataSetObserver) {
			if(_dataSetObservers == null){
				_dataSetObservers = new ArrayList<DataSetObserver>(4);
			}
			_dataSetObservers.add(dataSetObserver);
		}

		public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
			if(_dataSetObservers != null){
				_dataSetObservers.remove(dataSetObserver);
			}
		}
		
		public void notifyDataSetChanged(){
			if(_dataSetObservers != null){
				for(DataSetObserver dataSetObserver : _dataSetObservers.toArray(new DataSetObserver[_dataSetObservers.size()])){
					dataSetObserver.onChanged();
				}
			}
		}
	}
	
	public static class LayoutParams extends ViewGroup.LayoutParams{

		private final Rect _frame;
	
		public LayoutParams(Rect frame) {
			super((int) frame.getWidth(), (int) frame.getHeight());
			_frame = frame;
		}
		
		public int getLeft(){
			return (int) _frame.getX();
		}
		
		public int getTop(){
			return (int) _frame.getY();
		}
		
		public int getRight(){
			return (int)( _frame.getX() + _frame.getWidth());
		}
		
		public int getBottom(){
			return (int)( _frame.getY() + _frame.getHeight());
		}
	}
	
	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	      
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	    measureChildren(widthMeasureSpec, heightMeasureSpec);   

	    setMeasuredDimension(_contentWidth,_contentHeight);

	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r,
			int b) {
		
		int c = getChildCount();
		
		for(int i=0;i<c;i++){
			View v = getChildAt(i);
			ViewGroup.LayoutParams params = v.getLayoutParams();
			if(params != null && params instanceof DOMContainerView.LayoutParams){
				DOMContainerView.LayoutParams p = (DOMContainerView.LayoutParams) params;
				v.layout(p.getLeft(), p.getTop(), p.getRight(), p.getBottom());
			}
		}
		
	}
	
}
