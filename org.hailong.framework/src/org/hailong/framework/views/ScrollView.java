package org.hailong.framework.views;

import org.hailong.framework.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class ScrollView extends ViewGroup {

	private final static float Friction = 0.6f;
	private final static int VelocityUnits = 1000;
	private final static long Duration = 300;
	private final static float Factor = 0.6f;
	
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
	private View _scrollHorizontallyBar;
	private View _scrollVerticallyBar;
	private boolean _allowScrollHorizontallyBar;
	private boolean _allowScrollVerticallyBar = true;
	
	private boolean _dragging; 
	private boolean _decelerating; 
	
	public ScrollView(Context context) {
		super(context);
	}

	public ScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollView(Context context, AttributeSet attrs, int defStyle) {
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
	
	public View getScrollHorizontallyBar(){
		return _scrollHorizontallyBar;
	}
	
	public void setScrollHorizontallyBar(View scrollHorizontallyBar){
		
		if(_scrollHorizontallyBar == null){
			View view = new View(getContext());
			view.setLayoutParams(new LayoutParams(5,5));
			view.setBackgroundResource(R.drawable.scrollbar);
			_scrollHorizontallyBar = view;
		}

		_scrollHorizontallyBar = scrollHorizontallyBar;
	}
	
	public View getScrollVerticallyBar(){
		
		if(_scrollVerticallyBar == null){
			View view = new View(getContext());
			view.setLayoutParams(new LayoutParams(5,5));
			view.setBackgroundResource(R.drawable.scrollbar);
			view.setVisibility(View.GONE);
			_scrollVerticallyBar = view;
		}
		
		return _scrollVerticallyBar;
	}
	
	public void setScrollVerticallyBar(View scrollVerticallyBar){
		_scrollVerticallyBar = scrollVerticallyBar;
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
		
		refreshScrollBar();
		
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
			
			_dragging = true;
			onDraggingStart();
			
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
			
			int width = getMeasuredWidth();
			int height = getMeasuredHeight();
			
			if(_allowScrollVerticallyBar 
					&& (_allowBounceVertically || _contentSizeHeight > height || _contentEdgeTop > 0 || _contentEdgeBottom > 0)){
				
				View v = getScrollVerticallyBar();
				
				if(v != null){
					
					if(v.getParent() == null){
						addView(v);
						
						v.setVisibility(View.VISIBLE);
						v.setAlpha(0.6f);
						
						Animation anim = new Animation();
						
						anim.setDuration(300);
						
						anim.alpha(v, 0.0f, 0.6f);
						
						anim.submit();
					}
					
				}
			}
			
			if(_allowScrollHorizontallyBar
					&& (_allowBounceHorizontally || _contentSizeWidth > width || _contentEdgeLeft > 0 || _contentEdgeRight > 0)){
				
				View v = getScrollHorizontallyBar();
				
				if(v != null){
					
					if(v.getParent() == null){
						
						addView(v);
						
						v.setVisibility(View.VISIBLE);
						v.setAlpha(0.6f);
						
						Animation anim = new Animation();
						
						anim.setDuration(300);
						
						anim.alpha(v, 0.0f, 0.6f);
						
						anim.submit();
					}
					
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
				
				_decelerating = true;
				
				onDeceleratingStart();
				
				Scroller scroller = getScroller();

				scroller.startScroll(scrollX, scrollY, toScrollX - scrollX, toScrollY - scrollY,(int)Duration);
				
				invalidate();
				
				getHandler().postDelayed(new Runnable() {

					public void run() {
						
						_decelerating = false;
						
						onDeceleratingStop();
						
					}}, Duration);
			}

			_dragging = false;
			
			onDraggingStop();
		}
		}
		
		return super.onTouchEvent(event);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int height = b - t;
		int width = r - l;
		
		if(height <=0 || width <=0){
			return;
		}
		
		int scrollX = getScrollX();
		int scrollY = getScrollY();
		
		if(_allowScrollVerticallyBar && _scrollVerticallyBar != null && _scrollVerticallyBar.getParent() == this){
			
			int contentSizeHeight = _contentSizeHeight - height;
			
			if(contentSizeHeight < height){
				contentSizeHeight = height;
			}
			
			int h = (int) ((double) height * (double) height / (double) contentSizeHeight);
			int w = _scrollVerticallyBar.getMeasuredWidth();
			
			int dy = 0;
			
			if(scrollY < 0){
				dy = - scrollY;
			}
			
			if(scrollY > contentSizeHeight){
				dy = scrollY - contentSizeHeight;
			}
			
			h = h - h * dy / contentSizeHeight;
			
			int top = scrollY;
			
			if(top < 0){
				top = 0;
			}
			
			if(top > contentSizeHeight){
				top = contentSizeHeight;
			}
			
			top = scrollY + (int)((double) top * (height - h)  / contentSizeHeight);
		
			_scrollVerticallyBar.layout(width - w - 1, top, width -1, top + h);
			
			
		}
		
		if(_allowScrollHorizontallyBar && _scrollHorizontallyBar != null && _scrollHorizontallyBar.getParent() == this){
			
			int contentSizeWidth = _contentSizeWidth - width;
			
			if(contentSizeWidth < width){
				contentSizeWidth = width;
			}
			
			int w = (int) ((double) width * (double) width / (double) contentSizeWidth);
			int h = _scrollHorizontallyBar.getMeasuredHeight();
			
			int dx = 0;
			
			if(scrollX < 0){
				dx = - scrollX;
			}
			
			if(scrollX > contentSizeWidth ){
				dx = scrollX -  contentSizeWidth;
			}
			
			w = w - w * dx / contentSizeWidth;
			
			int left = scrollX;
			
			if(left < 0){
				left = 0;
			}
			
			if(left > contentSizeWidth){
				left = contentSizeWidth;
			}
			
			left = scrollX + (int)((double) left * (width - w)  / contentSizeWidth);
			
			_scrollHorizontallyBar.layout(left,height - h - 1, left + w, height -1);
			
		}
		
	}
	
	public void refreshScrollBar(){

		int height = getHeight();
		int width = getWidth();
		
		if(height <=0 || width <=0){
			return;
		}
		
		int scrollY = getScrollY();
		int scrollX = getScrollX();
		
		if(_allowScrollVerticallyBar && _scrollVerticallyBar != null && _scrollVerticallyBar.getParent() == this){
			
			int contentSizeHeight = _contentSizeHeight - height;
			
			if(contentSizeHeight < height){
				contentSizeHeight = height;
			}
			
			int top = scrollY;
			
			if(top < 0){
				top = 0;
			}
			
			if(top > contentSizeHeight){
				top = contentSizeHeight;
			}
			
			int h = _scrollVerticallyBar.getHeight();
			
			top = (int)((double) top * (height - h)  / contentSizeHeight);
			
			_scrollVerticallyBar.setY(scrollY + top);
		
			int dy = 0;
			
			if(scrollY < 0){
				dy = - scrollY;
				_scrollVerticallyBar.setPivotY(0.0f);
			}
			
			if(scrollY > contentSizeHeight){
				dy = scrollY - contentSizeHeight;
				_scrollVerticallyBar.setPivotY(h);
			}
			
			_scrollVerticallyBar.setScaleY(1.0f - (float)dy / contentSizeHeight);
			
		}
		
		if(_allowScrollHorizontallyBar && _scrollHorizontallyBar != null && _scrollHorizontallyBar.getParent() == this){
			
			int contentSizeWidth = _contentSizeWidth - width;
			
			if(contentSizeWidth < width){
				contentSizeWidth = width;
			}
			
			int left = scrollX;
			
			if(left < 0){
				left = 0;
			}
			
			if(left > contentSizeWidth){
				left = contentSizeWidth;
			}
			
			int w = _scrollHorizontallyBar.getWidth();
			
			left = (int)((double) left * (width - w)  / contentSizeWidth);
			
			_scrollVerticallyBar.setX(scrollX + left);
			
			int dx = 0;
			
			if(scrollX < 0){
				dx = - scrollX;
				_scrollVerticallyBar.setPivotX(0.0f);
			}
			
			if(scrollX > contentSizeWidth ){
				dx = scrollX -  contentSizeWidth;
				_scrollVerticallyBar.setPivotX(w);
			}

			_scrollVerticallyBar.setScaleX(1.0f - (float)dx / contentSizeWidth);
		}
	}

	private void hiddenScrollBar(){
		
		if(_allowScrollVerticallyBar){
			View v = getScrollVerticallyBar();
			
			if(v != null){
				
				if(v.getParent() != null){
					
					v.setAlpha(0.0f);
					
					Animation anim = new Animation();
				
					anim.setDuration(300);
					
					anim.setListener(new AnimatorListener() {
						
						public void onAnimationStart(Animator arg0) {
							
						}
						
						public void onAnimationRepeat(Animator arg0) {
						
						}
						
						public void onEnd(){
							View v = getScrollVerticallyBar();
							if(v != null && v.getParent() != null){
								removeView(v);
							}
						}
						
						public void onAnimationEnd(Animator arg0) {
							onEnd();
						}
						
						public void onAnimationCancel(Animator arg0) {
							onEnd();
						}
					});
					
					anim.alpha(v, 0.6f, 0.0f);
					
					anim.submit();
				}
				
			}
		}
		
		if(_allowScrollHorizontallyBar){
			
			View v = getScrollHorizontallyBar();
			
			if(v != null){
				
				if(v.getParent() != null){
					
					v.setAlpha(0.0f);
					
					Animation anim = new Animation();
				
					anim.setDuration(300);
					
					anim.setListener(new AnimatorListener() {
						
						public void onAnimationStart(Animator arg0) {
							
						}
						
						public void onAnimationRepeat(Animator arg0) {
						
						}
						
						public void onEnd(){
							View v = getScrollVerticallyBar();
							if(v != null && v.getParent() != null){
								removeView(v);
							}
						}
						
						public void onAnimationEnd(Animator arg0) {
							onEnd();
						}
						
						public void onAnimationCancel(Animator arg0) {
							onEnd();
						}
					});
					
					anim.alpha(v, 0.6f, 0.0f);
					
					anim.submit();
				}
				
			}
		}

	}
	
	public boolean isAllowScrollHorizontallyBar(){
		return _allowScrollHorizontallyBar;
	}
	
	public void setAllowScrollHorizontallyBar(boolean allowScrollHorizontallyBar){
		_allowScrollHorizontallyBar = allowScrollHorizontallyBar;
	}
	
	public boolean isAllowScrollVerticallyBar(){
		return _allowScrollVerticallyBar;
	}
	
	public void setAllowScrollVerticallyBar(boolean allowScrollVerticallyBar){
		_allowScrollVerticallyBar = allowScrollVerticallyBar;
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
		if(! _decelerating){
			hiddenScrollBar();
		}
	}
	
	protected void onDeceleratingStart(){
		
	}
	
	protected void onDeceleratingStop(){
		hiddenScrollBar();
	}
}
