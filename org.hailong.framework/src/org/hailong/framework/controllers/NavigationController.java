package org.hailong.framework.controllers;

import java.util.ArrayList;
import java.util.List;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.R;
import org.hailong.framework.ServiceContextHandler;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class NavigationController<T extends IServiceContext> extends
		ViewController<T> implements IViewControllerContext<T> {

	private List<ViewController<T>> _viewControllers;
	private ViewGroup _contentView;
	private Handler _handler;
	
	public NavigationController(IViewControllerContext<T> activity) {
		super(activity, 0);
		_handler = new Handler();
	}
	
	public NavigationController(IViewControllerContext<T> activity,ViewController<T> rootViewController) {
		super(activity, 0);
		_handler = new Handler();
		_viewControllers = new ArrayList<ViewController<T>>();
		_viewControllers.add(rootViewController);
		rootViewController.setParentViewController(this);
	}
	
	public NavigationController(IViewControllerContext<T> activity, int viewLayout) {
		super(activity, viewLayout);
		_handler = new Handler();
	}
	
	public void setViewControllers(List<ViewController<T>> viewControllers){
		setViewControllers(viewControllers, false);
	}
	
	public void setViewControllers(List<ViewController<T>> viewControllers,boolean animated){
		
		if(isViewAppeared() && _viewControllers != null){
			ViewController<T> topViewController = getTopViewController();
			if(topViewController !=null){
				topViewController.viewRemoveForSuperView(animated);
			}
		}
		if(_viewControllers != null){
			for(ViewController<T> viewController : _viewControllers){
				viewController.setParentViewController(null);
			}
		}
		
		_viewControllers = viewControllers;
		
		if(_viewControllers != null){
			for(ViewController<T> viewController : _viewControllers){
				viewController.setParentViewController(this);
			}
		}
		
		if(isViewAppeared()){
			ViewController<T> topViewController = getTopViewController();
			if(topViewController !=null){
				
				ViewGroup contentView = getContentView();
				
				topViewController.viewAppearToSuperView(contentView, animated);
		
				TranslateAnimation animation = new  TranslateAnimation(contentView.getWidth(), 0, 0, 0);
				animation.setDuration(300);
				topViewController.getView().startAnimation(animation);
			}
		}
		
		onTopControllerChanged();
	}
	
	public List<ViewController<T>> getViewControllers(){
		return _viewControllers;
	}
	
	public ViewController<T> getTopViewController(){
		if(_viewControllers != null && _viewControllers.size() >0){
			return _viewControllers.get(_viewControllers.size() - 1);
		}
		return null;
	}
	
	public ViewController<T> getRootViewController(){
		if(_viewControllers != null && _viewControllers.size() >0){
			return _viewControllers.get(0);
		}
		return null;
	}
	
	public void pushViewController(ViewController<T> viewController,boolean animated){
		
		if(isAnimation()){
			return;
		}
		
		if(_viewControllers == null ){
			_viewControllers = new ArrayList<ViewController<T>>();
		}
		
		if(isViewAppeared()){
			ViewGroup contentView = getContentView();
			
			final ViewController<T> topViewController = getTopViewController();
			if(topViewController !=null){
				
				if(animated){
					TranslateAnimation animation = new TranslateAnimation(0, - contentView.getWidth(), 0, 0);
					animation.setDuration(300);
					animation.setAnimationListener(new AnimationListener() {
						
						public void onAnimationStart(Animation animation) {
							
						}
						
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						public void onAnimationEnd(Animation animation) {
							_handler.post(new Runnable(){

								public void run() {
									topViewController.viewRemoveForSuperView(true);
									setAnimation(false);
								}
								
							});
						}
					});
					setAnimation(true);
					topViewController.getView().startAnimation(animation);
				}
				else{
					topViewController.viewRemoveForSuperView(animated);
				}
			}
			
			_viewControllers.add(viewController);
			viewController.setParentViewController(this);

			viewController.viewAppearToSuperView(contentView, animated);
			
			if(animated){
				TranslateAnimation animation = new TranslateAnimation(contentView.getWidth(), 0, 0, 0);
				animation.setDuration(300);
				animation.setAnimationListener(new AnimationListener() {
					
					public void onAnimationStart(Animation animation) {

					}
					
					public void onAnimationRepeat(Animation animation) {

					}
					
					public void onAnimationEnd(Animation animation) {
						getContentView().setEnabled(true);
					}
				});
				contentView.setEnabled(false);
				viewController.getView().startAnimation(animation);
			}

		}
		else{
			_viewControllers.add(viewController);
			viewController.setParentViewController(this);
		}
		
		onTopControllerChanged();
	}

	public void popViewController(boolean animated){
		if( (_viewControllers != null && _viewControllers.size() <2 ) ||  isAnimation()){
			return ;
		}
		if(isViewAppeared()){
			ViewGroup contentView = getContentView();
			final ViewController<T> topViewController = getTopViewController();
			if(topViewController !=null){
				
				if(animated){
					TranslateAnimation animation = new TranslateAnimation(0, contentView.getWidth(), 0, 0);
					animation.setDuration(300);
					animation.setAnimationListener(new AnimationListener() {
						
						public void onAnimationStart(Animation animation) {
							
						}
						
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						public void onAnimationEnd(Animation animation) {
							_handler.post(new Runnable(){

								public void run() {
									topViewController.viewRemoveForSuperView(true);
									topViewController.setParentViewController(null);
									setAnimation(false);
								}
								
							});
						}
					});
					
					setAnimation(true);
					topViewController.getView().startAnimation(animation);
				}
				else{
					topViewController.viewRemoveForSuperView(animated);
					topViewController.setParentViewController(null);
				}
				_viewControllers.remove(topViewController);
			}
			
			ViewController<T> viewController = getTopViewController();
			if(viewController != null){
				
				viewController.viewAppearToSuperView(getContentView(), animated);
				
				if(animated){
					TranslateAnimation animation = new TranslateAnimation(- contentView.getWidth(), 0, 0, 0);
					animation.setDuration(300);
					animation.setAnimationListener(new AnimationListener() {
						
						public void onAnimationStart(Animation animation) {
						}
						
						public void onAnimationRepeat(Animation animation) {
						}
						
						public void onAnimationEnd(Animation animation) {
							getContentView().setEnabled(true);
						}
					});
					getContentView().setEnabled(false);
					viewController.getView().startAnimation(animation);
				}
			}
		}
		else {
			ViewController<T> topViewController = getTopViewController();
			if(topViewController !=null){
				topViewController.setParentViewController(null);
				_viewControllers.remove(topViewController);
			}
		}
		onTopControllerChanged();
	}
	
	public void popToRootViewController(boolean animated){
		if((_viewControllers != null && _viewControllers.size() <2 ) || isAnimation()){
			return ;
		}
		if(isViewAppeared()){
			ViewController<T> topViewController = getTopViewController();
			if(topViewController !=null){
				topViewController.viewRemoveForSuperView(animated);
			}
			while(_viewControllers.size() >1){
				topViewController = getTopViewController();
				topViewController.setParentViewController(null);
				_viewControllers.remove(topViewController);
			}
			topViewController = getTopViewController();
			if(topViewController != null){
				topViewController.viewAppearToSuperView(getContentView(), animated);
			}
		}
		else{
			ViewController<T> topViewController;
			while(_viewControllers.size() >1){
				topViewController = getTopViewController();
				topViewController.setParentViewController(null);
				_viewControllers.remove(topViewController);
			}
		}
		
		onTopControllerChanged();
	}
	
	@Override
	protected void loadView() {
		if(getViewLayoutReseource() == 0){
			setView(new FrameLayout(getContext()));
			didViewLoaded();
		}
		else{
			super.loadView();
		}
	}
	
	@Override
	protected void didViewLoaded(){
		super.didViewLoaded();
		
		_contentView = (ViewGroup) getView().findViewById(R.id.contentView);
		if(_contentView == null){
			_contentView = (ViewGroup)getView();
		}
	}
	
	@Override
	protected void didViewUnLoaded(){
		super.didViewUnLoaded();
		
		_contentView = null;
	}
	
	@Override
	public void viewWillAppear(boolean animated){
		super.viewWillAppear(animated);
		
		ViewController<T> topViewController = getTopViewController();
		if(topViewController != null){
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
		
		ViewController<T> topViewController = getTopViewController();
		if(topViewController != null){
			topViewController.viewRemoveForSuperView(false);
		}
	}

	public boolean isBindServiceContext() {
		return getControllerContext().isBindServiceContext();
	}

	public void addServiceContextListener(ServiceContextHandler<T> listener) {
		getControllerContext().addServiceContextListener(listener);
	}

	public void removeServiceContextListener(ServiceContextHandler<T> listener) {
		getControllerContext().removeServiceContextListener(listener);
	}

	public ViewController<T> getInstance(String alias,
			IViewControllerContext<T> controllerContext) {
		return getControllerContext().getInstance(alias, controllerContext);
	}
	
	public ViewController<T> getInstance(String alias){
		return getControllerContext().getInstance(alias, this);
	}

	public boolean openUrl(String uri, boolean animated) {
		if("..".equals(uri)){
			popViewController(animated);
			return true;
		}
		else if("@root".equals(uri)){
			popToRootViewController(animated);
			return true;
		}
		else if(uri != null){
			String alias = uri;
			String token = null;
			int index = uri.indexOf("-");
			if(index >=0){
				alias = uri.substring(0, index);
				token = uri.substring(index +1);
			}
			ViewController<T> viewController = getInstance(alias,this);
			if(viewController != null){
				if(token != null){
					viewController.setToken(token);
				}
				pushViewController(viewController,animated);
				return true;
			}
		}
		return false;
	}

	public Object getValue(String key) {
		return getControllerContext().getValue(key);
	}

	public void setValue(String key, Object value) {
		getControllerContext().setValue(key, value);
	}

	public String setValue(Object value) {
		return getControllerContext().setValue(value);
	}

	public IViewControllerContext<T> getRootContext() {
		return getControllerContext().getRootContext();
	}

	public IViewControllerContext<T> getParentContext() {
		return getControllerContext();
	}
	
	public void setToken(String token){
		super.setToken(token);
		if(token != null){
			String[] aliass = token.split("/");
			List<ViewController<T>> viewControllers = new ArrayList<ViewController<T>>();
			for(String alias : aliass){
				ViewController<T> viewController = getInstance(alias,this);
				if(viewController != null){
					viewControllers.add(viewController);
				}
			}
			setViewControllers(viewControllers,false);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(getModalViewController() != null){
			return getModalViewController().onKeyDown(keyCode, event);
		}
		
		ViewController<T> topViewController = getTopViewController();
	    if (topViewController != null ) {
	        if( !topViewController.onKeyDown(keyCode, event)){
	        	
	        	if(topViewController.onPressBack()){
	        		if(_viewControllers.size() >1){
	        			_handler.post(new Runnable(){

							public void run() {
								popViewController(true);
							}});
		        		return true;
	        		}
	        		else{
	        			return false;
	        		}
	        		
	        	}
	        	
	        }
	        
	        return true;
	    
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public ViewGroup getContentView(){
		getView();
		return _contentView;
	}
	
	public void setResult(Object result){
		getControllerContext().setResult(result);
	}
	
	public Object getResult(){
		return getControllerContext().getResult();
	}
	
	public int getControllerOrientation(){
		ViewController<T> topViewController = getTopViewController();
	    if (topViewController != null ) {
	    	return topViewController.getControllerOrientation();
	    }
		return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}
	
	public void onOrientationChanged(int orientation){
		if(getModalViewController() != null){
			getModalViewController().onOrientationChanged(orientation);
			return ;
		}
		
		ViewController<T> topViewController = getTopViewController();
	    if (topViewController != null ) {
	    	topViewController.onOrientationChanged(orientation);
	    	return;
	    }
	    
	    
		super.onOrientationChanged(orientation);
	}
	
	protected void onTopControllerChanged(){
		
	}
	
	public ViewController<T> getFocusViewController(){
		ViewController<T> focusController = super.getFocusViewController();
		if(getTopViewController() != null && focusController == this){
			return getTopViewController();
		}
		return focusController;
	}

	public void onFocusViewControllerChanged() {
		getControllerContext().onFocusViewControllerChanged();
	}
}
