package org.hailong.framework.controllers;

import java.util.ArrayList;
import java.util.List;

import org.hailong.framework.IServiceContext;
import org.hailong.framework.R;
import org.hailong.framework.ServiceContextHandler;

import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class TabBarController<T extends IServiceContext> extends
		ViewController<T>  implements IViewControllerContext<T> {

	private List<ViewController<T>> _viewControllers;
	private ViewGroup _contentView;
	private int _selectedIndex;
	
	public TabBarController(IViewControllerContext<T> activity) {
		super(activity, 0);
	}
	
	public TabBarController(IViewControllerContext<T> activity,ViewController<T> rootViewController) {
		super(activity, 0);
		_viewControllers = new ArrayList<ViewController<T>>();
		_viewControllers.add(rootViewController);
		rootViewController.setParentViewController(this);
	}
	
	public TabBarController(IViewControllerContext<T> activity, int viewLayout) {
		super(activity, viewLayout);
	}
	
	
	public ViewController<T> getSelectedViewController(){
		if(_selectedIndex >=0 && _viewControllers != null && _selectedIndex < _viewControllers.size()){
			return _viewControllers.get(_selectedIndex);
		}
		return null;
	}
	
	public int getSelectedIndex(){
		return _selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex){
		setSelectedIndex(selectedIndex,false);
	}
	
	public void setSelectedIndex(int selectedIndex,boolean animated){
		if(selectedIndex >=0 && _viewControllers != null && selectedIndex < _viewControllers.size() && _selectedIndex != selectedIndex){
			if(isViewAppeared()){
				ViewController<T> topViewController = getSelectedViewController();
				if(topViewController !=null){
					topViewController.viewRemoveForSuperView(animated);
				}
			}
			
			_selectedIndex = selectedIndex;
			
			if(isViewAppeared()){
				ViewController<T> topViewController = getSelectedViewController();
				if(topViewController !=null){
					topViewController.viewAppearToSuperView(getContentView(), animated);
				}
			}
			
			onSelectedControllerChanged();
		}
	}
	
	public void setSelectedViewController(ViewController<T> viewController){
		setSelectedViewController(viewController,false);
	}
	
	public void setSelectedViewController(ViewController<T> viewController,boolean animated){
		if(_viewControllers !=null){
			setSelectedIndex(_viewControllers.indexOf(viewController),animated);
		}
	}
	
	public void setViewControllers(List<ViewController<T>> viewControllers){
		setViewControllers(viewControllers, false);
	}

	public void setViewControllers(List<ViewController<T>> viewControllers,boolean animated){
		
		if(isViewAppeared() && _viewControllers != null){
			ViewController<T> topViewController = getSelectedViewController();
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
			ViewController<T> topViewController = getSelectedViewController();
			if(topViewController !=null){
				
				ViewGroup contentView = getContentView();
				
				topViewController.viewAppearToSuperView(contentView, animated);
		
				TranslateAnimation animation = new  TranslateAnimation(contentView.getWidth(), 0, 0, 0);
				animation.setDuration(300);
				topViewController.getView().startAnimation(animation);
			}
		}
		
		onSelectedControllerChanged();
	}
	
	public List<ViewController<T>> getViewControllers(){
		return _viewControllers;
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
		
		ViewController<T> topViewController = getSelectedViewController();
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
		
		ViewController<T> topViewController = getSelectedViewController();
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
		if("@root".equals(uri)){
			setSelectedIndex(0,animated);
			return true;
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
		
		ViewController<T> topViewController = getSelectedViewController();
	    if (topViewController != null ) {
	        if(! topViewController.onKeyDown(keyCode, event)){
	        	if(onPressBack()){
	        		super.onKeyDown(keyCode, event);
	        	}
	        	return false;
	        }
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public int getControllerOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}
	
	public void onOrientationChanged(int orientation){
		
		if(getModalViewController() != null){
			getModalViewController().onOrientationChanged(orientation);
			return ;
		}
		
		ViewController<T> topViewController = getSelectedViewController();
	    if (topViewController != null ) {
	    	topViewController.onOrientationChanged(orientation);
	    	return;
	    }
	    
		super.onOrientationChanged(orientation);
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

	
	protected void onSelectedControllerChanged(){
		
	}
	
	public ViewController<T> getFocusViewController(){
		ViewController<T> focusController = super.getFocusViewController();
		if(getSelectedViewController() != null && focusController == this){
			return getSelectedViewController();
		}
		return focusController;
	}
	
	public void onFocusViewControllerChanged() {
		getControllerContext().onFocusViewControllerChanged();
	}
}
