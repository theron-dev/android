package org.hailong.framework.controllers;

import java.util.Map;
import org.hailong.framework.Controller;
import org.hailong.framework.Framework;
import org.hailong.framework.IServiceContext;
import org.hailong.framework.URL;
import org.hailong.framework.tasks.IImageTask;
import org.hailong.framework.tasks.ILocalResourceTask;
import org.hailong.framework.value.Value;
import org.hailong.framework.views.ViewLayout;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class ViewController<T extends IServiceContext> extends Controller<T> implements IViewController<T> {

	private View _view;
	private IViewController<T> _parentController;
	private Object _config;
	private String _title;
	private Handler _handler;
	private boolean _animation;
	private String _scheme;
	private URL _url;
	private String _basePath;
	private String _alias;
	private ViewController<T> _modalViewController;
	private ViewLayout _viewLayout;
	
	public ViewController(IViewControllerContext<T> context,String viewLayout) {
		super(context);
		_viewLayout = new ViewLayout(getContext(), viewLayout);
		_handler = new Handler();
		_animation = false;
	}

	@Override
	public void onServiceContextStart() {
	
		if(_modalViewController != null){
			_modalViewController.onServiceContextStart();
		}
	
		if(isViewLoaded()){
			didViewLoadedContextStart();
		}
	}

	@Override
	public void onServiceContextStop() {

		if(_modalViewController != null){
			_modalViewController.onServiceContextStop();
		}
		
	}
	
	public void onLowMemory(){
		if(_view != null && _view.getParent() == null){
			didViewUnLoaded();
			_view = null;
		}
		if(_modalViewController != null){
			_modalViewController.onLowMemory();
		}
	}
	
	protected void didViewLoadedContextStart(){
		
	}

	protected void didViewLoaded(){

	
	}
	
	protected void didViewUnLoaded(){
		
	}
	
	public void viewWillAppear(boolean animated){

		Activity activity = getActivity();
		
		if(activity != null){
			
			int orientation = getControllerOrientation();
			if(orientation != activity.getRequestedOrientation()){
				activity.setRequestedOrientation(orientation);
			}
			
		}
		
	}
	
	public void viewDidAppear(boolean animated){
	}
	
	public void viewWillDisappear(boolean animated){
		
	}
	
	public void viewDidDisappear(boolean animated){

	}

	protected void loadView() {
		if(_viewLayout == null){
			_view = new FrameLayout(getContext());
		}
		else{
			
			_view = _viewLayout.getView();
			
			if(_view == null){
				_view = new FrameLayout(getContext());
			}
		}
		
		didViewLoaded();
		
		if(getServiceContext() != null){
			didViewLoadedContextStart();
		}
		
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
		return _view != null && _view.getParent() != null;
	}
	
	public void viewAppearToSuperView(ViewGroup superView,boolean animated){
		viewAppearToSuperView(superView,animated,false); 
	}

	public void viewAppearToSuperView(ViewGroup superView,boolean animated,boolean toBackground){
		View view = getView();
		viewWillAppear(animated);
		if(toBackground){
			superView.addView(view, 0);
		}
		else{
			superView.addView(view);
		}
		viewWillDisappear(animated);
	}
	
	public void viewRemoveForSuperView(boolean animated){
		if(_view != null && _view.getParent() !=null){
			viewWillDisappear(animated);
			((ViewGroup)_view.getParent()).removeView(_view);
			viewDidDisappear(animated);
		}
	}
	
	public Object getConfig(){
		return _config;
	}
	
	public void setConfig(Object config){
		_config = config;
		_title = Value.stringValueForKey(config, "title");
	}
	
	
	public boolean isDisplaced(){
		if(_config != null && _config instanceof Map){
			boolean disabledDisplaced = Value.booleanValueForKey(_config,"disabledDisplaced");
			if(disabledDisplaced){
				return false;
			}
		}
		return getParentController() == null;
	}
	
	public String getTitle(){
		return _title;
	}
	
	public void setTitle(String title){
		_title = title;
	}
	
	public boolean onPressBack(){
		
		IViewController<T> controller = getModalViewController();
		
		if(controller != null){
			return true;
		}
		
		controller = getParentController();
		
		if(controller != null){
			
			return controller.onPressBack();
		}
		
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if (keyCode == KeyEvent.KEYCODE_BACK 
	    		&& event.getRepeatCount() == 0 ) {
	    	
	    	if(onPressBack()){
	    		_handler.post(new Runnable(){

					public void run() {
						openURL(new URL(".", getURL()),true);
					}});
	    		
	    	}
	    	return true;
	    }

	    return false;
	}
	
	public ViewController<T> getModalViewController(){
		return _modalViewController;
	}
	
	public void presentModalViewController(ViewController<T> viewController){
		presentModalViewController(viewController,true);
	}
	
	public void presentModalViewController(ViewController<T> viewController,boolean animated){
		
		if( _animation || viewController == null || _modalViewController != null 
				|| !isViewAppeared() || getViewControllerContext().isIdleTimerDisabled()){
			return;
		}
		
		_modalViewController = viewController;
		_modalViewController.setParentController(this);
		
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
							_animation = false;
							getViewControllerContext().setIdleTimerDisabled(false);
						}
						
					});
				}
			});
			_animation = true;
			contentView.setEnabled(false);
			getViewControllerContext().setIdleTimerDisabled(true);
			_modalViewController.getView().startAnimation(animation);
			
		}

	}
	
	public void dismissModalViewController(boolean animated){
		
		ViewGroup contentView = (ViewGroup)getView().getParent();
		
		if(animated){
			
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
							ViewGroup contentView = (ViewGroup)getView().getParent();
							contentView.setEnabled(true);
							_animation = false;
							viewRemoveForSuperView(true);
						}
						
					});
				}
			});
			
			_animation = true;
			contentView.setEnabled(false);
			getView().startAnimation(animation);
			
		}
		else{
			viewRemoveForSuperView(animated);
		}

	}
	
	public int getControllerOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	}
	
	public void onOrientationChanged(int orientation){

	}
	
	public boolean isAnimation(){
		return _animation;
	}
	
	public void setAnimation(boolean animation){
		_animation = animation;
	}

	public IViewController<T> getParentController() {
		return _parentController;
	}

	public void setParentController(IViewController<T> parentController) {
		_parentController = parentController;
	}

	public IViewController<T> getTopController() {
		if(_modalViewController != null){
			return _modalViewController.getTopController();
		}
		return this;
	}

	public String getAlias() {
		return _alias;
	}

	public void setAlias(String alias) {
		_alias = alias;
	}

	public String getBasePath() {
		return _basePath;
	}

	public void setBasePath(String basePath) {
		_basePath = basePath;
	}

	public URL getURL() {
		return _url;
	}

	public void setURL(URL url) {
		_url = url;
	}

	public String getScheme() {
		return _scheme;
	}

	public void setScheme(String scheme) {
		_scheme = scheme;
	}
	
	public boolean openURL(URL url, boolean animated) {
		
		String scheme = url.getScheme();
		
		if("present".equals(scheme)){
	        
			String alias = url.firstPathComponent("/");
			
			if(alias != null&& alias.length() >0){
				
				String host = url.getHost();
				
				if(host  != null && host.length() >0){
					
					if(host.equals(this.getScheme())){
						
						IViewController<T> topController = getTopController();
						
						if(topController instanceof ViewController){
							ViewController<T> parentController = (ViewController<T>) topController;
							
							IViewController<T> viewController = getViewControllerContext().getViewController(url, "/");
							
							if(viewController != null && viewController instanceof ViewController){
								
								Log.d(Framework.TAG, "openURL "+ url.toString());
								
								parentController.presentModalViewController((ViewController<T>)viewController, animated);
								
								return true;
							}
							
						}
						
					}
					
				}
				else{
					
					IViewController<T> viewController = getViewControllerContext().getViewController(url, "/");
					
					if(viewController != null && viewController instanceof ViewController){
						
						Log.d(Framework.TAG, "openURL "+ url.toString());
						
						presentModalViewController((ViewController<T>)viewController, animated);
						
						return true;
					}
					
				}
			}
			else{
				
				Log.d(Framework.TAG, "openURL "+ url.toString());
				
				dismissModalViewController(animated);
				
				return true;
			}
			
	    }

		return _parentController != null ? _parentController.openURL(url, animated) : false;
	}

	public String loadURL(URL url, String basePath, boolean animated) {
		if(basePath != null){
			if(basePath.endsWith("/")){
				return basePath + getAlias();
			}
			return basePath + "/" + getAlias();
		}
		return null;
	}

	public IViewControllerContext<T> getViewControllerContext() {
		if(_activity instanceof IViewControllerContext){
			return (IViewControllerContext<T>) _activity;
		}
		return null;
	}
	
	public Handler getHandler(){
		return _handler;
	}
	

	public void downloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					getServiceContext().handle(IImageTask.class, imageTask, 0);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}
		
		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				downloadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
		
	}

	public void loadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isNeedDownload() && !imageTask.isLoading()){
				
				try {
					
					getServiceContext().handle(ILocalResourceTask.class, imageTask, 0);
					
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				loadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
	}

	public void cancelDownloadImagesForView(View view){
		
		if(view instanceof IImageTask){
			
			IImageTask imageTask = (IImageTask) view;
			
			if(imageTask.isLoading()){
				
				try {
					getServiceContext().cancelHandle(IImageTask.class, imageTask);
				} catch (Exception e) {
					Log.d(Framework.TAG, Log.getStackTraceString(e));
				}
				
			}
		}

		if(view instanceof ViewGroup){
			
			ViewGroup viewGroup = (ViewGroup) view;
			
			int c = viewGroup.getChildCount();
			
			for(int i=0;i<c;i++){
				
				cancelDownloadImagesForView(viewGroup.getChildAt(i));
				
			}
			
		}
	}
}
