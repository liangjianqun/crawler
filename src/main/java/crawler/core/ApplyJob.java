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

public class ApplyJob {
	private Article article_ = null;
	private Chapter chapter_ = null;
	private Crawler crawler_ = new Crawler();
	public static int lastArticleNo_ = 0;
	private int lastChapterNo_ = 0;
	private boolean dropArticle_ = false;
	private boolean dropChapter_ = false;
	private String baseUrl_ = "http://www.kaixinwx.com";
	
	private int articleNo_ = 0;
	private String articleUrl_ = null;
	
	public ApplyJob(int articleNo) {
		articleNo_ = articleNo;
		articleUrl_ = FetchJob.kBookUrl + articleNo +".html";
	}
	
	public int ParseArticle() {
		byte[] html = null;
		try {
			html = Utils.ReadFile(FetchJob.ArticlePath(articleNo_, false) + FetchJob.kArticleHtml);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
		List<String> toReplace = new ArrayList<String>();
		toReplace.add("开心");
		article_ = ArticleParser.Parse(new String(html), toReplace);
		
		return 0;
	}
	
	public static String CoverPath(int articleNo, int imgflag, boolean createDir) {
		String file = Api.kDeployRoot + Api.kDeployCover;
		file = file + (articleNo / Api.kArticleHashNum) + "/"; 
		file = file + articleNo + "/";
		if (createDir) {
			Utils.Makedir(file);
		}
		file = file + articleNo + Article.ImgSuffix(imgflag);
		return file;
	}
	
	public static int SaveCoverToDisk(byte[] img, int articleNo, int imgflag) {
		int result = 0;
		String file = CoverPath(articleNo, imgflag, true);
		
		try {
			Utils.WriteFile(img, file);
		} catch (IOException e) {
			result = -1;
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int SaveArticleToDb() {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		try {
			pstmt = con.createStatement();
			if (pstmt.execute(article_.Sql() + " RETURNING articleno;")) {
				ResultSet rs = pstmt.getResultSet();
				if (rs.next()) {
					lastArticleNo_ = Integer.parseInt(rs.getString("articleno"));
					article_.setArticleno(lastArticleNo_);
				}
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
		int lastChapterNo = 0;
		String lastChapterName = "";
		List<Pair<String, String>> list = article_.getChapterList();
		for (int i = 0; i < list.size(); ++i) {
			chapter_ = new Chapter();
			chapter_.setArticleno(article_.getArticleno());
			chapter_.setArticlename(article_.getArticlename());
			chapter_.setChaptername(list.get(i).first());
			
			String url = "";
			if (list.get(i).second().indexOf("http") < 0) {
				url = FetchJob.kBaseUrl;
			}
			url += list.get(i).second();
			String from = FetchJob.ArticlePath(articleNo_, false) + url.substring(url.lastIndexOf('/') + 1);
			
			if (!Utils.FileExist(from)) {
				System.out.println("file " + from + " Not exist, fetch now");
				if (FetchUrl(url, from) != 0) {
					System.err.println("FATAL failed to Fetch " + url +" " + from);
					continue;
				}
			}

			int fileSize = (int) Utils.FileSize(from);
			chapter_.setSize(fileSize);
			article_.setSize(article_.getSize() + fileSize);
			if (SaveChapterToDb(chapter_) != 0) {
				System.err.println("FATAL failed to SaveChapterToDb url " + url + " " +
									ChapterPath(chapter_.getArticleno(), chapter_.getChapterno(), false));
				continue;
			}
			
			
			String to = ChapterPath(chapter_.getArticleno(), chapter_.getChapterno(), true);
			if (!Utils.Rename(from, to)) {
				if (FetchUrl(url, to) != 0) {
					System.err.println("FATAL failed to Fetch " + url +" Rename " + from + " " + to);
					continue;
				}
			}
						
			lastChapterNo = chapter_.getChapterno();
			lastChapterName = list.get(i).first();
		}
		if (UpdateLastChapter(lastChapterNo, lastChapterName) != 0) {
			System.err.println("FATAL failed to UpdateLastChapter " + article_.getArticlename());
		}

		return 0;
	}
	
	private int FetchUrl(String url, String fileName) {
		byte[] html = crawler_.FetchByGet(url, Crawler.DefaultProperties(), Api.kFetchRetry);
		if (html == null) {
			System.err.println("FATAL failed to fetch url " + url + " " + fileName);
			return -1;
		}
		String txt = ParseChapter(new String(html));
		if (txt == null) {
			System.err.println("FATAL failed to ParseChapter url " + url + " " + fileName);
			return -1;
		}
		try {
			Utils.WriteFile(txt, fileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("FATAL failed to Save url " + url + " " + fileName);
			return -1;
		}
		return 0;
	}
	
	private int UpdateLastChapter(int lastNo, String lastChapter) {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		String sql = "UPDATE t_article SET lastchapterno = " + lastNo + 
				",lastchapter = '" + lastChapter + "'" + ", size = " + article_.getSize() +
				" WHERE articleno = " + article_.getArticleno();
		try {
			pstmt = con.createStatement();
			pstmt.execute(sql);
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
	
	public String ParseChapter(String html) {
        List<Div> authors=FastParser.parseTags(html, Div.class, "class", "mainContenr"); 
		for (Div div : authors) {
			return div.getStringText();
		}
		return null;
	}
	
	public static String ChapterPath(int articleNo, int chapterNo, boolean createDir) {
		String file = Api.kDeployRoot + Api.kDeployTxt;
		file = file + (articleNo / Api.kArticleHashNum) + "/"; 
		file = file + articleNo + "/";
		if (createDir) {
			Utils.Makedir(file);
		}
		file = file + chapterNo + ".txt";
		return file;
	}
	
	public int SaveChapterToDb(Chapter chapter) {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		try {
			pstmt = con.createStatement();
			if (pstmt.execute(chapter.Sql() + " RETURNING chapterno;")) {
				ResultSet rs = pstmt.getResultSet();
				if (rs.next()) {
					lastChapterNo_ = Integer.parseInt(rs.getString("chapterno"));
					chapter.setChapterno(lastChapterNo_);
				}
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
        System.out.println("LastArticleNo " + lastArticleNo_ +" Begin to ProcessArticle " + articleUrl_);    
		if (ParseArticle() != 0) {
			System.err.println("FATAL failed to ParseArticle " + articleUrl_);
			return -1;
		}
		if (AlreadyHasArticle(article_.getArticlename())) {
			System.out.println("AlreadyHasArticle " + articleUrl_ + " " + article_.getArticlename());  
			return -1;
		}

		if (SaveArticleToDb() != 0) {
			System.err.println("FATAL failed to SaveArticleToDb url " + articleUrl_);
			return -1;
		}
		
		String from = FetchJob.ArticlePath(articleNo_, false) + articleNo_ +".jpg";
		String to = CoverPath(article_.getArticleno(), article_.getImgflag(), true);
		
		if (!Utils.Rename(from, to)) {
			String url = "";
			if (article_.getNovelCover().indexOf("http") < 0) {
				url = FetchJob.kBaseUrl;
			}
			url += article_.getNovelCover();
			if (FetchUrl(url, to) != 0) {
				System.err.println("FATAL failed to Fetch " + url + " Rename " + from + " " + to);
				return -1;
			}
		}
		return 0;
	}
	
	private int DropArticle() {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		String sql = "delete from t_article where articleno = " + article_.getArticleno();
		System.err.println("Begin to Drop article " + sql);
		try {
			pstmt = con.createStatement();
			pstmt.execute(sql); 
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
		if (ProcessArticle() != 0) {
			if (dropArticle_) {
				DropArticle();
			}
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
			ApplyJob job = new ApplyJob(i);
			System.out.println("Apply Job " + job.Process());
		}
	}
}
