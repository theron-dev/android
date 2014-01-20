package org.hailong.controller;

import org.hailong.framework.controllers.AbstractViewControllerActivity;

import android.content.Intent;

public class DemoActivity extends AbstractViewControllerActivity<DemoContext> {

	@Override
	protected Intent getServiceContextIntent() {
		return new Intent(this,DemoService.class);
	}

}