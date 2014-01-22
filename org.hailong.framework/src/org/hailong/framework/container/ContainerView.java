package org.hailong.framework.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hailong.framework.R;
import org.hailong.framework.Rect;
import org.hailong.framework.Size;
import org.hailong.framework.views.ScrollView;
import org.hailong.framework.views.LoadingView;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class ContainerView extends ScrollView implements IContainerView{
	
	private DataSetObserver _dataSetObserver;
	private IContainerAdapter _adapter;
	private LoadingView _topLoadingView;
	private LoadingView _bottomLoadingView;
	private Container _container;
	private Set<View> _dequeueItemViews;
	
	public ContainerView(Context context) {
		super(context);
		
	}
	
	public ContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	public void setAdapter(IContainerAdapter adpater){
		if(_dataSetObserver == null){
			_dataSetObserver = new DataSetObserver(){
				
				@Override
				public void onChanged(){
					reloadData(true);
				}
			};
		}

		if(_adapter != null){
			_adapter.unregisterDataSetObserver(_dataSetObserver);
		}
		
		_adapter = adpater;
		
		if(_adapter != null){
			_adapter.registerDataSetObserver(_dataSetObserver);
		}
	}
	
	public IContainerAdapter getAdapter(){
		return _adapter;
	}
	
	public LoadingView getTopLoadingView(){
		return _topLoadingView;
	}
	
	public void setTopLoadingView(LoadingView loadingView){
		if(_topLoadingView != loadingView){
			if(_topLoadingView != null){
				ViewParent v = _topLoadingView.getParent();
				if(v != null && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_topLoadingView);
				}
			}
			_topLoadingView = loadingView;
			if(_topLoadingView != null){
				ViewParent v = _topLoadingView.getParent();
				if(v != null && v != this && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_topLoadingView);
				}
				else if(v == null || v != this){
					addView(_topLoadingView, 0);
				}
			}
		}
	}
	
	public LoadingView getBottomLoadingView(){
		return _bottomLoadingView;
	}
	
	public void setBottomLoadingView(LoadingView loadingView){
		
		if(_bottomLoadingView != loadingView){
		
			if(_bottomLoadingView != null){
				ViewParent v = _bottomLoadingView.getParent();
				if(v != null && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_bottomLoadingView);
				}
			}
			
			_bottomLoadingView = loadingView;
			
			if(_bottomLoadingView != null){
				ViewParent v = _bottomLoadingView.getParent();
				if(v != null && v != this && v instanceof ViewGroup){
					((ViewGroup) v).removeView(_bottomLoadingView);
				}
				else if(v == null || v != this){
					addView(_bottomLoadingView,0);
				}
			}
		}
	
	}

	public Container getContainer(){
		
		if(_container == null){
			_container = new Container(this);
		}
		
		return _container;
	}
	
	public void reloadData(){
		reloadData(true);
	}
	
	@Override
	public void scrollTo(int x,int y){
		super.scrollTo(x, y);
		
		reloadData(false);
		
		if(y < 0 && _topLoadingView != null){
			_topLoadingView.setAnimationValue( - (double) y / _topLoadingView.getHeight());
		}
		
		if(_bottomLoadingView != null){
			int height = getHeight();
			int h = getContentSizeHeight() - height;
			if(h < 0){
				h = 0;
			}
			if(y > h){
				_bottomLoadingView.setAnimationValue((double)(y - h) / _bottomLoadingView.getHeight());
			}
		}
	}
	
	public boolean isVisableRect(Rect rect){
		
		int width = getWidth();
		int height = getHeight();
		int x = getContentOffsetX();
		int y = getContentOffsetY();
		
		int left = rect.getX() - x;
		int top = rect.getY() - y;
		int right = left + rect.getWidth();
		int bottom = top + rect.getHeight();
		
		left = Math.max(left, 0);
		top = Math.max(top, 0);
		right = Math.min(right, width);
		bottom = Math.min(bottom, height);
		
	    return right > left && bottom > top;
	}
	
	protected void reloadData(boolean reloadData){
	
		if(_adapter != null){
			
			if(reloadData){
				
				Size size = _adapter.layout(new Rect(0,0,getWidth(),getHeight()));
			
				setContentSize(0, size.getHeight());
				
			}
			
			Map<Number,View> itemViews = new HashMap<Number,View>(4);
			
			int c = getChildCount();
			
			for(int i=0;i<c;i++){
				
				View v = getChildAt(i);
				
				Object index = v.getTag(R.id.index);
				
				if(index != null && index instanceof Number){
					
					if(reloadData){
						
						if(_dequeueItemViews == null){
							_dequeueItemViews = new HashSet<View>(4);
						}
						
						v.setTag(R.id.index, -1);
						
						_dequeueItemViews.add(v);
						
					}
					else{
						itemViews.put((Number) index, v);
					}
				}
				
			}
			
			c = _adapter.getCount();
			
			for(int i=0;i<c;i++){
				
				Rect rect = _adapter.getItemRect(i);
				
				Integer index = Integer.valueOf(i);
				
				if(isVisableRect(rect)){
				
					View itemView = itemViews.get(index);
					
					if(itemView == null){
						
						String reuseIdentifier = _adapter.getReuseIdentifier(i);
						
						if(_dequeueItemViews != null){
							for(View v : _dequeueItemViews){
								String identifier = (String) v.getTag(R.id.reuseIdentifier);
								if(reuseIdentifier == identifier || (reuseIdentifier != null && reuseIdentifier.equals(identifier))){
									itemView = v;
									break;
								}
							}
						}
						
						itemView = _adapter.getView(i, itemView, null);
						itemView.setTag(R.id.index, index);
						itemView.setTag(R.id.reuseIdentifier, reuseIdentifier);
						
						if(_dequeueItemViews != null){
							_dequeueItemViews.remove(itemView);
						}
						
						addView(itemView,0);

					}
					else{
						itemViews.remove(index);
					}
		
					itemView.setLeft(rect.getX());
					itemView.setTop(rect.getY());
					itemView.setRight(rect.getX() + rect.getWidth());
					itemView.setBottom(rect.getY() + rect.getHeight());
				}
				else{
					View itemView = itemViews.get(index);
					if(itemView != null){
						removeView(itemView);
						itemView.setTag(R.id.index, -1);
						itemViews.remove(index);
						if(_dequeueItemViews == null){
							_dequeueItemViews = new HashSet<View>(4);
						}
						_dequeueItemViews.add(itemView);
					}
				}
			}
			
		}
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	
		super.onLayout(changed, l, t, r, b);
		
		int width = r - l;
		int height = b - t;
		
		if(_topLoadingView != null){
			int h =  _topLoadingView.getMeasuredHeight();
			int top = t  - h;
			_topLoadingView.layout(0, top, width, top + h);
		}
		
		if(_bottomLoadingView != null){
			int h =  _bottomLoadingView.getMeasuredHeight();
			int top = getContentSizeHeight();
			if(top < height){
				_bottomLoadingView.layout(0, 0, 0, 0);
			}
			else{
				_bottomLoadingView.layout(0, top, width, top + h);
			}
		}
		
		int c = getChildCount();
		
		for(int i=0;i<c;i++){
			View v = getChildAt(i);
			Object index = v.getTag(R.id.index);
			if(index != null && index instanceof Number){
				v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
			}
		}
	
		
	}
	
}
