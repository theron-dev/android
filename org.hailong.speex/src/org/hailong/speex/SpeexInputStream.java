package org.hailong.speex;

import java.io.IOException;
import java.io.InputStream;

public class SpeexInputStream implements SpeexInput {

	private Speex _speex;
	private InputStream _inputStream;
	private byte[] _decodeBuffer;
	
	public SpeexInputStream(InputStream inputStream) throws IOException{

		byte[] h = new byte[Speex.HEADER_SIZE];
		
		int len = inputStream.read(h);
		
		if(len != Speex.HEADER_SIZE){
			throw new IOException("not read speex header");
		}
		
		_speex = Speex.newSpeexByHeader(h, len);
		
		if(_speex == null){
			throw new IOException("not read speex header");
		}
		
		_inputStream = inputStream;
		
	}
	
	public Speex getSpeex(){
		return _speex;
	}
	
	public InputStream getInputStream(){
		return _inputStream;
	}

	@Override
	public int readFrame( byte[] outBytes)
			throws IOException {

		int len = _speex.getEncodedSize();
		
		if(len == 0){
			
			byte[] lb = new byte[2];
			
			if(0 == _inputStream.read(lb)){
				return 0;
			}
			
			len = (lb[0] << 8) | lb[1];
		}
		
		if(_decodeBuffer == null){
			_decodeBuffer = new byte[_speex.getFrameBytes()];
		}
		
		_inputStream.read(_decodeBuffer,0,len);
		
		return _speex.decode(_decodeBuffer, len, outBytes);
	}

	@Override
	public void close() throws IOException {
		_inputStream.close();
	}

}
