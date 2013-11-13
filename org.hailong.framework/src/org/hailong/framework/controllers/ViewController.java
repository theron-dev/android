package org.hailong.framework.controllers;

import java.util.Map;

import org.hailong.framework.Controller;
import org.hailong.framework.IServiceContext;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class ViewController<T extends IServiceContext> extends Controller<T> {

	private LayoutInflater _layoutInflater;
	private int _viewLayout;
	private View _view;
	private ViewController<T> _parentViewController;
	private Object _config;
	private String _token;
	private String _title;
	private IViewControllerContext<T> _context;
	private ViewController<T> _modalViewController;
	private Handler _handler;
	private boolean _animation;

	public ViewController(IViewControllerContext<T> context,int viewLayout) {
		super(context.getRootContext());
		_context = context;
		_layoutInflater = LayoutInflater.from(getContext());
		_viewLayout = viewLayout;
		_handler = new Handler();
		_animation = false;
	}

	public IViewControllerContext<T> getControllerContext(){
		return _context;
	}
	
	@Override
	public void onServiceContextStart() {

	}

	@Override
	public void onServiceContextStop() {

	}

	@Override
	public void destroy() {
		_view = null;
		_parentViewController = null;
	}
	
	public void onLowMemory(){
		if(_view != null && _view.getParent() == null){
			_view = null;
			didViewUnLoaded();
		}
	}
	

	protected void didViewLoaded(){

	}
	
	protected void didViewUnLoaded(){
		
	}
	
	public void viewWillAppear(boolean animated){
		getControllerContext().onFocusViewControllerChanged();
	}
	
	public void viewDidAppear(boolean animated){
	}
	
	public void viewWillDisappear(boolean animated){
		
	}
	
	public void viewDidDisappear(boolean animated){

	}
	
	protected void loadView() {
		if(_viewLayout == 0){
			_view = new View(getContext());
		}
		else{
			_view = _layoutInflater.inflate(_viewLayout, null);
		}
		didViewLoaded();
	}
	
	public View getView(){
		if(_view == null){
			loadView();
		}
		return _view;
	}
	
	public void setView(View view){
		_view = view;
	}
	
	public boolean isViewLoaded(){
		return _view != null;
	}
	
	public boolean isViewAppeared(){
		return _view != null && ( _view.getParent() != null || (_modalViewController != null && _modalViewController.isViewLoaded() 
				&& _modalViewController.getView().getParent() != null));
	}
	
	public void viewAppearToSuperView(ViewGroup superView,boolean animated){
		View view = getView();
		viewWillAppear(animated);
		superView.addView(view);
		viewWillDisappear(animated);
	}

	public void viewRemoveForSuperView(boolean animated){
		if(_view != null && _view.getParent() !=null){
			viewWillDisappear(animated);
			((ViewGroup)_view.getParent()).removeView(_view);
			viewDidDisappear(animated);
		}
	}
	
	protected int getViewLayoutReseource(){
		return _viewLayout;
	}
	
	public ViewController<T> getParentViewController(){
		return _parentViewController;
	}
	
	public void setParentViewController(ViewController<T> parentViewController){
		_parentViewController = parentViewController;
	}
	
	public Object getConfig(){
		return _config;
	}
	
	public void setConfig(Object config){
		_config = config;
	}
	
	public String getToken(){
		return _token;
	}
	
	public void setToken(String token){
		_token = token;
	}
	
	public boolean isDisplaced(){
		if(_config != null && _config instanceof Map){
			@SuppressWarnings("unchecked")
			boolean disabledDisplaced = (Boolean) ((Map<String,Object>)_config).get("disabledDisplaced");
			if(disabledDisplaced){
				return false;
			}
		}
		return getParentViewController() == null && getModalViewController() == null;
	}
	
	public String getTitle(){
		return _title;
	}
	
	public void setTitle(String title){
		_title = title;
	}
	
	public boolean onPressBack(){
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(_modalViewController != null){
			return _modalViewController.onKeyDown(keyCode, event);
		}
		ViewController<T> parentViewController = getParentViewController();
	    if (keyCode == KeyEvent.KEYCODE_BACK 
	    		&& event.getRepeatCount() == 0 
	    		&& parentViewController != null 
	    		&& parentViewController.getModalViewController() ==this) {
	    	
	    	if(onPressBack()){
	    		_handler.post(new Runnable(){

					public void run() {
						dismissModalViewController(true);
					}});
	    		
	    	}
	    	return true;
	    }
	    
	    return false;
	}
	
	public ViewController<T> getModalViewController(){
		return _modalViewController;
	}
	
	public void setModalViewController(ViewController<T> viewController){
		setModalViewController(viewController,true);
	}
	
	public void setModalViewController(ViewController<T> viewController,boolean animated){
		
		if( _animation || (viewController != null && _modalViewController !=null) || viewController == _modalViewController){
			return;
		}
		
		if(isViewAppeared()){
			
			if(_modalViewController !=null){
				if(animated){
					ViewGroup contentView = (ViewGroup)_modalViewController.getView().getParent();
					
					final ViewController<T> topViewController = _modalViewController;
					
					TranslateAnimation animation = new TranslateAnimation(0, 0, 0, contentView.getHeight());
					animation.setDuration(300);
					animation.setAnimationListener(new AnimationListener() {
						
						public void onAnimationStart(Animation animation) {
							
						}
						
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						public void onAnimationEnd(Animation animation) {
							_handler.post(new Runnable(){

								public void run() {
									((View)topViewController.getView().getParent()).setEnabled(true);
									topViewController.viewRemoveForSuperView(true);
									topViewController.setParentViewController(null);
									_animation = false;
								}
								
							});
						}
					});
					
					_animation = true;
					contentView.setEnabled(false);
					viewAppearToSuperView(contentView, false);
					topViewController.getView().bringToFront();
					topViewController.getView().startAnimation(animation);
				}
				else{
					ViewGroup contentView = (ViewGroup)_modalViewController.getView().getParent();
					viewAppearToSuperView(contentView, false);
					_modalViewController.viewRemoveForSuperView(animated);
					_modalViewController.setParentViewController(null);
				}
			}
		}
		
		_modalViewController = viewController;
		
		if(_modalViewController != null){
			_modalViewController.setParentViewController(this);
		}

		if(isViewAppeared()){
			if(_modalViewController !=null){
				
				ViewGroup contentView = (ViewGroup)getView().getParent();
				
				_modalViewController.viewAppearToSuperView(contentView, animated);
				
				if(animated){
					TranslateAnimation animation = new TranslateAnimation(0, 0, contentView.getHeight(), 0);
					animation.setDuration(300);
					animation.setAnimationListener(new AnimationListener() {
						
						public void onAnimationStart(Animation animation) {
							
						}
						
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						public void onAnimationEnd(Animation animation) {
							_handler.post(new Runnable(){

								public void run() {
									ViewGroup contentView = (ViewGroup)getView().getParent();
									contentView.setEnabled(true);
									viewRemoveForSuperView(false);
									_animation = false;
								}
								
							});
						}
					});
					_animation = true;
					contentView.setEnabled(false);
					_modalViewController.getView().startAnimation(animation);
					
				}
				else{
					viewRemoveForSuperView(false);
				}
			}
		}
	}
	
	public void dismissModalViewController(boolean animated){
		ViewController<T> parentViewController = getParentViewController();
		if(parentViewController != null && parentViewController.getModalViewController() ==this){
			parentViewController.setModalViewController(null, animated);
		}
	}
	
	public int getControllerOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}
	
	public void onOrientationChanged(int orientation){
		if(getModalViewController() != null){
			getModalViewController().onOrientationChanged(orientation);
			return ;
		}
	}
	
	public boolean isAnimation(){
		return _animation;
	}
	
	protected void setAnimation(boolean animation){
		_animation = animation;
	}
	
	public ViewController<T> getFocusViewController(){
		if(getModalViewController() != null){
			return getModalViewController();
		}
		return this;
	}
}
