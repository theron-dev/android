package org.hailong.controller;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.URL;
import org.hailong.service.IServiceContext;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class NavigationController<T extends IServiceContext> extends
		Controller<T> {

	private List<Controller<T>> _controllers;
	
	public void setControllers(List<Controller<T>> controllers){
		setControllers(controllers, false);
	}
	
	public void setControllers(List<Controller<T>> controllers,boolean animated){
		
		ArrayList<Controller<T>> removeViewControllers = new ArrayList<Controller<T>>(4);
		ArrayList<Controller<T>> addViewControllers = new ArrayList<Controller<T>>(4);
		
		if(_controllers != null){
			
			if(controllers != null){
				
				int size = Math.min(_controllers.size(), controllers.size());
				int i =0;
				
				while(i < size){
					
					if(_controllers.get(i) != controllers.get(i)){
						break;
					}
					
					i ++;
				}
				
				int ii = i;
				
				while(ii<_controllers.size()){
					removeViewControllers.add(_controllers.get(ii));
					_controllers.remove(ii);
				}
				
				ii = i;
				
				for(;ii<controllers.size();ii++){
					addViewControllers.add(controllers.get(ii));
				}
				
			}
			else{
				for(Controller<T> viewController : _controllers){
					removeViewControllers.add(viewController);
				}
				_controllers.clear();
			}
		}
		else if(controllers != null){
			for(Controller<T> viewController : controllers){
				addViewControllers.add(viewController);
			}
		}
		
		if(_controllers == null){
			_controllers = new ArrayList<Controller<T>>(4);
		}
		
		if(isVisible() && animated){
			
			FragmentManager fragmentManager = getChildFragmentManager();
			
			int addSize = addViewControllers.size();
			int removeSize = removeViewControllers.size();
			int i;
			
			if(addSize + removeSize > 0){
			
				for(i=0; i< removeSize;i++){
					
					Controller<T> viewController = removeViewControllers.get(i);
					
					viewController.setParentController(null);

				}
				
				for(i=0; i< addSize -1;i++){
					
					Controller<T> viewController = addViewControllers.get(i);
					
					viewController.setParentController(this);
					
					_controllers.add(viewController);
					
				}

				if(i < addSize){
					
					Controller<T> viewController = addViewControllers.get(i);
		
					viewController.setParentController(this);
					
					_controllers.add(viewController);
					
					fragmentManager.beginTransaction()
						.setCustomAnimations(R.animator.in_left, R.animator.out_left)
						.replace(R.id.contentView, viewController)
						.commit();
					
				}
				
				Controller<T> viewController = getTopViewController();
				
				if(viewController !=null && !viewController.isAdded()){
					
					fragmentManager.beginTransaction()
					.setCustomAnimations(R.animator.in_right, R.animator.out_right)
					.replace(R.id.contentView, viewController)
					.commit();
				}
				
			}
			
			
		}
		else if(isAdded()){
			
			FragmentManager fragmentManager = getChildFragmentManager();
			
			for(Controller<T> viewController : removeViewControllers){
				viewController.setParentController(null);
			}
			
			Controller<T> topViewController = null;
			
			for(Controller<T> viewController : addViewControllers){
				viewController.setParentController(this);
				_controllers.add(viewController);
				topViewController = viewController;
			}
			
			if(  topViewController != null){
				fragmentManager.beginTransaction()
				.replace(R.id.contentView, topViewController)
				.commit();
			}
		}
		else {
			
			for(Controller<T> viewController : removeViewControllers){
				viewController.setParentController(null);
			}
			
			for(Controller<T> viewController : addViewControllers){
				viewController.setParentController(this);
				_controllers.add(viewController);
			}
			
		}
		
		onTopControllerChanged();
	}

	
	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

        
		Controller<T> topViewController = getTopViewController();
        
        if(topViewController != null){
        	
        	FragmentManager fragmentManager = getChildFragmentManager();
        	
        	fragmentManager.beginTransaction()
			.replace(R.id.contentView, topViewController)
			.commit();
        	
        }
    } 
	
	protected void onTopControllerChanged(){
		
	}
	
	public Controller<T> getTopViewController(){
		if(_controllers != null && _controllers.size() > 0){
			return _controllers.get(_controllers.size() - 1);
		}
		return null;
	}
	
	@Override
	public String loadURL(URL url,String basePath,boolean animated){
		
		ArrayList<Controller<T>> viewControllers = new ArrayList<Controller<T>>(4);
		
		basePath = URL.stringAddPathComponent(basePath, getAlias());
		
		String alias = url.firstPathComponent(basePath);
		
		int index = 0;
		
		while(alias != null){
			
			if(_controllers !=null &&  index >= 0 && index < _controllers.size()){
				
				Controller<T> viewController = _controllers.get(index);
				
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
				
				Controller<T> viewController = getControllerContext().getController(url, basePath);
				
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
		
		setControllers(viewControllers, animated);
		
		return basePath;

	}
	
	@Override
	public boolean openURL(URL url,boolean animated){
		
		String scheme = getScheme();
		
		if(scheme == null){
			scheme = "nav";
		}
		
		if(scheme.equals(url.getScheme())){

			Log.d(C.TAG, url.toString());
			
			setURL(url);
			
			loadURL(url, getBasePath(), animated);
			
			return true;

		}
		
		return super.openURL(url, animated);
	}

	@Override
	public boolean onPressBack(){
		
		if(_controllers != null && _controllers.size() > 1){
			return true;
		}
		
		Controller<T> controller = getParentController();
		
		if(controller != null){
			return controller.onPressBack();
		}
		
		return super.onPressBack();
	}
	
	@SuppressWarnings("unchecked")
	public Controller<T>[] getControllers(){
		if(_controllers != null){
			return _controllers.toArray( new Controller[_controllers.size()]);
		}
		return new Controller[0];
	}
}
