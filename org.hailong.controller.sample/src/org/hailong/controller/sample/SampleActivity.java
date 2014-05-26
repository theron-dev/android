package org.hailong.controller.sample;

import org.hailong.controller.AbstractControllerActivity;
import android.content.Intent;

public class SampleActivity extends AbstractControllerActivity<ISampleContext> {

	@Override
	protected Intent getServiceContextIntent() {
		return new Intent(this,SampleService.class);
	}

}
