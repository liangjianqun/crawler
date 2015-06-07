package crawler.common;

import java.io.FileWriter;
import java.io.IOException;

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
	
}
