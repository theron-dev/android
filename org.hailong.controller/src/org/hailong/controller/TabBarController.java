package org.hailong.controller;

import java.util.ArrayList;
import java.util.List;

import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.service.IServiceContext;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class TabBarController<T extends IServiceContext> extends
		Controller<T> {

	private List<Controller<T>> _controllers;
	private int _selectedIndex;
	
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
		
		FragmentManager fragmentManager = getFragmentManager();
		
		if(isVisible() && animated){
			
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
					
					getFragmentManager().beginTransaction()
						.replace(getContentViewId(), viewController)
						.commit();
					
				}
				
				Controller<T> viewController = getSelectedController();
				
				if(viewController !=null && !viewController.isAdded()){
					
					fragmentManager.beginTransaction()
					.replace(getContentViewId(), viewController)
					.commit();
				}
				
			}
			
			
		}
		else{
			
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			
			transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
			
			for(Controller<T> viewController : removeViewControllers){
				
				viewController.setParentController(null);
				
				if(viewController.isAdded()){
					
					transaction.remove(viewController);
					
				}
				
			}
			
			transaction.commit();
			
			Controller<T> topViewController = null;
			
			for(Controller<T> viewController : addViewControllers){
				viewController.setParentController(this);
				_controllers.add(viewController);
				topViewController = viewController;
			}
			
			if( isAdded() && topViewController != null){
				fragmentManager.beginTransaction()
				.replace(getContentViewId(), topViewController)
				.commit();
			}
		}
	}
	
	 @Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);  
       
        Controller<T> topViewController = getSelectedController();
        
        if(topViewController != null){
        	getFragmentManager().beginTransaction()
			.replace(getContentViewId(), topViewController)
			.commit();
        }
        
    } 

	
	public int getSelectedIndex(){
		return _selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex){
		if(_selectedIndex != selectedIndex){
			
			Controller<T> controller = getSelectedController();
			
			if(controller != null && controller.isAdded()){
				getFragmentManager().beginTransaction()
				.setTransition(FragmentTransaction.TRANSIT_NONE)
				.remove( controller)
				.commit();
			}
			
			_selectedIndex = selectedIndex;
			
			Controller<T> topViewController = getSelectedController();
	        
	        if(isAdded() && topViewController != null){
	        	getFragmentManager().beginTransaction()
				.setTransition(FragmentTransaction.TRANSIT_NONE)
				.replace(getContentViewId(), topViewController)
				.commit();
	        }
		}
	}
	
	public Controller<T> getSelectedController(){
		if(_controllers != null &&_selectedIndex >=0 && _selectedIndex < _controllers.size()){
			return _controllers.get(_selectedIndex);
		}
		return null;
	}
	
	@Override
	public boolean openURL(URL url,boolean animated){
		
		String scheme = getScheme();
		
		if(scheme == null){
			scheme = "tab";
		}
		
		if(scheme.equals(url.getScheme())){

			String alias = url.firstPathComponent(getBasePath());
			int index = 0;
			
			if(_controllers != null){
				for(Controller<T> controller : _controllers){
					if(controller.getAlias().equals(alias)){
						break;
					}
					index ++;
				}
			}

			setSelectedIndex(index);
		}
		
		return super.openURL(url, animated);
	}
	
	public int getContentViewId(){
		return R.id.tabBarContentView;
	}
	
	public void setConfig(Object config){
		super.setConfig(config);
		
		List<?> items = Value.listValueForKey(config, "items");
		
		List<Controller<T>> controllers = new ArrayList<Controller<T>>(4);
		
		if(items != null){
			
			for(Object item : items){
				
				String url = Value.stringValueForKey(item, "url");
				
				if(url != null){
					
					Controller<T> controller = getControllerContext().getController(new URL(url), "/");
					
					if(controller != null){
						controllers.add(controller);
					}
					
				}
				
			}
			
		}
		
		setControllers(controllers,false);
		
	}
}
