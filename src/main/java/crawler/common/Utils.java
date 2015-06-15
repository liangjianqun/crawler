package crawler.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static String TimeOfDay() {
		return TimeOfDay("yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static String TimeOfDay(String format) {
		SimpleDateFormat date = new SimpleDateFormat(format);
		return date.format(new Date());
	}
	
	public static void WriteFile(byte[] content, String filename)
			throws IOException {
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(filename));
			out.write(content);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static void WriteFile(String content, String filename)
			throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(filename);
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static byte[] ReadFromStream(InputStream is, int maxSize) {
		if (is == null) {
			return null;
		}

		int bufferSize = (maxSize > 0 ? maxSize : Api.kMaxPageSize);
		byte[] buf = new byte[bufferSize];
		byte[] buf2 = new byte[4 * 1024];
		boolean hasContent = false;
		int len;
		int totalLen = 0, cursor = 0;
		try {
			while ((len = is.read(buf2, 0, buf2.length)) >= 0) {
				hasContent = true;
				totalLen += len;
				if (maxSize > 0 && totalLen >= maxSize) {
					totalLen -= len;
					System.arraycopy(buf2, 0, buf, cursor, maxSize - totalLen);
					cursor += (maxSize - totalLen);
					break;
				} else if (totalLen > bufferSize) {
					byte[] buf3 = new byte[bufferSize * 2];
					System.arraycopy(buf, 0, buf3, 0, buf.length);
					buf = buf3;
				}
				System.arraycopy(buf2, 0, buf, cursor, len);
				cursor += len;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!hasContent) {
			return null;
		}
		byte[] results = new byte[totalLen];
		System.arraycopy(buf, 0, results, 0, totalLen);
		return results;
	}

	public static byte[] ReadFile(String fileName) throws IOException {
		File file = new File(fileName);
		InputStream is = new FileInputStream(file);
		byte[] result = ReadFromStream(is, 0);
		is.close();
		return result;
	}
	
	public static void Makedir(String path) {
		File fd = null;
		try {
			fd = new File(path);
			if (!fd.exists()) {
				fd.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fd = null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Utils.TimeOfDay());
	}

}
