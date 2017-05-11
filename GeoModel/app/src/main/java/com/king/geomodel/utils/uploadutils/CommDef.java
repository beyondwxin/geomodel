package com.king.geomodel.utils.uploadutils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.os.Environment;

@SuppressLint("SimpleDateFormat")
public class CommDef {
	public static float _gdensity = (float) 1.0; //

	private static String mData = null;

	/**
	 * 存储路径
	 * 
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String gDataFloder() {
		if (mData != null)
			return mData;
		String state = Environment.getExternalStorageState(); // 是否有SD卡

		try {
			if (state.equals(Environment.MEDIA_MOUNTED)) {

				mData = Environment.getExternalStorageDirectory().getCanonicalPath();

				return mData;
			}
			// 判断 /udisk 是否存在 存在则 /udisk/wzy_sl/
			try {
				File f = new File("/udisk/");
				if (f.exists()) {
					mData = "/udisk/";
					return mData;
				}
			} catch (Exception e) {
			}

			mData = Environment.getDataDirectory().getCanonicalPath();
			return mData;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析UTF8数据
	 * 
	 * @param line
	 * @return
	 */
	public static String parseUnicode(String line) {
		int len = line.length();
		char[] out = new char[len];// 保存解析以后的结果
		int outLen = 0;
		for (int i = 0; i < len; i++) {
			char aChar = line.charAt(i);
			if (aChar == '\\') {
				aChar = line.charAt(++i);
				if (aChar == 'u') {
					int value = 0;
					for (int j = 0; j < 4; j++) {
						aChar = line.charAt(++i);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					out[outLen++] = (char) value;
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					else if (aChar == '"')
						out[outLen++] = '\\';
					out[outLen++] = aChar;
				}
			} else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

	/**
	 * 将时间戳转为字符串
	 * 
	 * @param cc_time
	 * @return
	 */
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}
}
