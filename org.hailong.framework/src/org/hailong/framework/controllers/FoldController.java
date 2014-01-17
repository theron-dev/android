package org.hailong.framework.controllers;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.R;
import org.hailong.framework.URL;
import org.hailong.framework.value.Value;
import org.hailong.framework.views.Animation;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class FoldController<T extends IServiceContext> extends ViewController<T> implements OnGestureListener ,View.OnTouchListener {

	private IViewController<T> _leftViewController;
	private IViewController<T> _rightViewController;
	private IViewController<T> _centerViewController;
	private ViewGroup _contentView;
	private GestureDetector _gestureDetector;
	private float _gestureTouchX = 0;
	private int _gestureDirection = GestureDirectionNone;
	private int _topControllerDirection = TopControllerDirectionCenter;
	
	private final static int GestureDirectionNone = 0;
	private final static int GestureDirectionLeft = 1;
	private final static int GestureDirectionRight = 2;
	private final static float AlphaValue = 0.4f;
	
	private final static int TopControllerDirectionCenter = 0;
	private final static int TopControllerDirectionLeft = 1;
	private final static int TopControllerDirectionRight = 2;
	private final static float FoldWidth = 45;
	
	public FoldController(IViewControllerContext<T> activity) {
		super(activity, null);
	}

	public FoldController(IViewControllerContext<T> activity, String viewLayout) {
		super(activity, viewLayout);
	}
	
	public IViewController<T> getLeftViewController(){
		return _leftViewController;
	}
	
	public void setLeftViewController(IViewController<T> leftViewController){
		_leftViewController = leftViewController;
	}
	
	public IViewController<T> getRightViewController(){
		return _rightViewController;
	}
	
	public void setRightViewController(IViewController<T> rightViewController){
		_rightViewController = rightViewController;
	}
	
	public IViewController<T> getCenterViewController(){
		return _centerViewController;
	}
	
	public void setCenterViewController(IViewController<T> centerViewController){
		_centerViewController = centerViewController;
	}
	
	public IViewController<T> getTopViewController(){
		switch(_topControllerDirection){
		case TopControllerDirectionCenter:
			return getCenterViewController();
		case TopControllerDirectionLeft:
			return getLeftViewController();
		case TopControllerDirectionRight:
			return getRightViewController();
		}
		return null;
	}
	
	public IViewController<T> getTopController() {
		IViewController<T> controller = super.getTopController();
		if(controller == this){
			controller = getTopViewController();
			if(controller != null){
				return controller.getTopController();
			}
			controller = getCenterViewController();
			if(controller != null){
				return controller.getTopController();
			}
			return this;
		}
		return controller;
	}
	
	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		_contentView = (ViewGroup) getView().findViewById(R.id.contentView);
		
		if(_contentView == null){
			_contentView = (ViewGroup)getView();
		}
		
		if(_gestureDetector == null){
			_gestureDetector = new GestureDetector(getContext(), this, getHandler());
		}
		
		getView().setOnTouchListener(this);
		
	}
	
	@Override
	protected void didViewUnLoaded(){
		super.didViewUnLoaded();
		
		_contentView = null;
	}
	
	@Override
	public void viewWillAppear(boolean animated){
		super.viewWillAppear(animated);
		
		IViewController<T> topViewController = null;
		
		if(_topControllerDirection == TopControllerDirectionLeft){
			
			topViewController = getLeftViewController();
		
			if(topViewController != null && !topViewController.isViewAppeared()){
				topViewController.viewAppearToSuperView(getContentView(), animated);
			}
		}
		
		if(_topControllerDirection == TopControllerDirectionRight){
			
			topViewController = getRightViewController();
		
			if(topViewController != null && !topViewController.isViewAppeared()){
				topViewController.viewAppearToSuperView(getContentView(), animated);
			}
		}
		
		topViewController = getCenterViewController();
		
		if(topViewController != null && !topViewController.isViewAppeared()){
			topViewController.viewAppearToSuperView(getContentView(), animated);
		}
		
	}
	
	@Override
	public void viewDidAppear(boolean animated){
		super.viewDidAppear(animated);
	}
	
	@Override
	public void viewWillDisappear(boolean animated){
		super.viewWillDisappear(animated);
	}
	
	@Override
	public void viewDidDisappear(boolean animated){
		super.viewDidDisappear(animated);

	}


	public ViewGroup getContentView(){
		return _contentView;
	}

	protected void onTopControllerChanged(){
		
	}
	
	@Override
	public String loadURL(URL url,String basePath,boolean animated){
		
	    basePath = URL.stringAddPathComponent(basePath, getAlias());
	    
	    String alias = url.firstPathComponent(basePath);
	    
	    if(alias != null){
	    	
	    	IViewController<T> topController = getCenterViewController();
	    	
	    	if(topController == null || !alias.equals(topController.getAlias())){
	    		
	    		if(topController != null){
	    			if(topController.isViewAppeared()){
	    				topController.viewRemoveForSuperView(animated);
	    			}
	    			topController.setParentController(null);
	    		}
	    		
	    		topController = getViewControllerContext().getViewController(url, basePath);
	    		
	    		if(topController != null){
	    			
	    			topController.setParentController(this);
	    			
	    			View v = topController.getView();
	    			
	    			if(_topControllerDirection == TopControllerDirectionLeft){
	    				v.setX(v.getWidth() - FoldWidth);
	    			}
	    			else if(_topControllerDirection == TopControllerDirectionRight){
	    				v.setX(FoldWidth - v.getWidth());
	    			}
	    			else{
	    				v.setX(0);
	    			}
	    			
	    			if(isViewAppeared()){
	    				topController.viewAppearToSuperView(getContentView(), animated);
	    			}
	    			
	    			setCenterViewController(topController);
	    		}
	    	}
	    	
	    	if(topController != null){
	    		basePath = topController.loadURL(url, basePath, animated);
	    	}
	    	
	    }
	    
	    return basePath;
	}
	
	protected void setTopControllerDirection(int topControllerDirection,boolean animated){
		
		if(getViewControllerContext().isIdleTimerDisabled() && animated && isViewAppeared()){
			return ;
		}
		
		if(_topControllerDirection != topControllerDirection){
			
			getView();
			
			final ViewGroup contentView = getContentView();
			
			if(animated && isViewAppeared()){
				
				switch(topControllerDirection){
				case TopControllerDirectionCenter:
				{
					Animation anim = new Animation();
					
					anim.setDuration(300);
					anim.setListener(new AnimatorListener() {
						
						public void onEnd(){
							
							IViewController<T> topController = getLeftViewController();
							
							if(topController != null){
								if(topController.isViewAppeared()){
									topController.viewRemoveForSuperView(true);
								}
							}
							
							topController = getRightViewController();
							
							if(topController != null){
								if(topController.isViewAppeared()){
									topController.viewRemoveForSuperView(true);
								}
							}
							
							contentView.setEnabled(true);
							getViewControllerContext().setIdleTimerDisabled(false);
						}
						
						public void onAnimationStart(Animator animation) {
							
						}
						
						public void onAnimationRepeat(Animator animation) {
							
						}
						
						public void onAnimationEnd(Animator animation) {
							onEnd();
						}
						
						public void onAnimationCancel(Animator animation) {
							onEnd();
						}
					});
					
					IViewController<T> topController = getLeftViewController();
					
					if(topController != null){
						if(topController.isViewAppeared()){
							anim.alpha(topController.getView(),1.0f,AlphaValue);
						}
					}
					
					topController = getCenterViewController();
					
					if(topController != null){
						
						View v = topController.getView();
						
						anim.translateTo(v, 0, 0);

						if(!topController.isViewAppeared()){
							topController.viewAppearToSuperView(contentView, animated);
						}
						
					}
					
					topController = getRightViewController();
					
					if(topController != null){
						
						if(topController.isViewAppeared()){
							anim.alpha(topController.getView(),1.0f,AlphaValue);
						}
						
					}
					contentView.setEnabled(false);
					getViewControllerContext().setIdleTimerDisabled(true);
					anim.submit();
				}
					break;
				case TopControllerDirectionLeft:
				{
					
					Animation anim = new Animation();
					
					anim.setDuration(300);
					anim.setListener(new AnimatorListener() {
						
						public void onEnd(){
							
							IViewController<T> topController =  getRightViewController();
							
							if(topController != null){
								if(topController.isViewAppeared()){
									topController.viewRemoveForSuperView(true);
								}
							}
							
							contentView.setEnabled(true);
							getViewControllerContext().setIdleTimerDisabled(false);
						}
						
						public void onAnimationStart(Animator animation) {
							
						}
						
						public void onAnimationRepeat(Animator animation) {
							
						}
						
						public void onAnimationEnd(Animator animation) {
							onEnd();
						}
						
						public void onAnimationCancel(Animator animation) {
							onEnd();
						}
					});
					
					IViewController<T> topController = getLeftViewController();
					
					if(topController != null){

						if(!topController.isViewAppeared()){
							topController.viewAppearToSuperView(getContentView(), animated,true);
						}
						
						anim.alpha(topController.getView(), AlphaValue, 1.0f);
						
					}
					
					topController = getCenterViewController();
					
					if(topController != null){
						
						View v = topController.getView();
						
						anim.translateTo(v, contentView.getWidth() - FoldWidth, 0);
						
						if(!topController.isViewAppeared()){
							topController.viewAppearToSuperView(getContentView(), animated);
						}					
						
					}
					
					topController = getRightViewController();
					
					if(topController != null){
						
						if(topController.isViewAppeared()){
							anim.alpha(topController.getView(), 1.0f, AlphaValue);
						}
						
					}
					
					contentView.setEnabled(false);
					getViewControllerContext().setIdleTimerDisabled(true);
					anim.submit();
				}
					break;
				case TopControllerDirectionRight:
				{
					
					Animation anim = new Animation();
					
					anim.setDuration(300);
					anim.setListener(new AnimatorListener() {
						
						public void onEnd(){
							
							IViewController<T> topController = getLeftViewController();
							
							if(topController != null){
								if(topController.isViewAppeared()){
									topController.viewRemoveForSuperView(true);
								}
							}
							
							contentView.setEnabled(true);
							getViewControllerContext().setIdleTimerDisabled(false);
						}
						
						public void onAnimationStart(Animator animation) {
							
						}
						
						public void onAnimationRepeat(Animator animation) {
							
						}
						
						public void onAnimationEnd(Animator animation) {
							onEnd();
						}
						
						public void onAnimationCancel(Animator animation) {
							onEnd();
						}
					});
					

					IViewController<T> topController = getLeftViewController();
					
					if(topController != null){
						if(topController.isViewAppeared()){
							anim.alpha(topController.getView(), 1.0f, AlphaValue);
						}
					}
					
					topController = getCenterViewController();
					
					if(topController != null){
						
						View v = topController.getView();
						
						anim.translateTo(v, FoldWidth - contentView.getWidth(), 0);
						
						if(!topController.isViewAppeared()){
							topController.viewAppearToSuperView(contentView, animated);
						}
						
					}
					
					topController = getRightViewController();
					
					if(topController != null){
						
						if(!topController.isViewAppeared()){
							topController.viewAppearToSuperView(contentView, animated,true);
						}

						anim.alpha(topController.getView(), AlphaValue,1.0f);
					}
					
					contentView.setEnabled(false);
					getViewControllerContext().setIdleTimerDisabled(true);
					anim.submit();
					
				}
					break;
				}
				
			}
			else{
				
				switch(topControllerDirection){
				case TopControllerDirectionCenter:
				{
					IViewController<T> topController = getLeftViewController();
					
					if(topController != null){
						if(topController.isViewAppeared()){
							topController.viewRemoveForSuperView(animated);
						}
					}
					
					topController = getCenterViewController();
					
					if(topController != null){
						
						View v = topController.getView();
						
						v.setX(0);
						
						if(isViewAppeared()){
							if(!topController.isViewAppeared()){
								topController.viewAppearToSuperView(contentView, animated);
							}
						}
						
					}
					
					topController = getRightViewController();
					
					if(topController != null){
						
						if(topController.isViewAppeared()){
							topController.viewRemoveForSuperView(animated);
						}
						
					}
				}
					break;
				case TopControllerDirectionLeft:
				{
					
					IViewController<T> topController = getLeftViewController();
					
					if(topController != null){
						if(isViewAppeared()){
							if(!topController.isViewAppeared()){
								topController.viewAppearToSuperView(getContentView(), animated,true);
							}
						}
					}
					
					topController = getCenterViewController();
					
					if(topController != null){
						
						View v = topController.getView();
						
						v.setX(contentView.getWidth() - FoldWidth);
						
						if(isViewAppeared()){
							if(!topController.isViewAppeared()){
								topController.viewAppearToSuperView(getContentView(), animated);
							}
						}
						
					}
					
					topController = getRightViewController();
					
					if(topController != null){
						
						if(topController.isViewAppeared()){
							topController.viewRemoveForSuperView(animated);
						}
						
					}
					
				}
					break;
				case TopControllerDirectionRight:
				{

					IViewController<T> topController = getLeftViewController();
					
					if(topController != null){
						if(topController.isViewAppeared()){
							topController.viewRemoveForSuperView(animated);
						}
					}
					
					topController = getCenterViewController();
					
					if(topController != null){
						
						View v = topController.getView();
						
						v.setX(FoldWidth - contentView.getWidth());
						
						if(isViewAppeared()){
							if(!topController.isViewAppeared()){
								topController.viewAppearToSuperView(contentView, animated);
							}
						}
						
					}
					
					topController = getRightViewController();
					
					if(topController != null){
						
						if(isViewAppeared()){
							if(!topController.isViewAppeared()){
								topController.viewAppearToSuperView(contentView, animated,true);
							}
						}

					}
					
				}
					break;
				}
				
			}
			
			_topControllerDirection = topControllerDirection;
		}
	}
	
	@Override
	public boolean openURL(URL url,boolean animated){
		
		String scheme = getScheme();
		
		if(scheme == null){
			scheme = "fold";
		}
		
		if(scheme.equals(url.getScheme())){

			String alias = url.firstPathComponent("/");
			
			if(alias != null){
				
				IViewController<T> topController = getLeftViewController();
				
				if(topController != null && alias.equals(topController.getAlias())){
					
					Log.d(Framework.TAG, url.toString());
					
					topController.loadURL(url, "/", animated);
					
					setTopControllerDirection(TopControllerDirectionLeft,animated);
					
					return true;
				}
				
				topController = getCenterViewController();
				
				if(topController != null && alias.equals(topController.getAlias())){
					
					Log.d(Framework.TAG, url.toString());
					
					topController.loadURL(url, "/", animated);
					
					setTopControllerDirection(TopControllerDirectionCenter,animated);
					
					return true;
				}
				
				topController = getRightViewController();
				
				if(topController != null && alias.equals(topController.getAlias())){
					
					Log.d(Framework.TAG, url.toString());
					
					topController.loadURL(url, "/", animated);
					
					setTopControllerDirection(TopControllerDirectionRight,animated);
					
					return true;
				}
				
			}
			else{
				
				Log.d(Framework.TAG, url.toString());
				
				setTopControllerDirection(TopControllerDirectionCenter,animated);
				
				return true;
			}

		}
		
		return super.openURL(url, animated);
	}
	
	@Override
	public boolean onPressBack(){
		
		IViewController<T> controller = getModalViewController();
		
		if(controller != null){
			return true;
		}
		
		if(_topControllerDirection != TopControllerDirectionCenter){
			return true;
		}
		
		controller = getParentController();
		
		if(controller != null){
			return controller.onPressBack();
		}
		
		return super.onPressBack();
	}
	
	public boolean onDown(MotionEvent e) {
		
		IViewController<T> viewController = getTopViewController();
		
		if(viewController != null){
			
			boolean disabledHeapGesture = Value.booleanValueForKey(viewController.getConfig(),"disabledFoldGesture");
			
			if(!disabledHeapGesture){
				
				_gestureTouchX = e.getX();
				
				return true;
			}
			
		}

		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		float dx = e2.getX() - _gestureTouchX;
		
		if(distanceX < 0.0f){
			_gestureDirection = GestureDirectionRight;
		}
		else{
			_gestureDirection = GestureDirectionLeft;
		}
		
		ViewGroup contentView = getContentView();

		if(dx == 0.0f){
			
			IViewController<T> controller = getCenterViewController();
			
			if(controller != null){
				View v = controller.getView();
				v.setX(dx);
			}
			
			controller = getLeftViewController();
			
			if(controller != null){
				
				if(controller.isViewAppeared()){
					controller.viewRemoveForSuperView(false);
				}
				
			}
			
			controller = getRightViewController();
			
			if(controller != null){
				
				if(controller.isViewAppeared()){
					controller.viewRemoveForSuperView(false);
				}
				
			}
		}
		else if(dx < 0.0f){
			
			IViewController<T> controller = getLeftViewController();
			
			if(controller != null){
				
				if(controller.isViewAppeared()){
					controller.viewRemoveForSuperView(false);
				}
				
			}
			
			controller = getRightViewController();
			
			if(controller != null){
				
				if(!controller.isViewAppeared()){
					controller.viewAppearToSuperView(contentView, false, true);
				}
				
				View v = controller.getView();
				
				float alpha = AlphaValue - (1.0f - AlphaValue) * dx / contentView.getWidth();
				
				v.setAlpha(alpha );
				
				controller = getCenterViewController();
				
				if(controller != null){
					v = controller.getView();
					v.setX(dx);
				}
			}
			else{
				
				controller = getCenterViewController();
				
				if(controller != null){
					View v = controller.getView();
					v.setX(0);
				}
			}
			
		}
		else{
			
			IViewController<T> controller = getRightViewController();
			
			if(controller != null){
				
				if(controller.isViewAppeared()){
					controller.viewRemoveForSuperView(false);
				}
			}
			
			controller = getLeftViewController();
			
			if(controller != null){
				
				if(!controller.isViewAppeared()){
					controller.viewAppearToSuperView(contentView, false, true);
				}
				
				View v = controller.getView();
				
				float alpha = AlphaValue - (1.0f - AlphaValue) * dx / contentView.getWidth();
				
				v.setAlpha(alpha );
				
				controller = getCenterViewController();
				
				if(controller != null){
					v = controller.getView();
					v.setX(dx);
				}
			}
			else{
				
				controller = getCenterViewController();
				
				if(controller != null){
					View v = controller.getView();
					v.setX(0);
				}
				
			}

		}
	
		
		return true;
	}

	public void onShowPress(MotionEvent e) {
	
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onTouch(View view, MotionEvent event) {
		
		boolean rs = _gestureDetector.onTouchEvent(event);
		
		int action = event.getAction();
		
		if(MotionEvent.ACTION_CANCEL == action || MotionEvent.ACTION_UP == action){
			
			if(_gestureDirection != GestureDirectionNone){
				
				final IViewController<T> leftViewController = getLeftViewController();
				final IViewController<T> rightViewController = getRightViewController();
				final IViewController<T> centerViewController = getCenterViewController();
				
				final ViewGroup contentView = getContentView();
				
				if(leftViewController != null && leftViewController.isViewAppeared()){
	
					Animation anim = new Animation();
					
					anim.setDuration(300);
					
					if(_gestureDirection == GestureDirectionRight){
					
						anim.setListener(new AnimatorListener() {
							
							public void onAnimationStart(Animator animation) {
								
							}
							
							public void onAnimationRepeat(Animator animation) {
								
							}
							
							public void onAnimationEnd(Animator animation) {
								onEnd();
							}
							
							public void onAnimationCancel(Animator animation) {
								onEnd();
							}
							
							public void onEnd(){
								
								if(rightViewController != null && !rightViewController.isViewAppeared()){
									rightViewController.viewRemoveForSuperView(true);
								}
								
								getViewControllerContext().setIdleTimerDisabled(false);
								contentView.setEnabled(true);
							}
						});
					
						if(centerViewController != null){
							View v = centerViewController.getView();
							anim.translateTo(v, 0.0f, 0.0f);
						}
						
						if(leftViewController != null){
							View v = leftViewController.getView();
							anim.alphaTo(v, 1.0f);
						}
						
						_topControllerDirection = TopControllerDirectionLeft;
					}
					else{
						
						anim.setListener(new AnimatorListener() {
							
							public void onAnimationStart(Animator animation) {
								
							}
							
							public void onAnimationRepeat(Animator animation) {
								
							}
							
							public void onAnimationEnd(Animator animation) {
								onEnd();
							}
							
							public void onAnimationCancel(Animator animation) {
								onEnd();
							}
							
							public void onEnd(){
								
								if(rightViewController != null && !rightViewController.isViewAppeared()){
									rightViewController.viewRemoveForSuperView(true);
								}
								
								if(leftViewController != null && !leftViewController.isViewAppeared()){
									leftViewController.viewRemoveForSuperView(true);
								}
								
								getViewControllerContext().setIdleTimerDisabled(false);
								contentView.setEnabled(true);
							}
						});
						
					}
					
					if(centerViewController != null){
						View v = centerViewController.getView();
						anim.translateTo(v, 0.0f, 0.0f);
					}
					
					anim.submit();
					getViewControllerContext().setIdleTimerDisabled(true);
					contentView.setEnabled(false);
					
					_topControllerDirection = TopControllerDirectionCenter;
					
				}
				else if(rightViewController != null && rightViewController.isViewAppeared()){
					
					Animation anim = new Animation();
					
					anim.setDuration(300);
					
					if(_gestureDirection == GestureDirectionLeft){
					
						anim.setListener(new AnimatorListener() {
							
							public void onAnimationStart(Animator animation) {
								
							}
							
							public void onAnimationRepeat(Animator animation) {
								
							}
							
							public void onAnimationEnd(Animator animation) {
								onEnd();
							}
							
							public void onAnimationCancel(Animator animation) {
								onEnd();
							}
							
							public void onEnd(){
								
								if(leftViewController != null && !leftViewController.isViewAppeared()){
									leftViewController.viewRemoveForSuperView(true);
								}
								
								getViewControllerContext().setIdleTimerDisabled(false);
								contentView.setEnabled(true);
							}
						});
					
						if(centerViewController != null){
							View v = centerViewController.getView();
							anim.translateTo(v, 0.0f, 0.0f);
						}
						
						if(rightViewController != null){
							View v = rightViewController.getView();
							anim.alphaTo(v, 1.0f);
						}
						
						_topControllerDirection = TopControllerDirectionRight;
					}
					else{
						
						anim.setListener(new AnimatorListener() {
							
							public void onAnimationStart(Animator animation) {
								
							}
							
							public void onAnimationRepeat(Animator animation) {
								
							}
							
							public void onAnimationEnd(Animator animation) {
								onEnd();
							}
							
							public void onAnimationCancel(Animator animation) {
								onEnd();
							}
							
							public void onEnd(){
								
								if(rightViewController != null && !rightViewController.isViewAppeared()){
									rightViewController.viewRemoveForSuperView(true);
								}
								
								if(leftViewController != null && !leftViewController.isViewAppeared()){
									leftViewController.viewRemoveForSuperView(true);
								}
								
								getViewControllerContext().setIdleTimerDisabled(false);
								contentView.setEnabled(true);
							}
						});
						
						_topControllerDirection = TopControllerDirectionCenter;
					}
					
					if(centerViewController != null){
						View v = centerViewController.getView();
						anim.translateTo(v, 0.0f, 0.0f);
					}
					
					anim.submit();
					getViewControllerContext().setIdleTimerDisabled(true);
					contentView.setEnabled(false);
					
				}
				else{
					
					Animation anim = new Animation();
					
					anim.setDuration(300);
					
					anim.setListener(new AnimatorListener() {
						
						public void onAnimationStart(Animator animation) {
							
						}
						
						public void onAnimationRepeat(Animator animation) {
							
						}
						
						public void onAnimationEnd(Animator animation) {
							onEnd();
						}
						
						public void onAnimationCancel(Animator animation) {
							onEnd();
						}
						
						public void onEnd(){
							
							if(rightViewController != null && !rightViewController.isViewAppeared()){
								rightViewController.viewRemoveForSuperView(true);
							}
							
							if(leftViewController != null && !leftViewController.isViewAppeared()){
								leftViewController.viewRemoveForSuperView(true);
							}
							
							getViewControllerContext().setIdleTimerDisabled(false);
							contentView.setEnabled(true);
						}
					});
					
					_topControllerDirection = TopControllerDirectionCenter;
					
					if(centerViewController != null){
						View v = centerViewController.getView();
						anim.translateTo(v, 0.0f, 0.0f);
					}
					
					anim.submit();
					getViewControllerContext().setIdleTimerDisabled(true);
					contentView.setEnabled(false);
				}
				
				_gestureDirection = GestureDirectionNone;
				_gestureTouchX = 0;
			}
			

		}
		
		
		return rs;
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		if(_leftViewController != null){
			_leftViewController.onLowMemory();
		}
		if(_rightViewController != null){
			_rightViewController.onLowMemory();
		}
		if(_centerViewController != null){
			_centerViewController.onLowMemory();
		}
	}
	
	
	@Override
	public void onServiceContextStart() {
		super.onServiceContextStart();
		
		if(_leftViewController != null){
			_leftViewController.onServiceContextStart();
		}
		if(_rightViewController != null){
			_rightViewController.onServiceContextStart();
		}
		if(_centerViewController != null){
			_centerViewController.onServiceContextStart();
		}
		
	}

	@Override
	public void onServiceContextStop() {

		if(_leftViewController != null){
			_leftViewController.onServiceContextStop();
		}
		if(_rightViewController != null){
			_rightViewController.onServiceContextStop();
		}
		if(_centerViewController != null){
			_centerViewController.onServiceContextStop();
		}
		
		super.onServiceContextStop();
	}
}
