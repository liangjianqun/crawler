package crawler.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import crawler.common.Utils;
import crawler.db.DbUtils;
import crawler.novel.Article;
import crawler.novel.Chapter;
import crawler.parser.ArticleParser;

public class Job {
	private String articleUrl_ = null;
	private Article article_ = null;
	private Chapter chapter_ = null;
	
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
	
	public int SaveArticle() {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		try {
			pstmt = con.createStatement();
			pstmt.execute(article_.Sql());
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
	
	public int ParseChapter() {
		return 0;
	}
	
	public int Process() {
		int result = 0;
		if (ParseArticle() != 0 || SaveArticle() != 0) {
			result = -1;
		}
		//result = ParseChapter();
		
		return result;
	}
	
	public static void main(String[] args) {
		for (int i = 1; i <= 10; ++i) {
			Job job = new Job("http://www.kaixinwx.com/book/" + i +".html");
			System.out.println(job.Process());
		}
	}
}
