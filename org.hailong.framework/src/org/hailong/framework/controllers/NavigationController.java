package org.hailong.framework.controllers;

import java.util.ArrayList;
import java.util.List;

import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.R;
import org.hailong.framework.URL;
import org.hailong.framework.views.Animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.util.Log;
import android.view.ViewGroup;

public class NavigationController<T extends IServiceContext> extends ViewController<T>  {

	private List<IViewController<T>> _viewControllers;
	private ViewGroup _contentView;
	
	public NavigationController(IViewControllerContext<T> activity) {
		super(activity, null);
	}

	public NavigationController(IViewControllerContext<T> activity, String viewLayout) {
		super(activity, viewLayout);
	}
	
	public void setViewControllers(List<IViewController<T>> viewControllers){
		setViewControllers(viewControllers, false);
	}
	
	public void setViewControllers(List<IViewController<T>> viewControllers,boolean animated){
		
		if(isAnimation() || getViewControllerContext().isIdleTimerDisabled()){
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
				final ArrayList<IViewController<T>> removeViews = new ArrayList<IViewController<T>>(4);
				
				Animation anim = new Animation();
				
				anim.setDuration(300);
				anim.setListener(new AnimatorListener(){

					public void onEnd() {
						
						for(IViewController<T> viewController : removeViews){
							if(viewController.isViewAppeared()){
								viewController.viewRemoveForSuperView(false);
							}
						}
						
						contentView.setEnabled(true);
						setAnimation(false);
						getViewControllerContext().setIdleTimerDisabled(false);
					}

					public void onAnimationCancel(Animator arg0) {
						onEnd();
					}

					public void onAnimationEnd(Animator arg0) {
						onEnd();
					}

					public void onAnimationRepeat(Animator arg0) {
						// TODO Auto-generated method stub
						
					}

					public void onAnimationStart(Animator arg0) {
						// TODO Auto-generated method stub
						
					}});
				
				contentView.setEnabled(false);
				setAnimation(true);
				getViewControllerContext().setIdleTimerDisabled(true);
				
				for(i=0; i< removeSize;i++){
					
					IViewController<T> viewController = removeViewControllers.get(i);
					
					viewController.setParentController(null);

					if(viewController.isViewAppeared()){
					
						anim.translate(viewController.getView(), 0, contentView.getWidth(), 0, 0);
						
						removeViews.add(viewController);
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
							
							anim.translate(viewController.getView(), 0, - contentView.getWidth(), 0 , 0);
							
							removeViews.add(viewController);
						}
					}
					
					IViewController<T> viewController = addViewControllers.get(i);
		
					viewController.setParentController(this);
					
					_viewControllers.add(viewController);
					
					viewController.viewAppearToSuperView(contentView, animated);
					
					anim.translate(viewController.getView(),  contentView.getWidth(), 0, 0, 0);
					
				}
				
				IViewController<T> viewController = getTopViewController();
				
				if(viewController !=null && !viewController.isViewAppeared()){
					viewController.viewAppearToSuperView(contentView, animated,true);
					anim.translate(viewController.getView(),  -contentView.getWidth(), 0, 0, 0);
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
					index ++;
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
	
	@Override
	public boolean onPressBack(){
		
		IViewController<T> controller = getModalViewController();
		
		if(controller != null){
			return true;
		}
		
		if(_viewControllers != null && _viewControllers.size() > 1){
			return true;
		}
		
		controller = getParentController();
		
		if(controller != null){
			return controller.onPressBack();
		}
		
		return super.onPressBack();
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
