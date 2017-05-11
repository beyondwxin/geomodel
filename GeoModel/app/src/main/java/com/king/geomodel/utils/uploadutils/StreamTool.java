package com.king.geomodel.utils.uploadutils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
	/**
	 * 从输入流获取数据
	 * 从输入流中获取数据并以字节数组返回，这种输入流可以来自Android 本地也可以来自网络。
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024]; // 你可以根据实际需要调整缓存大小
		int len = -1;
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inputStream.close();
		return outSteam.toByteArray();
	}
}