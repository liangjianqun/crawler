package crawler.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
	public static void WriteFile(String content, String filename) throws IOException {
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
					break;
				} else if (totalLen > bufferSize) {
					buf = new byte[bufferSize * 2];
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
	
}
