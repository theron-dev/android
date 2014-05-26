package org.hailong.controller.sample;

import org.hailong.controller.AbstractControllerActivity;

import android.content.Intent;
import android.os.Bundle;

public class SampleActivity extends AbstractControllerActivity<ISampleContext> {

	@Override
	protected Intent getServiceContextIntent() {
		return new Intent(this,SampleService.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	
	}
}
