package org.hailong.controller;

import java.util.ArrayList;
import java.util.List;
import org.hailong.core.URL;
import org.hailong.core.Value;
import org.hailong.service.IServiceContext;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class TabBarController<T extends IServiceContext> extends
		Controller<T> {

	private List<Controller<T>> _controllers;
	private int _selectedIndex;
	
	public void setControllers(List<Controller<T>> controllers){
		setControllers(controllers, false);
	}
	
	public void setControllers(List<Controller<T>> controllers,boolean animated){
		
		if(getControllerContext().isIdleTimerDisabled()){
			return;
		}
		
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

		if(isAdded()){
			
			FragmentManager fragmentManager = getChildFragmentManager();
			
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			
			for(Controller<T> viewController : removeViewControllers){
				
				viewController.setParentController(null);
			
				if(viewController.isAdded()){
					transaction.remove(viewController);
				}
				
			}
			
			for(Controller<T> viewController : addViewControllers){
				viewController.setParentController(this);
				_controllers.add(viewController);
			}
			
			Controller<T> topViewController = getSelectedController();
			
			for(Controller<T> viewController : _controllers){
				if(viewController == topViewController){
					if(viewController.isAdded()){
						transaction.show(viewController);
					}
					else{
						transaction.add(R.id.contentView, viewController);
					}
				}
				else if(viewController.isAdded()){
					transaction.hide(viewController);
				}
			}
			
			transaction.commit();
			
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
		
	}
	
	@Override  
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		List<?> items = Value.listValueForKey(getConfig(), "items");
			
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
	
	@Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  

        Controller<T> topViewController = getSelectedController();
        
        if(topViewController != null){
        	
        	FragmentManager fragmentManager = getChildFragmentManager();
        	
        	fragmentManager.beginTransaction()
			.replace(R.id.contentView, topViewController)
			.commit();
        }
        
    } 

	
	public int getSelectedIndex(){
		return _selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex){
		if(_selectedIndex != selectedIndex){
			
			_selectedIndex = selectedIndex;
			
			if(isAdded()){
				
				FragmentManager fragmentManager = getChildFragmentManager();
				
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				
				Controller<T> topViewController = getSelectedController();
				
				for(Controller<T> viewController : _controllers){
					if(viewController == topViewController){
						if(viewController.isAdded()){
							transaction.show(viewController);
						}
						else{
							transaction.add(R.id.contentView, viewController);
						}
					}
					else if(viewController.isAdded()){
						transaction.hide(viewController);
					}
				}
				
				transaction.commit();
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
	
	
	@SuppressWarnings("unchecked")
	public Controller<T>[] getControllers(){
		if(_controllers != null){
			return _controllers.toArray( new Controller[_controllers.size()]);
		}
		return new Controller[0];
	}
}
