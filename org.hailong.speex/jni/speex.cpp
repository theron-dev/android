#include <jni.h>
#include <android/log.h>
#include "org_hailong_speex_Speex.h"

#include "speex/speex.h"
#include "speex/speex_preprocess.h"
#include "speex/speex_echo.h"
#include <string.h>
#include "speex/speex_header.h"

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved){
	__android_log_print(ANDROID_LOG_DEBUG,"hailong","speex JNI_OnLoad");
	return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved){
	__android_log_print(ANDROID_LOG_DEBUG,"hailong","speex JNI_OnUnload");
}

typedef struct _Speex {

	void * encodeState;
	void * decodeState;
	SpeexPreprocessState * preprocessState;
	SpeexEchoState * echoState;
	SpeexBits bits;
	spx_int16_t * ebuf;

	int quality;
	int samplingRate;
	int frameBytes;
	int bitSize;
	int frameSize;

	int mode;
} Speex;

static int vSpeexBitSizes[] = {10,15,20,20,28,28,38,38,46,46};

/*
 * Class:     org_hailong_speex_Speex
 * Method:    alloc
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_org_hailong_speex_Speex_alloc
  (JNIEnv * env, jclass clazz, jint mode){

	Speex * speex = (Speex *) malloc(sizeof(Speex));

	memset(speex,0,sizeof(Speex));

	speex->quality = 8;
	speex->bitSize = vSpeexBitSizes[speex->quality - 1];

	switch(mode){
	case 0:
		speex->encodeState = speex_encoder_init(& speex_nb_mode);
		speex->decodeState = speex_decoder_init(& speex_nb_mode);
		break;
	case 1:
		speex->encodeState = speex_encoder_init(& speex_wb_mode);
		speex->decodeState = speex_decoder_init(& speex_wb_mode);
		break;
	case 2:
		speex->encodeState = speex_encoder_init(& speex_uwb_mode);
		speex->decodeState = speex_decoder_init(& speex_uwb_mode);
		break;
	default:
		speex->encodeState = speex_encoder_init(& speex_wb_mode);
		speex->decodeState = speex_decoder_init(& speex_wb_mode);
		break;
	}

	speex_bits_init(& speex->bits);

	int b = 1;

	speex_encoder_ctl(speex->encodeState,SPEEX_GET_FRAME_SIZE,&speex->frameSize);
	speex_encoder_ctl(speex->encodeState,SPEEX_SET_QUALITY,&speex->quality);
	speex_encoder_ctl(speex->encodeState,SPEEX_GET_SAMPLING_RATE,&speex->samplingRate);
	speex_encoder_ctl(speex->encodeState,SPEEX_SET_DTX,&b);
	speex_encoder_ctl(speex->encodeState,SPEEX_SET_VAD,&b);

	speex->preprocessState = speex_preprocess_state_init(speex->frameSize,speex->samplingRate);

	speex_preprocess_ctl(speex->preprocessState, SPEEX_PREPROCESS_SET_DENOISE, &b);

	speex->echoState = speex_echo_state_init(speex->frameSize, 100 );

	speex->frameBytes = speex->frameSize * sizeof(spx_int16_t);

	return (jlong) (void *) speex;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    dealloc
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_hailong_speex_Speex_dealloc
  (JNIEnv * env, jclass clazz, jlong ptr){

	Speex * speex = (Speex *) (void *) ptr;

	speex_bits_destroy(& speex->bits);

	if(speex->encodeState){
		speex_encoder_destroy(speex->encodeState);
	}

	if(speex->decodeState){
		speex_decoder_destroy(speex->decodeState);
	}

	if(speex->preprocessState){
		speex_preprocess_state_destroy(speex->preprocessState);
	}

	if(speex->echoState){
		speex_echo_state_destroy(speex->echoState);
	}

	if(speex->ebuf){
		free(speex->ebuf);
	}


	free(speex);
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    frameSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_frameSize
  (JNIEnv * env, jclass clazz, jlong ptr){

	Speex * speex = (Speex *) (void *) ptr;

	return speex->frameSize;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    frameBytes
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_frameBytes
  (JNIEnv * env, jclass clazz, jlong ptr){

	Speex * speex = (Speex *) (void *) ptr;

	return speex->frameBytes;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    samplingRate
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_samplingRate
  (JNIEnv * env, jclass clazz, jlong ptr){

	Speex * speex = (Speex *) (void *) ptr;

	return speex->samplingRate;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    setSamplingRate
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_hailong_speex_Speex_setSamplingRate
  (JNIEnv * env, jclass clazz, jlong ptr, jint samplingRate){

	Speex * speex = (Speex *) (void *) ptr;

	speex_encoder_ctl(speex->encodeState,SPEEX_SET_SAMPLING_RATE,&samplingRate);
	speex_encoder_ctl(speex->encodeState,SPEEX_GET_SAMPLING_RATE,&speex->samplingRate);

}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    quality
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_quality
  (JNIEnv * env, jclass clazz, jlong ptr){

	Speex * speex = (Speex *) (void *) ptr;

	return speex->quality;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    setQuality
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_hailong_speex_Speex_setQuality
  (JNIEnv * env, jclass clazz, jlong ptr, jint quality){

	Speex * speex = (Speex *) (void *) ptr;

	if(quality < 1){
		quality = 1;
	}

	if(quality > 10){
		quality = 10;
	}

	speex->quality = quality;

	speex->bitSize = vSpeexBitSizes[quality - 1];

	speex_encoder_ctl(speex->encodeState,SPEEX_SET_QUALITY,&speex->quality);
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    bitSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_bitSize
  (JNIEnv * env, jclass clazz, jlong ptr){

	Speex * speex = (Speex *) (void *) ptr;

	return speex->bitSize;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    encode
 * Signature: (J[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_encode
  (JNIEnv * env, jclass clazz, jlong ptr, jbyteArray inBytes, jbyteArray outBytes, jbyteArray echoBytes){

	Speex * speex = (Speex *) (void *) ptr;

	if(inBytes && outBytes){

		jbyte * inb = env->GetByteArrayElements(inBytes,0);
		jbyte * oub = env->GetByteArrayElements(outBytes,0);

		spx_int16_t * enc = (spx_int16_t *) inb;

		jint length = 0;

		if(echoBytes){

			jbyte * echob = env->GetByteArrayElements(echoBytes,0);

			if(speex->ebuf == NULL){
				speex->ebuf = (spx_int16_t *) malloc(speex->frameBytes);
			}

			speex_echo_state_reset(speex->echoState);
			speex_echo_cancellation(speex->echoState, enc, (spx_int16_t *)echob, speex->ebuf);

			enc = speex->ebuf;

			env->ReleaseByteArrayElements(echoBytes,echob,JNI_COMMIT);
		}

		if(speex_preprocess_run(speex->preprocessState, enc))
		{
			speex_bits_reset(&speex->bits);
			speex_encode_int(speex->encodeState, enc, &speex->bits);
			length = speex_bits_write(&speex->bits, (char *) oub, speex->frameBytes);
		}

		env->ReleaseByteArrayElements(inBytes,inb,JNI_COMMIT);

		env->ReleaseByteArrayElements(outBytes,oub,JNI_COMMIT);

		return length;
	}

	return 0;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    decode
 * Signature: (J[BI[B)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_decode
  (JNIEnv * env, jclass clazz, jlong ptr, jbyteArray inBytes, jint length, jbyteArray outBytes){

	Speex * speex = (Speex *) (void *) ptr;

	int rs = -1;

	if(inBytes && length >0 && outBytes){

		jbyte * inb = env->GetByteArrayElements(inBytes,0);

		jbyte * oub = env->GetByteArrayElements(outBytes,0);

		speex_bits_reset(&speex->bits);
		speex_bits_read_from(&speex->bits, (char *) inb, length);

		rs = speex_decode_int(speex->decodeState, &speex->bits, (spx_int16_t *) oub);

		env->ReleaseByteArrayElements(inBytes,inb,JNI_COMMIT);

		env->ReleaseByteArrayElements(outBytes,oub,JNI_COMMIT);
	}

	return rs ==0 ? speex->frameBytes : 0;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    getHeader
 * Signature: (J[BI)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_getHeader
  (JNIEnv * env, jclass clazz, jlong ptr, jbyteArray buffer, jint length){

	Speex * speex = (Speex *) (void *) ptr;

	const struct SpeexMode * m = & speex_nb_mode;

	switch (speex->mode) {
		case 0:
			m = & speex_nb_mode;
			break;
		case 1:
			m = & speex_wb_mode;
			break;
		case 3:
			m = & speex_uwb_mode;
			break;
		default:
			m = & speex_wb_mode;
			break;
	}

	SpeexHeader header;

	speex_init_header(&header, speex->samplingRate, 1, m);

	header.vbr = 0;
	header.bitrate = 16;
	header.frame_size = speex->frameSize;
	header.frames_per_packet = speex->bitSize;
	header.reserved1 = speex->quality;

	int bytes = 0;
	void * data = speex_header_to_packet(&header, & bytes);

	if(buffer && length >= bytes){

		jbyte * b = env->GetByteArrayElements(buffer,0);

		memcpy(b,data,bytes);

		env->ReleaseByteArrayElements(buffer,b,JNI_COMMIT);

	}
	else{
		bytes = 0;
	}

	speex_header_free(data);

	return bytes;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    allocByHeader
 * Signature: ([BI)J
 */
JNIEXPORT jlong JNICALL Java_org_hailong_speex_Speex_allocByHeader
  (JNIEnv * env, jclass clazz, jbyteArray buffer, jint length){

	jlong ptr = 0;

	jbyte * b = env->GetByteArrayElements(buffer,0);

	SpeexHeader * header = speex_packet_to_header((char *)b, length);

	if(header){

		ptr = Java_org_hailong_speex_Speex_alloc(env,clazz,header->mode);

		if(header->reserved1){
			Java_org_hailong_speex_Speex_setQuality(env,clazz,ptr,header->reserved1);
		}

		Speex * speex = (Speex *) (void *) ptr;

		speex->bitSize = header->frames_per_packet;
	}

	speex_header_free(header);


	return ptr;
}

/*
 * Class:     org_hailong_speex_Speex
 * Method:    getMode
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_hailong_speex_Speex_getMode
  (JNIEnv * env, jclass clazz, jlong ptr){
	Speex * speex = (Speex *) (void *) ptr;
	return speex->mode;
}
