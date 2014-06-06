package org.hailong.app.tasks;

import org.hailong.dom.DOMDocument;

public interface IDocumentTaskListener {

	public void onDocumentTaskLoadedFromCached(IDocumentTask documentTask, DOMDocument document);
	
	public void onDocumentTaskLoaded(IDocumentTask documentTask, DOMDocument document);
	
	public void onDocumentTaskException(IDocumentTask documentTask, Exception exception);
	
}
