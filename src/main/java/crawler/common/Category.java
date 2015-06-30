package crawler.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import crawler.core.Job;

public class Category {
	private static Logger logger = Logger.getLogger(Category.class);
	private String categoryFile_;
	private Map<String, Integer> bigCategory_ = new HashMap<String, Integer>();
	private Map<String, Integer> smallCategory_ = new HashMap<String, Integer>();;

	public static int kDefaultBigCategory = 10;
	public static int kSmalltBigCategory = 35;
	
	private static Category category_ = new Category("./category.ini");
	
	public static Category Instance() {
		return category_;
	}

	public Category(String categoryFile) {
		categoryFile_ = categoryFile;
		try {
			Parse();
		} catch (IOException e) {
			logger.error("Failed to Load " + categoryFile_);
			assert false;
			e.printStackTrace();
		}
	}

	public int GetBigCategory(String category) {
		Integer c = bigCategory_.get(category);
		if (c != null) {
			return c.intValue();
		}
		return kDefaultBigCategory;
	}

	public int GetSmallCategory(String category) {
		Integer c = smallCategory_.get(category);
		if (c != null) {
			return c.intValue();
		}
		return kSmalltBigCategory;
	}

	private void Parse() throws IOException {
		FileInputStream fis = new FileInputStream(categoryFile_);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		String line = "";
		String[] arrs = null;
		Map map = null;
		while ((line = br.readLine()) != null) {
			line.replaceAll(" ", "");
			if (line.length() == 0) {
				continue;
			}
			if (line.equalsIgnoreCase("[big]")) {
				map = bigCategory_;
				continue;
			} else if (line.equalsIgnoreCase("[small]")) {
				map = smallCategory_;
				continue;
			}
			if (map == null) {
				continue;
			}
			arrs = line.split("\\|");
			if (arrs.length != 3) {
				continue;
			}
			Integer category = Integer.valueOf(arrs[0]);
			map.put(arrs[1], category);
			arrs = arrs[2].split(",");
			for (int i = 0; i < arrs.length; ++i) {
				map.put(arrs[i], category);
			}
		}
		br.close();
		isr.close();
		fis.close();
	}

	public static void main(String[] args) {
		Category ca = new Category("src/main/resources/category.ini");
		logger.info(ca.GetBigCategory("幻想网游"));
		logger.info(ca.GetSmallCategory("思想·文化"));
	}

}
