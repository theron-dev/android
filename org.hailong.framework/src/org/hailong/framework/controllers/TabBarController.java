package org.hailong.framework.controllers;

import java.util.ArrayList;
import java.util.List;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.R;
import org.hailong.framework.URL;
import org.hailong.framework.value.Value;

import android.util.Log;
import android.view.ViewGroup;

public class TabBarController<T extends IServiceContext> extends
		ViewController<T>  {

	private List<IViewController<T>> _viewControllers;
	private ViewGroup _contentView;
	private int _selectedIndex;
	
	public TabBarController(IViewControllerContext<T> activity) {
		super(activity, null);
	}
	
	public TabBarController(IViewControllerContext<T> activity, String viewLayout) {
		super(activity, viewLayout);
	}
	
	public IViewController<T> getSelectedViewController(){
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
				IViewController<T> topViewController = getSelectedViewController();
				if(topViewController !=null){
					topViewController.viewRemoveForSuperView(animated);
				}
			}
			
			_selectedIndex = selectedIndex;
			
			if(isViewAppeared()){
				IViewController<T> topViewController = getSelectedViewController();
				if(topViewController !=null){
					topViewController.viewAppearToSuperView(getContentView(), animated);
				}
			}
			
			onSelectedControllerChanged();
		}
	}
	
	public void setSelectedViewController(IViewController<T> viewController){
		setSelectedViewController(viewController,false);
	}
	
	public void setSelectedViewController(IViewController<T> viewController,boolean animated){
		if(_viewControllers !=null){
			setSelectedIndex(_viewControllers.indexOf(viewController),animated);
		}
	}
	
	public void setViewControllers(List<IViewController<T>> viewControllers){
		setViewControllers(viewControllers, false);
	}

	public void setViewControllers(List<IViewController<T>> viewControllers,boolean animated){
		
		if(isAnimation() || getViewControllerContext().isIdleTimerDisabled()){
			return;
		}
		
		ArrayList<IViewController<T>> newViewControllers = new ArrayList<IViewController<T>>(4);
		
		for(IViewController<T> viewController : viewControllers){
			
			newViewControllers.add(viewController);
			viewController.setParentController(this);

			if(_viewControllers != null){
				_viewControllers.remove(viewController);
			}
		}
		
		if(_viewControllers != null){
			
			for(IViewController<T> viewController : _viewControllers){
				
				viewController.setParentController(null);

				if(viewController.isViewAppeared()){
					viewController.viewRemoveForSuperView(animated);
				}
			}

		}
		
		_viewControllers = newViewControllers;

		if(_selectedIndex >= _viewControllers.size()){
			_selectedIndex = _viewControllers.size() - 1;
		}
		
		if(_selectedIndex < 0 ){
			_selectedIndex = 0;
		}
		
		if(isViewAppeared()){
			IViewController<T> topViewController = getSelectedViewController();
			
			for(IViewController<T> viewController : _viewControllers){
				
				if(topViewController == viewController){
					if(!topViewController.isViewAppeared()){
						topViewController.viewAppearToSuperView(getContentView(), animated);
					}
				}
				else if(viewController.isViewAppeared()){
					viewController.viewRemoveForSuperView(animated);
				}
			}
		}
		
		onSelectedControllerChanged();
	}
	
	public List<IViewController<T>> getViewControllers(){
		return _viewControllers;
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
		
		IViewController<T> topViewController = getSelectedViewController();
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

	protected void onSelectedControllerChanged(){
		
	}
	
	public void setConfig(Object config){
		super.setConfig(config);
		
		ArrayList<IViewController<T>> viewControllers = new ArrayList<IViewController<T>>();
		
		List<?> items = Value.listValueForKey(config, "items");
		
		for(Object item : items){
			
			String url = Value.stringValueForKey(item, "url");
			
			if(url != null){
				
				URL u = new URL(url);
				
				IViewController<T> viewController = getViewControllerContext().getViewController(u, "/");
				
				if(viewController != null){
					viewController.loadURL(u, "/", false);
					viewControllers.add(viewController);
				}
				
			}
			
		}
		
		setViewControllers(viewControllers,false);
	}
	
	@Override
	public boolean openURL(URL url,boolean animated){
		
		String scheme = getScheme();
		
		if(scheme == null){
			scheme = "tab";
		}
		
		if(scheme.equals(url.getScheme())){

			Log.d(Framework.TAG, url.toString());
			
			String alias = url.firstPathComponent("/");
			
			if(alias != null && _viewControllers !=null){
				
				for(IViewController<T> viewController : _viewControllers){
					if(alias.equals(viewController.getAlias())){
						setSelectedViewController(viewController,animated);
						break;
					}
				}
				
			}
			
	        return true;
			
		}
		
		return super.openURL(url, animated);
	}
	
	public IViewController<T> getTopController() {
		IViewController<T> controller = super.getTopController();
		if(controller == this){
			controller = getSelectedViewController();
			if(controller != null){
				return controller.getTopController();
			}
			return this;
		}
		return controller;
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		if(_viewControllers != null){
			for(IViewController<T> viewController : _viewControllers){
				viewController.onLowMemory();
			}
		}
	}
	
	@Override
	public void onServiceContextStart() {
		super.onServiceContextStart();
		
		if(_viewControllers != null){
			for(IViewController<T> viewController : _viewControllers){
				viewController.onServiceContextStart();
			}
		}
		
	}

	@Override
	public void onServiceContextStop() {

		if(_viewControllers != null){
			for(IViewController<T> viewController : _viewControllers){
				viewController.onServiceContextStop();
			}
		}
		
		super.onServiceContextStop();
	}
}
