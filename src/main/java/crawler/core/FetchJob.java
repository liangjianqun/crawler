package crawler.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.tags.Div;

import crawler.common.Api;
import crawler.common.Pair;
import crawler.common.Utils;
import crawler.db.DbUtils;
import crawler.novel.Article;
import crawler.novel.Chapter;
import crawler.parser.ArticleParser;
import crawler.parser.FastParser;

public class FetchJob {
	public static String resultDir_ = "./fetch_result/";
	public static String kArticleHtml = "article.html";
	private String baseUrl_ = "http://www.kaixinwx.com";
	private String bookUrl_ = "http://www.kaixinwx.com/book/";
	private String toReplace_ = "开心";
	
	private int articleNo_ = 0;
	private String articleUrl_ = null;
	private Article article_ = null;
	private Crawler crawler_ = new Crawler();
	public static int lastArticleNo_ = 0;
	private int lastChapterNo_ = 0;
	
	public FetchJob(int articleNo) {
		articleNo_ = articleNo;
		articleUrl_ = bookUrl_ + articleNo +".html";
	}
	
	public int ParseArticle() {
		System.out.println("Try to fetch " + articleUrl_);
		byte[] html = crawler_.FetchByGet(articleUrl_, Crawler.DefaultProperties(), Api.kFetchRetry);
  
		if (html == null) {
			return -1;
		}
		try {
			Utils.WriteFile(html, ArticlePath() + kArticleHtml);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		List<String> toReplace = new ArrayList<String>();
		toReplace.add(toReplace_);
		article_ = ArticleParser.ParseSome(new String(html), toReplace);
		
		return 0;
	}
	
	public String ArticlePath() {
		String dir = resultDir_ + articleNo_ + "/";
		Utils.Makedir(dir);
		return dir;
	}
	
	public int ProcessChapter() {
		List<Pair<String, String>> list = article_.getChapterList();
		for (int i = 0; i < list.size(); ++i) {
			String fileName = ArticlePath();
			
			String url = "";
			if (list.get(i).second().indexOf("http") < 0) {
				url = baseUrl_;
			}
			url += list.get(i).second();
			
			fileName = fileName + url.substring(url.lastIndexOf('/') + 1);
			
            System.out.println("LastChapterNo " + lastChapterNo_ + " Begin to FetchChapter " + url);    
			byte[] html = crawler_.FetchByGet(url, Crawler.DefaultProperties(), Api.kFetchRetry);
			if (html == null) {
				System.err.println("FATAL failed to fetch url " + url + " " + fileName);
				continue;
			}
			String txt = ParseChapter(new String(html));
			if (txt == null) {
				System.err.println("FATAL failed to ParseChapter url " + url + " " + fileName);
				continue;
			}
			try {
				Utils.WriteFile(txt, fileName);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("FATAL failed to Save url " + url + " " + fileName);
				continue;
			}
		}

		return 0;
	}
	
	public String ParseChapter(String html) {
        List<Div> authors=FastParser.parseTags(html, Div.class, "class", "mainContenr"); 
		for (Div div : authors) {
			return div.getStringText();
		}
		return null;
	}
	
	public static boolean AlreadyHasArticle(String name) {
		Connection con = DbUtils.GetConnection();
		String sql = "select * from t_article where articlename = '" + name+ "'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public int ProcessArticle() {
        System.out.println("LastArticleNo " + lastArticleNo_ +" Begin to FetchArticle " + articleUrl_);    
		if (ParseArticle() != 0) {
			System.err.println("FATAL failed to ParseArticle " + articleUrl_);
			return -1;
		}
		if (AlreadyHasArticle(article_.getArticlename())) {
			System.out.println("AlreadyHasArticle " + articleUrl_ + " " + article_.getArticlename());  
			return -1;
		}

		String fileName = ArticlePath() + articleNo_ +".jpg";
		String url = "";
		if (article_.getNovelCover().indexOf("http") < 0) {
			url = baseUrl_;
		}
		url += article_.getNovelCover();
		byte[] html = crawler_.FetchByGet(url, Crawler.DefaultProperties(), Api.kFetchRetry);
		if (html == null) {
			System.err.println("FATAL failed to fetch url " + url + " " + fileName);
			return -1;
		}
		try {
			Utils.WriteFile(html, fileName);
		} catch (IOException e) {
            System.err.println("FATAL failed to SaveCoverToDisk url " + url + " " + fileName);
            return -1;
		}

		return 0;
	}
	
	public int Process() {
		if (ProcessArticle() != 0) {
            return -1;
		}
		return ProcessChapter();
	}
	
	public static void main(String[] args) {
		int start = 1;
		int stop = 50000;
		if (args.length >= 1) {
			start = Integer.parseInt(args[0]);
		} 
		if (args.length >= 2) {
			stop = Integer.parseInt(args[1]);
		}
		for (int i = start; i <= stop; ++i) {
			FetchJob job = new FetchJob(i);
			job.Process();
		}
	}
}
