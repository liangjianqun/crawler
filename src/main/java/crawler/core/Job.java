package crawler.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
	private static Logger logger = Logger.getLogger(Job.class);
	private String articleUrl_ = null;
	private Article article_ = null;
	private Chapter chapter_ = null;
	private Crawler crawler_ = new Crawler();
	public static int lastArticleNo_ = 0;
	private int lastChapterNo_ = 0;
	private boolean dropArticle_ = false;
	private boolean dropChapter_ = false;
	private String baseUrl_ = "http://www.kaixinwx.com";

	public Job(String articleUrl) {
		articleUrl_ = articleUrl;
	}

	public int ParseArticle() {
		logger.info("Try to fetch " + articleUrl_);
		Crawler crawler = new Crawler();
		byte[] html = crawler.FetchByGet(articleUrl_,
				Crawler.DefaultProperties(), Api.kFetchRetry);

		if (html == null) {
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
					lastArticleNo_ = Integer
							.parseInt(rs.getString("articleno"));
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

			logger.info("LastChapterNo " + lastChapterNo_
					+ " Begin to ProcessChapter " + url);
			byte[] html = crawler_.FetchByGet(url, Crawler.DefaultProperties(),
					Api.kFetchRetry);
			if (html == null) {
				logger.error("FATAL failed to fetch url "
						+ url
						+ " "
						+ ChapterPath(chapter_.getArticleno(),
								chapter_.getChapterno(), false));
				continue;
			}
			String txt = ParseChapter(new String(html));
			if (txt == null) {
				logger.error("FATAL failed to ParseChapter url "
						+ url
						+ " "
						+ ChapterPath(chapter_.getArticleno(),
								chapter_.getChapterno(), false));
				continue;
			}
			chapter_.setSize(txt.length());
			article_.setSize(article_.getSize() + txt.length());
			if (SaveChapterToDb(chapter_) != 0) {
				logger.error("FATAL failed to SaveChapterToDb url "
						+ url
						+ " "
						+ ChapterPath(chapter_.getArticleno(),
								chapter_.getChapterno(), false));
				continue;
			}
			if (SaveChapterToDisk(txt, chapter_.getArticleno(),
					chapter_.getChapterno()) != 0) {
				logger.error("FATAL failed to SaveChapterToDisk url "
						+ url
						+ " "
						+ ChapterPath(chapter_.getArticleno(),
								chapter_.getChapterno(), false));
				continue;
			}
			if ((i % 100 == 0 || (i == list.size() - 1))
					&& UpdateLastChapter(chapter_.getChapterno(), list.get(i)
							.first()) != 0) {
				logger.error("FATAL failed to UpdateLastChapter "
						+ article_.getArticlename());
			}
		}

		return 0;
	}

	private int UpdateLastChapter(int lastNo, String lastChapter) {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		String sql = "UPDATE t_article SET lastchapterno = " + lastNo
				+ ",lastchapter = '" + lastChapter + "'" + ", size = "
				+ article_.getSize() + " WHERE articleno = "
				+ article_.getArticleno();
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
		List<Div> authors = FastParser.parseTags(html, Div.class, "class",
				"mainContenr");
		for (Div div : authors) {
			return div.getStringText();
		}
		return null;
	}

	public static String ChapterPath(int articleNo, int chapterNo,
			boolean createDir) {
		String file = Api.kDeployRoot + Api.kDeployTxt;
		file = file + (articleNo / Api.kArticleHashNum) + "/";
		file = file + articleNo + "/";
		if (createDir) {
			Utils.Makedir(file);
		}
		file = file + chapterNo + ".txt";
		return file;
	}

	private int SaveChapterToDisk(String txt, int articleNo, int chapterNo) {
		int result = 0;
		String file = ChapterPath(articleNo, chapterNo, true);

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
				if (rs.next()) {
					lastChapterNo_ = Integer
							.parseInt(rs.getString("chapterno"));
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
		String sql = "select * from t_article where articlename = '" + name
				+ "'";
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
		int result = 0;
		logger.info("LastArticleNo " + lastArticleNo_
				+ " Begin to ProcessArticle " + articleUrl_);
		if (ParseArticle() != 0) {
			logger.error("FATAL failed to ParseArticle " + articleUrl_);
			result = -1;
			return result;
		}
		if (AlreadyHasArticle(article_.getArticlename())) {
			logger.info("AlreadyHasArticle " + articleUrl_ + " "
					+ article_.getArticlename());
			return -1;
		}

		if (SaveArticleToDb() != 0) {
			logger.error("FATAL failed to SaveArticleToDb url " + articleUrl_);
			return -1;
		}
		String url = "";
		if (article_.getNovelCover().indexOf("http") < 0) {
			url = baseUrl_;
		}
		url += article_.getNovelCover();
		byte[] html = crawler_.FetchByGet(url, Crawler.DefaultProperties(),
				Api.kFetchRetry);
		if (html == null) {
			logger.error("FATAL failed to fetch url "
					+ url
					+ " "
					+ CoverPath(article_.getArticleno(), article_.getImgflag(),
							false));
			dropArticle_ = true;
			return -1;
		}
		if (SaveCoverToDisk(html, article_.getArticleno(),
				article_.getImgflag()) != 0) {
			logger.error("FATAL failed to SaveCoverToDisk url "
					+ url
					+ " "
					+ CoverPath(article_.getArticleno(), article_.getImgflag(),
							false));
			dropArticle_ = true;
			return -1;
		}

		return result;
	}

	private int DropArticle() {
		int result = 0;
		Connection con = DbUtils.GetConnection();
		Statement pstmt = null;
		String sql = "delete from t_article where articleno = "
				+ article_.getArticleno();
		logger.error("Begin to Drop article " + sql);
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
		logger.info("Begin now");
		int start = 1;
		int stop = 50000;
		if (args.length >= 1) {
			start = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			stop = Integer.parseInt(args[1]);
		}
		for (int i = start; i <= stop; ++i) {
			//Job job = new Job("http://www.kaixinwx.com/book/" + i + ".html");
			//logger.info(job.Process());
		}
	}
}
