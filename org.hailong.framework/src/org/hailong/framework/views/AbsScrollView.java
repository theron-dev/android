package org.hailong.framework.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public abstract class AbsScrollView extends ViewGroup {

	private final static float Friction = 0.99f;
	private final static int VelocityUnits = 1000;
	
	/**
	 * 内容宽度
	 */
	private int _contentSizeWidth;
	/**
	 * 内容高度
	 */
	private int _contentSizeHeight;
	/**
	 * 内容偏移X
	 */
	private int _contentOffsetX;
	/**
	 * 内容偏移Y
	 */
	private int _contentOffsetY;
	/**
	 * 内容上偏移
	 */
	private int _contentEdgeTop;
	/**
	 * 内容左偏移
	 */
	private int _contentEdgeLeft;
	/**
	 * 内容下偏移
	 */
	private int _contentEdgeBottom;
	/**
	 * 内容左偏移
	 */
	private int _contentEdgeRight;
	
	private boolean _allowBounceHorizontally = false;
	
	private boolean _allowBounceVertically = true;
	
	private Scroller _scroller;
	
	private float _touchX;
	private float _touchY;
	private int _scrollX;
	private int _scrollY;
	
	private VelocityTracker _tracker;
	
	public AbsScrollView(Context context) {
		super(context);
	}

	public AbsScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AbsScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	      
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
	  
	    measureChildren(widthMeasureSpec, heightMeasureSpec);   
	    
	    setMeasuredDimension(widthSize, heightSize);  
	}  

	public int getContentSizeWidth(){
		return _contentSizeWidth;
	}
	
	public int getContentSizeHeight(){
		return _contentSizeHeight;
	}
	
	public int getContentOffsetX(){
		return _contentOffsetX;
	}
	
	public int getContentOffsetY(){
		return _contentOffsetY;
	}
	
	public boolean isAllowBounceHorizontally(){
		return _allowBounceHorizontally;
	}
	
	public boolean isAllowBounceVertically(){
		return _allowBounceVertically;
	}
	
	public void setAllowBounceHorizontally(boolean allowBounceHorizontally){
		_allowBounceHorizontally = allowBounceHorizontally;
	}
	
	public void setAllowBounceVertically(boolean allowBounceVertically){
		_allowBounceVertically = allowBounceVertically;
	}
	
	public void setContentSize(int width,int height){
		_contentSizeWidth = width;
		_contentSizeHeight = height;
	}
	
	@Override
	public void scrollTo(int x,int y){
		super.scrollTo(x, y);
		_contentOffsetX = x;
		_contentOffsetY = y;
	}
	
	private Scroller getScroller(){
		if(_scroller == null){
			_scroller = new Scroller(getContext(),new DecelerateInterpolator());
			_scroller.setFriction(Friction);
		}
		return _scroller;
	}
	
	public void setContentOffset(int x,int y,boolean animated){
		if(animated){
			Scroller scroller = getScroller();
			int scrollX = getScrollX();
			int scrollY = getScrollY();
			scroller.startScroll(scrollX, scrollY, (int) x - scrollX, (int) y - scrollY);
			invalidate();
		}
		else{
			
			if(x < - _contentEdgeLeft){
				x =  - _contentEdgeLeft;
			}
			
			if(y < - _contentEdgeTop){
				y = - _contentEdgeTop;
			}
			
			scrollTo((int)x,(int)y);
		}
	}
	
	@Override  
    public void computeScroll() {    
    	if(_scroller != null){
    		if(_scroller.computeScrollOffset()){
    			int scrollY = _scroller.getCurrY();
    			int scrollX = _scroller.getCurrX();
    			scrollTo(scrollX,scrollY);
    		}

    	} 
    }
	  
	public void setContentEdge(int left,int top,int right,int bottom){
		_contentEdgeLeft =left;
		_contentEdgeTop = top;
		_contentEdgeRight = right;
		_contentEdgeBottom = bottom;
	}
	
	@SuppressLint("Recycle")
	private VelocityTracker getTracker(){
		
		if(_tracker == null){
			_tracker = VelocityTracker.obtain();
		}
		
		return _tracker;
	}

	@SuppressLint("Recycle")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
		{
			_touchX = event.getX();
			_touchY = event.getY();
			_scrollX = getScrollX();
			_scrollY = getScrollY();
			
			if(_scroller != null){
				_scroller.abortAnimation();
			}
			
			return true;
		}
		case MotionEvent.ACTION_MOVE:
		{
			float dy = event.getY() - _touchY;
			float dx = event.getX() - _touchX;
			
			int scrollY = (int)(_scrollY - dy);
			int scrollX = (int)(_scrollX - dx);
			
			if(! _allowBounceHorizontally){
				
				int maxX = _contentSizeWidth - getMeasuredWidth();
    			
    			if(maxX < 0){
    				maxX = 0;
    			}
    			
				if(scrollX < - _contentEdgeLeft){
					scrollX = - _contentEdgeLeft;
				}
				if(scrollX > maxX + _contentEdgeRight){
					scrollX = maxX + _contentEdgeRight;
				}
			}
			
			if(! _allowBounceVertically){
				
				int maxY = _contentSizeHeight - getMeasuredHeight();
    			
    			if(maxY < 0){
    				maxY = 0;
    			}
    			
				if(scrollY < - _contentEdgeTop){
					scrollY = - _contentEdgeTop;
				}
				if(scrollY > maxY + _contentEdgeBottom){
					scrollY = maxY + _contentEdgeBottom;
				}
			}
			
			scrollTo(scrollX, scrollY);
			
			VelocityTracker tracker = getTracker();
			
			tracker.addMovement(event);
			
			return true;
		}
		default:
		{
			int scrollY = getScrollY();
			int scrollX = getScrollX();
			int toScrollX = scrollX;
			int toScrollY = scrollY;
			
			VelocityTracker tracker = getTracker();
			
			tracker.computeCurrentVelocity(VelocityUnits);
			
			int velocityX = (int) tracker.getXVelocity();
			int velocityY = (int) tracker.getYVelocity();
			
			int maxX = _contentSizeWidth - getMeasuredWidth();
			
			if(maxX < 0){
				maxX = 0;
			}
			
			int maxY = _contentSizeHeight - getMeasuredHeight();
			
			if(maxY < 0){
				maxY = 0;
			}
			
			toScrollX = scrollX - velocityX;
			toScrollY = scrollY - velocityY;
			
			if(toScrollX < - _contentEdgeLeft){
				toScrollX = - _contentEdgeLeft;
			}
			
			if(toScrollX > maxX + _contentEdgeRight){
				toScrollX = maxX + _contentEdgeRight ;
			}
			
			if(toScrollY < - _contentEdgeTop){
				toScrollY = - _contentEdgeTop;
			}
			
			if(toScrollY > maxY + _contentEdgeBottom){
				toScrollY = maxY + _contentEdgeBottom;
			}
		
			if(scrollX != toScrollX || scrollY != toScrollY){
				
				Scroller scroller = getScroller();
				
				scroller.startScroll(scrollX, scrollY, toScrollX - scrollX, toScrollY - scrollY);
				
				invalidate();
			}
			  
		}
		}
		
		return super.onTouchEvent(event);
	}
}
