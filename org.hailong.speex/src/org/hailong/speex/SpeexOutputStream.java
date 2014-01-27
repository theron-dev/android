package org.hailong.speex;

import java.io.IOException;
import java.io.OutputStream;

public class SpeexOutputStream implements SpeexOutput {

	private Speex _speex;
	private OutputStream _outputStream;
	private byte[] _encodeBuffer;
	
	public SpeexOutputStream(Speex speex,OutputStream outputStream) throws IOException{
		_speex = speex;
		_outputStream = outputStream;
		
		byte[] h =new byte[Speex.HEADER_SIZE];
		
		int len = speex.getHeader(h, Speex.HEADER_SIZE);
		
		_outputStream.write(h,0,len);
	}
	
	public Speex getSpeex(){
		return _speex;
	}
	
	public OutputStream getOutputStream(){
		return _outputStream;
	}

	@Override
	public void writeFrame(byte[] frameBytes, byte[] echoBytes)
			throws IOException {
		
		if(_encodeBuffer == null){
			_encodeBuffer = new byte[_speex.getFrameBytes()];
		}
		
		int length = _speex.encode(frameBytes, _encodeBuffer, echoBytes);
		
		_outputStream.write(_encodeBuffer, 0, length);
		
	}

	@Override
	public void close() throws IOException {
		_outputStream.close();
	}

}
