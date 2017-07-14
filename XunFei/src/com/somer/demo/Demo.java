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
			DebugLog.Log("1�������ϳ�           2���˳�  ");

			Scanner in = new Scanner(System.in);
			System.out.print("ѡ��");
			int command = in.nextInt();

			DebugLog.Log("You input " + command);

			switch (command) {
			case 1:
				in.nextLine();
				System.out.print("������ϳ����ݣ�");
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
	 * �ϳ�
	 */
	private void Synthesize() {
		SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.createSynthesizer();
		// ���÷�����
		speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

		// ���úϳ���Ƶ���¼�������Ҫʱ���������ô˲���
		speechSynthesizer.setParameter(SpeechConstant.TTS_BUFFER_EVENT, "1");
		
		// ���úϳ���Ƶ����λ�ã����Զ��屣��λ�ã���Ĭ�ϲ�����
		speechSynthesizer.synthesizeToUri(content, "./tts_test.pcm", synthesizeToUriListener);
	}

	/**
	 * �ϳɼ�����
	 */
	SynthesizeToUriListener synthesizeToUriListener = new SynthesizeToUriListener() {

		public void onBufferProgress(int progress) {
			DebugLog.Log("*************�ϳɽ���*************" + progress);

		}

		public void onSynthesizeCompleted(String uri, SpeechError error) {
			if (error == null) {
				DebugLog.Log("*************�ϳɳɹ�*************");
				DebugLog.Log("�ϳ���Ƶ����·����" + uri);
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
