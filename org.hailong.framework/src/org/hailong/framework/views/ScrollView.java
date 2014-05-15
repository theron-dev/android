package org.hailong.framework.views;

import org.hailong.framework.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class ScrollView extends ViewGroup {

	private final static float Friction = 0.99f;
	private final static int VelocityUnits = 1000;
	private final static int Duration = 800;
	private final static int FastDuration = 300;
	private final static float Factor = 0.99f;
	
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
	
	private int _touchId;
	private float _touchX;
	private float _touchY;
	private int _scrollX;
	private int _scrollY;
	
	private VelocityTracker _tracker;
	
	private boolean _dragging; 
	private boolean _decelerating; 
	
	private int _maximumVelocity;
	private int _minimumVelocity;
	
	public ScrollView(Context context) {
		super(context);
		_ScrollView(context);
	}

	public ScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_ScrollView(context);
	}

	public ScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_ScrollView(context);
	}
	
	protected void _ScrollView(Context context){
		
		ViewConfiguration configuration = ViewConfiguration.get(context);
		_maximumVelocity = configuration.getScaledMaximumFlingVelocity();
		_minimumVelocity = configuration.getScaledMinimumFlingVelocity();
		
		setScrollbarFadingEnabled(true);
		setScrollContainer(true);
		setHorizontalScrollBarEnabled(true);
		setVerticalScrollBarEnabled(true);

		TypedArray a = context.obtainStyledAttributes(R.styleable.View);
		initializeScrollbars(a);
		a.recycle();
	}
	
	@Override
	protected int computeHorizontalScrollOffset(){
		
		int x = _contentOffsetX;
		
		if(x < 0){
			x = 0;
		}

		return x;
	}
	
	@Override
	protected int computeHorizontalScrollRange(){
		
		int width = getWidth();
		int contentWidth = _contentSizeWidth;
	
		if(contentWidth > width){
			width = contentWidth - width;
			if(_contentOffsetX < 0){
				return contentWidth - _contentOffsetX;
			}
			else if(_contentOffsetX > width){
				return contentWidth + (_contentOffsetX - width);
			}
			return contentWidth;
		}
		
		return 0;
	}
	
	@Override
	protected int computeVerticalScrollOffset(){
		
		int y = _contentOffsetY;
		
		if(y < 0){
			y = 0;
		}

		return y;
	}
	
	@Override
	protected int computeVerticalScrollRange(){
		
		int height = getHeight();
		int contentHeight = _contentSizeHeight;
	
		if(contentHeight > height){
			height = contentHeight - height;
			if(_contentOffsetY < 0){
				return contentHeight - _contentOffsetY;
			}
			else if(_contentOffsetY > height){
				return contentHeight + (_contentOffsetY - height);
			}
			return contentHeight;
		}
		
		return 0;
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
			_scroller = new Scroller(getContext(),new DecelerateInterpolator(Factor));
			_scroller.setFriction(Friction);
		}
		return _scroller;
	}
	
	public void setContentOffset(int x,int y,boolean animated){
		if(animated){
			
			if(_decelerating){
				_decelerating = false;
				onDeceleratingStop();
			}
			
			Scroller scroller = getScroller();
			
			if(!scroller.isFinished()){
				scroller.abortAnimation();
			}
			
			int scrollX = getScrollX();
			int scrollY = getScrollY();
			scroller.startScroll(scrollX, scrollY, (int) x - scrollX, (int) y - scrollY,Duration);
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
    			invalidate();
    		}
    	} 
    }
	  
	public void setContentEdge(int left,int top,int right,int bottom){
		_contentEdgeLeft =left;
		_contentEdgeTop = top;
		_contentEdgeRight = right;
		_contentEdgeBottom = bottom;
	}
	
	private VelocityTracker getTracker(){
		
		if(_tracker == null){
			_tracker = VelocityTracker.obtain();
		}

		return _tracker;
	}
	
	@Override
	protected void finalize() throws Throwable{
		
		if(_tracker != null){
			_tracker.recycle();
			_tracker = null;
		}

		super.finalize();
	}

	protected boolean scrollTouchEvent(MotionEvent event){
		

		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
		{
			if(_touchId == 0){
				
				_touchId = event.getPointerId(0);
				_touchX = event.getX(0);
				_touchY = event.getY(0);
				
				_scrollX = getScrollX();
				_scrollY = getScrollY();
				
				if(_scroller != null){
					_scroller.abortAnimation();
				}
				
				if(_decelerating){
					_decelerating = false;
					onDeceleratingStop();
				}
				
				VelocityTracker tracker = getTracker();
				
				tracker.clear();
				
				tracker.addMovement(event);
			}
			
		}
			break;
		case MotionEvent.ACTION_MOVE:
		{
			int touchIndex = -1;
			int c = event.getPointerCount();
			
			for(int i=0;i<c;i++){
				if(event.getPointerId(i) == _touchId){
					touchIndex = i;
					break;
				}
			}
			
			if(touchIndex >=0 ){
				
				VelocityTracker tracker = getTracker();
				
				tracker.addMovement(event);
				
				float dy = event.getY(touchIndex) - _touchY;
				float dx = event.getX(touchIndex) - _touchX;
				
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
				
				if(!_dragging){
					_dragging = true;
					onDraggingStart();
				}
				
				scrollTo(scrollX, scrollY);
			}
		}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		{
			int touchIndex = -1;
			int c = event.getPointerCount();
			
			for(int i=0;i<c;i++){
				if(event.getPointerId(i) == _touchId){
					touchIndex = i;
					break;
				}
			}
			
			if(touchIndex >=0 ){
			
				int scrollY = getScrollY();
				int scrollX = getScrollX();
				int toScrollX = scrollX;
				int toScrollY = scrollY;
				
				VelocityTracker tracker = getTracker();
				
				tracker.addMovement(event);
				
				tracker.computeCurrentVelocity(VelocityUnits,_maximumVelocity);
				
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
				
				velocityX = toScrollX - scrollX;
				velocityY = toScrollY - scrollY;
			
				if(Math.abs(velocityY) > _minimumVelocity || Math.abs(velocityX) >_minimumVelocity){
					
					_decelerating = true;
					
					onDeceleratingStart();
					
					Scroller scroller = getScroller();
	
					int duration = Duration;
					
					int distanceX = getHeight() / 2;
					int distanceY = getWidth() / 2;
					
					if(Math.abs(velocityX) < distanceX && Math.abs(velocityY) < distanceY){
						duration = FastDuration;
					}
					
					scroller.startScroll(scrollX, scrollY, velocityX, velocityY,duration);
					
					invalidate();
					
					getHandler().postDelayed(new Runnable() {
	
						public void run() {
							
							if(_decelerating){
								
								Scroller scroller = getScroller();
								
								if(scroller.isFinished()){
									_decelerating = false;
									onDeceleratingStop();
								}
								else if(getHandler() != null){
									getHandler().postDelayed(this,Duration);
								}
								else{
									_decelerating = false;
									onDeceleratingStop();
								}
								
							}
							
						}}, duration);
				}
				else {
					toScrollX = scrollX;
					toScrollY = scrollY;
					
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
					if(toScrollY != scrollY || toScrollX != scrollX){
						
						_decelerating = true;
						
						onDeceleratingStart();
						
						Scroller scroller = getScroller();
						
						scroller.startScroll(scrollX, scrollY, velocityX, velocityY,FastDuration);
						
						invalidate();
						
						getHandler().postDelayed(new Runnable() {
	
							public void run() {
								
								if(_decelerating){
									
									Scroller scroller = getScroller();
									
									if(scroller.isFinished()){
										_decelerating = false;
										onDeceleratingStop();
									}
									else if(getHandler() != null){
										getHandler().postDelayed(this,FastDuration);
									}
									else{
										_decelerating = false;
										onDeceleratingStop();
									}
								}
								
							}}, FastDuration);
					}
				}
	
				_dragging = false;
				
				onDraggingStop();
				
				_touchId = 0;
			}
		}
		}
		
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event){

		scrollTouchEvent(event);

		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
	}
	
	public boolean isDragging(){
		return _dragging;
	}
	
	public boolean isDecelerating(){
		return _decelerating;
	}
	
	protected void onDraggingStart(){
		
	}
	
	protected void onDraggingStop(){
		
	}
	
	protected void onDeceleratingStart(){
		
	}
	
	protected void onDeceleratingStop(){
		
	}
}
