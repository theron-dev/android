package org.hailong.controller.sample;

import org.hailong.controller.Controller;
import org.hailong.core.URL;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SampleController extends Controller<ISampleContext> {

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		Button button = (Button) getView().findViewById(R.id.button1);
        
        if(button != null){
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				openURL(new URL("home/home",getURL()), true);
				
			}
		});
        }
	}
}
