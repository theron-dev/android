package org.hailong.speex;

public class Speex {

	public static final int MODE_NB = 0;
	public static final int MODE_WB = 1;
	public static final int MODE_UWB = 2;
	public static final int HEADER_SIZE = 18;
	
	static {
		System.loadLibrary("speex");
	}
	
	private long _ptr;
	
	private Speex(long ptr){
		_ptr = ptr;
	}
	
	public Speex(int mode){
		_ptr = alloc(mode);
	}
	
	@Override
	protected void finalize() throws Throwable{
		dealloc(_ptr);
		super.finalize();
	}
	
	public int getMode(){
		return getMode(_ptr);
	}
	
	public int getFrameSize(){
		return frameSize(_ptr);
	}
	
	public int getFrameBytes(){
		return frameBytes(_ptr);
	}
	
	public int getSamplingRate(){
		return samplingRate(_ptr);
	}
	
	public void setSamplingRate(int samplingRate){
		setSamplingRate(_ptr,samplingRate);
	}
	
	public int getQuality(){
		return quality(_ptr);
	}
	
	// 1~ 10, default 8
	public void setQuality(int quality){
		setQuality(_ptr,quality);
	}
	
	public int getEncodedSize(){
		return bitSize(_ptr);
	}
	
	public int encode(byte[] inBytes,byte[] outBytes,byte[] echoBytes){
		return encode(_ptr,inBytes,outBytes,echoBytes);
	}
	
	public int decode(byte[] inBytes,int length,byte[] outBytes){
		return decode(_ptr,inBytes,length,outBytes);
	}
	
	public static Speex newSpeexByHeader(byte[] bytes,int length){
		long ptr = allocByHeader(bytes,length);
		if(ptr != 0){
			return new Speex(ptr); 
		}
		return null;
	}
	
	public int getHeader(byte[] buffer,int length){
		return getHeader(_ptr,buffer,length);
	}
	
	private native static long alloc(int mode);
	private native static void dealloc(long ptr);
	private native static int frameSize(long ptr);
	private native static int frameBytes(long ptr);
	private native static int samplingRate(long ptr);
	private native static void setSamplingRate(long ptr,int samplingRate);
	private native static int quality(long ptr);
	private native static void setQuality(long ptr,int quality);
	private native static int bitSize(long ptr);
	private native static int encode(long ptr,byte[] inBytes,byte[] outBytes,byte[] echoBytes);
	private native static int decode(long ptr,byte[] inBytes,int length,byte[] outBytes);
	private native static int getHeader(long ptr,byte[] buffer,int length);
	private native static long allocByHeader(byte[] buffer,int length);
	private native static int getMode(long ptr);
}
