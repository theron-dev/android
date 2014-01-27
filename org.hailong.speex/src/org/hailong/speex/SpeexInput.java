package org.hailong.speex;

import java.io.IOException;

public interface SpeexInput {

	public int readFrame(byte[] outBytes) throws IOException;
	
	public void close() throws IOException;
}
