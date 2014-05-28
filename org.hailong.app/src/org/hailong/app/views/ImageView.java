package org.hailong.app.views;

import org.hailong.dom.DOMElement;
import org.hailong.dom.IDOMView;
import org.haiong.service.views.ImageViewTask;
import android.content.Context;

public class ImageView extends ImageViewTask implements IDOMView {

	
	public ImageView(Context context) {
		super(context);

	}

	@Override
	public void setElement(DOMElement element) {
		setImageUrl(element.getAttributeValue("src"));
	}

	@Override
	public void onElementAttributeChanged(DOMElement element, String name,String value){
		if("src".equals(name)){
			setImageUrl(value);
		}
	}
}
