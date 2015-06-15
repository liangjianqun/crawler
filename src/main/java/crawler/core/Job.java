package crawler.core;

import java.io.IOException;
import java.sql.Connection;
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

public class Job {
	private String articleUrl_ = null;
	private Article article_ = null;
	private Chapter chapter_ = null;
	private Crawler crawler_ = new Crawler();
	
	private String baseUrl_ = "http://www.kaixinwx.com/book/";
	
	public Job(String articleUrl) {
		articleUrl_ = articleUrl;
	}
	
	public int ParseArticle() {
		System.out.println("Try to fetch " + articleUrl_);
		Crawler crawler = new Crawler();
		byte[] html = crawler.FetchByGet(articleUrl_, Crawler.DefaultProperties());
  
		if (html == null) {
			return -1;
		}
		List<String> toReplace = new ArrayList<String>();
		toReplace.add("开心");
		article_ = ArticleParser.Parse(new String(html), toReplace);
		return 0;
	}
	
	public static int SaveCoverToDisk(byte[] img, int articleNo, int chapterNo) {
		
		return 0;
	}
	
	public int SaveArticleToDb() {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		try {
			pstmt = con.createStatement();
			if (pstmt.execute(article_.Sql() + " RETURNING articleno;")) {
				ResultSet rs = pstmt.getResultSet();
				article_.setArticleno(Integer.parseInt(rs.getString("articleno")));
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
		}

		try {
			pstmt.close();
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
		}
		return result;
	}
	
	public int ProcessChapter() {
		List<Pair<String, String>> list = article_.getChapterList();
		for (int i = 0; i < list.size(); ++i) {
			chapter_ = new Chapter();
			chapter_.setArticleno(article_.getArticleno());
			chapter_.setArticlename(article_.getArticlename());
			chapter_.setChaptername(list.get(i).first());
			
			String url = "";
			if (list.get(i).second().indexOf("http") < 0) {
				url = baseUrl_;
			}
			url += list.get(i).second();
			
			byte[] html = crawler_.FetchByGet(url, Crawler.DefaultProperties());
			if (html == null) {
				System.err.println("FATAL failed to fetch url " + url);
				continue;
			}
			String txt = ParseChapter(new String(html));
			if (txt == null) {
				System.err.println("FATAL failed to ParseChapter url " + url);
				continue;
			}
			if (SaveChapterToDisk(txt, chapter_.getArticleno(), chapter_.getChapterno()) != 0) {
				System.err.println("FATAL failed to SaveChapterToDisk url " + url);
				continue;
			}
			if (SaveChapterToDb(chapter_) != 0) {
				System.err.println("FATAL failed to SaveChapterToDb url " + url);
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
	
	private int SaveChapterToDisk(String txt, int articleNo, int chapterNo) {
		int result = 0;
		String file = Api.kDeployRoot + Api.kDeployTxt;
		file = file + (articleNo / Api.kArticleHashNum) + "/"; 
		file = file + articleNo + "/" + chapterNo + ".txt";
		
		try {
			Utils.WriteFile(txt, file);
		} catch (IOException e) {
			result = -1;
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int SaveChapterToDb(Chapter chapter) {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		try {
			pstmt = con.createStatement();
			if (pstmt.execute(chapter.Sql() + " RETURNING chapterno;")) {
				ResultSet rs = pstmt.getResultSet();
				chapter.setChapterno(Integer.parseInt(rs.getString("chapterno")));
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
		}

		try {
			pstmt.close();
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
		}
		return result;
	}
	
	public int Process() {
		int result = 0;
		if (ParseArticle() != 0 || SaveArticleToDb() != 0) {
			result = -1;
		}
		result = ProcessChapter();
		
		return result;
	}
	
	public static void main(String[] args) {
		for (int i = 1; i <= 10; ++i) {
			Job job = new Job("http://www.kaixinwx.com/book/" + i +".html");
			System.out.println(job.Process());
		}
	}
}
