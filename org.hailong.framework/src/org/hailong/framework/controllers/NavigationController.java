package org.hailong.framework.controllers;

import java.util.ArrayList;
import java.util.List;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.R;
import org.hailong.framework.URL;
import org.hailong.framework.views.Animation;

import android.util.Log;
import android.view.ViewGroup;

public class NavigationController<T extends IServiceContext> extends ViewController<T>  {

	private List<IViewController<T>> _viewControllers;
	private ViewGroup _contentView;
	
	public NavigationController(IViewControllerContext<T> activity) {
		super(activity, 0);
	}

	public NavigationController(IViewControllerContext<T> activity, int viewLayout) {
		super(activity, viewLayout);
	}
	
	public void setViewControllers(List<IViewController<T>> viewControllers){
		setViewControllers(viewControllers, false);
	}
	
	public void setViewControllers(List<IViewController<T>> viewControllers,boolean animated){
		
		if(isAnimation()){
			return;
		}
		
		ArrayList<IViewController<T>> removeViewControllers = new ArrayList<IViewController<T>>(4);
		ArrayList<IViewController<T>> addViewControllers = new ArrayList<IViewController<T>>(4);
		
		if(_viewControllers != null){
			
			if(viewControllers != null){
				
				int size = Math.min(_viewControllers.size(), viewControllers.size());
				int i =0;
				
				while(i < size){
					
					if(_viewControllers.get(i) != viewControllers.get(i)){
						break;
					}
					
					i ++;
				}
				
				if(i < size){
					
					int ii = i;
					
					while(ii<_viewControllers.size()){
						removeViewControllers.add(_viewControllers.get(ii));
						_viewControllers.remove(ii);
					}
					
					ii = i;
					
					for(;ii<viewControllers.size();ii++){
						addViewControllers.add(viewControllers.get(ii));
					}
				}
				else{
					for(;i<viewControllers.size();i++){
						addViewControllers.add(viewControllers.get(i));
					}
				}
			}
			else{
				for(IViewController<T> viewController : _viewControllers){
					removeViewControllers.add(viewController);
				}
				_viewControllers.clear();
			}
		}
		else if(viewControllers != null){
			for(IViewController<T> viewController : viewControllers){
				addViewControllers.add(viewController);
			}
		}
		
		if(_viewControllers == null){
			_viewControllers = new ArrayList<IViewController<T>>(4);
		}
		
		if(isViewAppeared() && animated){
			
			int addSize = addViewControllers.size();
			int removeSize = removeViewControllers.size();
			int i;
			
			if(addSize + removeSize > 0){
				
				final ViewGroup contentView = getContentView();
				
				Animation anim = new Animation();
				
				anim.setDuration(300);
				anim.setListener(new Animation.Listener(){

					public void onStart() {
						// TODO Auto-generated method stub
						
					}

					public void onEnd() {
						contentView.setEnabled(true);
						setAnimation(false);
					}});
				
				contentView.setEnabled(false);
				setAnimation(true);
				
				for(i=0; i< removeSize;i++){
					
					IViewController<T> viewController = removeViewControllers.get(i);
					
					viewController.setParentController(null);

					if(viewController.isViewAppeared()){
					
						anim.translate(viewController.getView(), contentView.getWidth(), 0, 0, 0);
					}
				}
				
				for(i=0; i< addSize -1;i++){
					
					IViewController<T> viewController = addViewControllers.get(i);
					viewController.setParentController(this);
					
					_viewControllers.add(viewController);
					
				}
				
				if(i < addSize){
					
					for(IViewController<T> viewController : _viewControllers){
						
						if(viewController.isViewAppeared()){
							anim.translate(viewController.getView(), 0, 0, - contentView.getWidth(), 0);
						}
					}
					
					IViewController<T> viewController = addViewControllers.get(i);
		
					viewController.setParentController(this);
					
					_viewControllers.add(viewController);
					
					viewController.viewAppearToSuperView(contentView, animated);
					
					anim.translate(viewController.getView(),  contentView.getWidth(), 0, 0, 0);
					
				}

				anim.submit();
			}
			
			
		}
		else{
			
			for(IViewController<T> viewController : removeViewControllers){
				viewController.setParentController(null);
				if(viewController.isViewAppeared()){
					viewController.viewRemoveForSuperView(false);
				}
			}
			
			IViewController<T> topViewController = null;
			
			for(IViewController<T> viewController : addViewControllers){
				viewController.setParentController(this);
				_viewControllers.add(viewController);
				topViewController = viewController;
			}
			
			if(isViewAppeared() && topViewController != null){
				topViewController.viewAppearToSuperView(getContentView(), animated);
			}
		}
		
		onTopControllerChanged();
	}
	
	public List<IViewController<T>> getViewControllers(){
		return _viewControllers;
	}
	
	public IViewController<T> getTopViewController(){
		if(_viewControllers != null && _viewControllers.size() >0){
			return _viewControllers.get(_viewControllers.size() - 1);
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
	}
	
	@Override
	protected void didViewUnLoaded(){
		super.didViewUnLoaded();
		
		_contentView = null;
	}
	
	@Override
	public void viewWillAppear(boolean animated){
		super.viewWillAppear(animated);
		
		IViewController<T> topViewController = getTopViewController();
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
		
		ArrayList<IViewController<T>> viewControllers = new ArrayList<IViewController<T>>(4);
		
		basePath = URL.stringAddPathComponent(basePath, getAlias());
		
		String alias = url.firstPathComponent(basePath);
		
		int index = 0;
		
		while(alias != null){
			
			if(_viewControllers !=null &&  index >= 0 && index < _viewControllers.size()){
				
				IViewController<T> viewController = _viewControllers.get(index);
				
				if(alias.equals(viewController.getAlias())){
					basePath = viewController.loadURL(url, basePath, animated);
					viewControllers.add(viewController);
				}
				else{
					index = -1;
				}
			}
			else{
				IViewController<T> viewController = getViewControllerContext().getViewController(url, basePath);
				if(viewController != null){
					basePath = viewController.loadURL(url, basePath, animated);
					viewControllers.add(viewController);
				}
				else{
					break;
				}
			}
			alias = url.firstPathComponent(basePath);
		}
		
		setViewControllers(viewControllers, animated);
		
		return basePath;

	}
	
	@Override
	public boolean openURL(URL url,boolean animated){
		
		String scheme = getScheme();
		
		if(scheme == null){
			scheme = "nav";
		}
		
		if(scheme.equals(url.getScheme())){

			String alias = url.firstPathComponent(URL.stringAddPathComponent(getBasePath(), getAlias()));
			
			if(alias != null && alias.length() >0){
			
				Log.d(Framework.TAG, url.toString());
				
				loadURL(url, getBasePath(), animated);
			
				return true;
			}

		}
		
		return super.openURL(url, animated);
	}
}
