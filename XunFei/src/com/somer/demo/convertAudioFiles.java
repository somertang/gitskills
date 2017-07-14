package com.somer.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class convertAudioFiles {

	public static void convertAudioFiles(String src, String target) throws Exception {
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(target);

		// ���㳤��
		byte[] buf = new byte[1024 * 4];
		int size = fis.read(buf);
		int PCMSize = 0;
		while (size != -1) {
			PCMSize += size;
			size = fis.read(buf);
		}
		fis.close();

		// ��������������ʵȵȡ������õ���16λ������ 8000 hz
		WaveHeader header = new WaveHeader();
		// �����ֶ� = ���ݵĴ�С��PCMSize) + ͷ���ֶεĴ�С(������ǰ��4�ֽڵı�ʶ��RIFF�Լ�fileLength�����4�ֽ�)
		header.fileLength = PCMSize + (44 - 8);
		header.FmtHdrLeth = 16;
		header.BitsPerSample = 16;
		header.Channels = 2;
		header.FormatTag = 0x0001;
		header.SamplesPerSec = 8000;
		header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		header.DataHdrLeth = PCMSize;

		byte[] h = header.getHeader();

		assert h.length == 44; // WAV��׼��ͷ��Ӧ����44�ֽ�
		// write header
		fos.write(h, 0, h.length);
		// write data stream
		fis = new FileInputStream(src);
		size = fis.read(buf);
		while (size != -1) {
			fos.write(buf, 0, size);
			size = fis.read(buf);
		}
		fis.close();
		fos.close();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String date=dateFormat.format(new java.util.Date());
		System.out.println("<" + date + ">" + target);
		System.out.println("<" + date + ">" + "Convert OK!");
		//System.out.println("Convert OK!");
	}
}
