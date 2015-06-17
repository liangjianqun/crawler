package crawler.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;

import crawler.common.Api;
import crawler.common.Pair;
import crawler.common.Utils;
import crawler.novel.Article;
import crawler.parser.FastParser;

public class ArticleCrawler {

	public ArticleCrawler() {}
	
	public static void GetAllArticle() {
		String url = "http://www.kaixinwx.com/sitemap/index.html";
		
		byte[] result = null;
		try {
			result = Utils.ReadFile("./all_articles.html");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        List<Div> list = FastParser.parseTags(new String(result), Div.class, "class", "mainLink"); 
		List<Pair<String, String>> all_pair = FastParser.ExtractLink(list.get(0).getStringText());
		String res = "";
		for (int i = 0; i < all_pair.size(); ++i) {
			if (all_pair.get(i).first().length() == 0 || all_pair.get(i).second().length() == 0) {
				continue;
			}
			res += all_pair.get(i).second() + "\n";
		}
		try {
			Utils.WriteFile(res, "./alink");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int GetAnArticle(String url, String file) {
		int ret = 0;
		Crawler crawler = new Crawler();
		byte[] result = crawler.FetchByGet(url, Crawler.DefaultProperties(), Api.kFetchRetry);
		if (result == null) {
			return -1;
		}
		try {
			Utils.WriteFile(new String(result), file);
		} catch (IOException e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}
	
	

	
	public static void main(String[] args) {
		String url = "http://www.kaixinwx.com/book/39800.html";
		String file = "article_test.html";
		ArticleCrawler.GetAnArticle(url, file);
	}

}
