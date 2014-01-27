package org.hailong.speex;

import java.io.IOException;

public interface SpeexOutput {

	public void writeFrame(byte[] frameBytes,byte[] echoBytes) throws IOException;
	
	public void close() throws IOException;
}
