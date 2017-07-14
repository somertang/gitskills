package com.somer.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechEvent;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizeToUriListener;

public class Demo {
	private static final String APPID = "59648829";
	
	private static Demo mObject;

	private static StringBuffer mResult = new StringBuffer();

	private boolean mIsLoop = true;

	private String content = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpeechUtility.createUtility("appid=" + APPID);
		getMscObj().loop();
	}

	private static Demo getMscObj() {
		if (mObject == null)
			mObject = new Demo();
		return mObject;
	}

	private boolean onLoop() {
		boolean isWait = true;
		try {
			DebugLog.Log("*********************************");
			DebugLog.Log("Please input the command");
			DebugLog.Log("1：无声合成           2：退出  ");

			Scanner in = new Scanner(System.in);
			System.out.print("选择：");
			int command = in.nextInt();

			DebugLog.Log("You input " + command);

			switch (command) {
			case 1:
				in.nextLine();
				System.out.print("请输入合成内容：");
				content = in.nextLine();
				Synthesize();
				break;
			case 2:
				mIsLoop = false;
				isWait = false;
				in.close();
				break;
			default:
				isWait = false;
				break;
			}
		} catch (Exception e) {

		}

		return isWait;
	}

	/**
	 * 合成
	 */
	private void Synthesize() {
		SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.createSynthesizer();
		// 设置发音人
		speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

		// 启用合成音频流事件，不需要时，不用设置此参数
		speechSynthesizer.setParameter(SpeechConstant.TTS_BUFFER_EVENT, "1");
		
		// 设置合成音频保存位置（可自定义保存位置），默认不保存
		speechSynthesizer.synthesizeToUri(content, "./tts_test.pcm", synthesizeToUriListener);
	}

	/**
	 * 合成监听器
	 */
	SynthesizeToUriListener synthesizeToUriListener = new SynthesizeToUriListener() {

		public void onBufferProgress(int progress) {
			DebugLog.Log("*************合成进度*************" + progress);

		}

		public void onSynthesizeCompleted(String uri, SpeechError error) {
			if (error == null) {
				DebugLog.Log("*************合成成功*************");
				DebugLog.Log("合成音频生成路径：" + uri);
				try {
					convertAudioFiles.convertAudioFiles(uri, "./demo.wav");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				DebugLog.Log("*************" + error.getErrorCode() + "*************");
			waitupLoop();

		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, int arg3, Object obj1, Object obj2) {
			if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				DebugLog.Log("onEvent: type=" + eventType + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3
						+ ", obj2=" + (String) obj2);
				ArrayList<?> bufs = null;
				if (obj1 instanceof ArrayList<?>) {
					bufs = (ArrayList<?>) obj1;
				} else {
					DebugLog.Log("onEvent error obj1 is not ArrayList !");
				} // end of if-else instance of ArrayList

				if (null != bufs) {
					for (final Object obj : bufs) {
						if (obj instanceof byte[]) {
							final byte[] buf = (byte[]) obj;
							DebugLog.Log("onEvent buf length: " + buf.length);
						} else {
							DebugLog.Log("onEvent error element is not byte[] !");
						}
					} // end of for
				} // end of if bufs not null
			} // end of if tts buffer event
		}

	};

	private void waitupLoop() {
		synchronized (this) {
			Demo.this.notify();
		}
	}

	public void loop() {
		while (mIsLoop) {
			try {
				if (onLoop()) {
					synchronized (this) {
						this.wait();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
