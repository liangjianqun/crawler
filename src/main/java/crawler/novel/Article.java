package crawler.novel;

import java.util.List;

import org.apache.log4j.Logger;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import crawler.common.Pair;
import crawler.common.Pinyin;
import crawler.common.Utils;
import crawler.core.FetchJob;

public class Article {
	private static Logger logger = Logger.getLogger(Article.class);
	public static final String kStringDelimiter = "''";
	public static final String kCommaDelimiter = ",";
	public static String SqlArticlePrefix = "INSERT INTO t_article(articlename,pinyin,pinyinheadchar,initial,keywords,"+
			"authorid,author,category,subcategory,intro,lastchapterno,lastchapter,chapters,size,"+
			"fullflag,imgflag,agent,firstflag,permission,authorflag,postdate,lastupdate,"+
			"dayvisit,weekvisit,monthvisit,allvisit,dayvote,weekvote,monthvote,allvote,deleteflag,"+
			"publicflag,createuserno,createtime,modifyuserno,modifytime) VALUES ";

	private int articleno = 1;
	private String articlename = "笑傲江湖";
	private String pinyin = "xiaoaojianhu";
	private String pinyinheadchar = "xiao";
	private String initial = "x";// 1Byte
	private String keywords = "金庸 令狐冲 独孤九剑";
	private int authorid = 0;
	private String author = "金庸";
	private int category = 10;
	private int subcategory = 10;
	private String intro = "笑傲江湖!";
	private int lastchapterno = 100;
	private String lastchapter = "华山论剑";
	private int chapters = 100;
	private int size = 0;
	private boolean fullflag = false;
	private int imgflag = 1;
	private String agent = "";
	private boolean firstflag = false;
	private int permission = 0;
	private boolean authorflag = false;
	private String postdate = Utils.TimeOfDay();
	private String lastupdate = Utils.TimeOfDay();
	private int dayvisit = 0;
	private int weekvisit = 0;
	private int monthvisit = 0;
	private int allvisit = 0;
	private int dayvote = 0;
	private int weekvote = 0;
	private int monthvote = 0;
	private int allvote = 0;
	private boolean deleteflag = false;
	private int publicflag = 0;
	private int createuserno = 1;
	private String createtime = Utils.TimeOfDay();
	private int modifyuserno = 0;
	private String modifytime = Utils.TimeOfDay();
	
	private String novelCover_ = "";
	private List<Pair<String, String>> chapterList_;
	
	
	public List<Pair<String, String>> getChapterList() {
		return chapterList_;
	}

	public void setChapterList(List<Pair<String, String>> chapterList) {
		this.chapterList_ = chapterList;
	}

	public String getNovelCover() {
		return novelCover_;
	}

	public void setNovelCover(String novelCover) {
		this.novelCover_ = novelCover;
		setImgflag(Imgflag(novelCover));
	}
	
	public String Sql() {
		String sql = SqlArticlePrefix + "(";
		sql = sql + "'" + articlename + "',";
		sql = sql + "'" + pinyin  + "','" + pinyinheadchar + "','";
		sql = sql + initial + "','" + keywords + "',";
		sql = sql + authorid + ",'" + author + "'," + category + ",";
		sql = sql + subcategory + ",'" + intro + "',";
		sql = sql + lastchapterno + ",'" + lastchapter + "'," + chapters + "," + size +",";
		sql = sql + String.valueOf(fullflag) +"," + imgflag + ",'" + agent+"',";
		sql = sql + String.valueOf(firstflag) + "," + permission + "," + String.valueOf(authorflag)+",";
		sql = sql + "'" + postdate + "','" + lastupdate+ "'," + dayvisit+",";
		sql = sql + weekvisit + "," +monthvisit + "," + allvisit + "," + dayvote + ",";
		sql = sql + weekvote + "," + monthvote + "," + allvote + "," + String.valueOf(deleteflag) +",";
		sql = sql + publicflag + "," + createuserno + ",'";
		sql = sql + createtime + "'," + modifyuserno + ",'" + modifytime+"')";
				
		return sql;
	}
	
	public static String getSqlArticlePrefix() {
		return SqlArticlePrefix;
	}
	public static void setSqlArticlePrefix(String sqlArticlePrefix) {
		SqlArticlePrefix = sqlArticlePrefix;
	}
	public int getArticleno() {
		return articleno;
	}
	public void setArticleno(int articleno) {
		this.articleno = articleno;
	}
	public String getArticlename() {
		return articlename;
	}
	public void setArticlename(String articlename) {
		this.articlename = articlename;
		String[] py = null;
		try {
			 py = Pinyin.getFirstAndPinyin(this.articlename);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		if (py != null && py.length == 2) {
			setPinyin(py[1]);
			setPinyinheadchar(py[0]);
			setInitial(py[0].substring(0, 1));
		}
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getPinyinheadchar() {
		return pinyinheadchar;
	}
	public void setPinyinheadchar(String pinyinheadchar) {
		this.pinyinheadchar = pinyinheadchar;
	}
	public String getInitial() {
		return initial;
	}
	public void setInitial(String initial) {
		this.initial = initial;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public int getAuthorid() {
		return authorid;
	}
	public void setAuthorid(int authorid) {
		this.authorid = authorid;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public int getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(int subcategory) {
		this.subcategory = subcategory;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public int getLastchapterno() {
		return lastchapterno;
	}
	public void setLastchapterno(int lastchapterno) {
		this.lastchapterno = lastchapterno;
	}
	public String getLastchapter() {
		return lastchapter;
	}
	public void setLastchapter(String lastchapter) {
		this.lastchapter = lastchapter;
	}
	public int getChapters() {
		return chapters;
	}
	public void setChapters(int chapters) {
		this.chapters = chapters;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isFullflag() {
		return fullflag;
	}
	public void setFullflag(boolean fullflag) {
		this.fullflag = fullflag;
	}
	public int getImgflag() {
		return imgflag;
	}
	public void setImgflag(int imgflag) {
		this.imgflag = imgflag;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public boolean isFirstflag() {
		return firstflag;
	}
	public void setFirstflag(boolean firstflag) {
		this.firstflag = firstflag;
	}
	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	public boolean isAuthorflag() {
		return authorflag;
	}
	public void setAuthorflag(boolean authorflag) {
		this.authorflag = authorflag;
	}
	public String getPostdate() {
		return postdate;
	}
	public void setPostdate(String postdate) {
		this.postdate = postdate;
	}
	public String getLastupdate() {
		return lastupdate;
	}
	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}
	public int getDayvisit() {
		return dayvisit;
	}
	public void setDayvisit(int dayvisit) {
		this.dayvisit = dayvisit;
	}
	public int getWeekvisit() {
		return weekvisit;
	}
	public void setWeekvisit(int weekvisit) {
		this.weekvisit = weekvisit;
	}
	public int getMonthvisit() {
		return monthvisit;
	}
	public void setMonthvisit(int monthvisit) {
		this.monthvisit = monthvisit;
	}
	public int getAllvisit() {
		return allvisit;
	}
	public void setAllvisit(int allvisit) {
		this.allvisit = allvisit;
	}
	public int getDayvote() {
		return dayvote;
	}
	public void setDayvote(int dayvote) {
		this.dayvote = dayvote;
	}
	public int getWeekvote() {
		return weekvote;
	}
	public void setWeekvote(int weekvote) {
		this.weekvote = weekvote;
	}
	public int getMonthvote() {
		return monthvote;
	}
	public void setMonthvote(int monthvote) {
		this.monthvote = monthvote;
	}
	public int getAllvote() {
		return allvote;
	}
	public void setAllvote(int allvote) {
		this.allvote = allvote;
	}
	public boolean isDeleteflag() {
		return deleteflag;
	}
	public void setDeleteflag(boolean deleteflag) {
		this.deleteflag = deleteflag;
	}
	public int getPublicflag() {
		return publicflag;
	}
	public void setPublicflag(int publicflag) {
		this.publicflag = publicflag;
	}
	public int getCreateuserno() {
		return createuserno;
	}
	public void setCreateuserno(int createuserno) {
		this.createuserno = createuserno;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public int getModifyuserno() {
		return modifyuserno;
	}
	public void setModifyuserno(int modifyuserno) {
		this.modifyuserno = modifyuserno;
	}
	public String getModifytime() {
		return modifytime;
	}
	public void setModifytime(String modifytime) {
		this.modifytime = modifytime;
	}
	
	public static int Imgflag(String imgfile) {
		if (imgfile.indexOf("nocover.jpg") >= 0) {
			return 0;
		} else if (imgfile.indexOf("l.jpg") >= 0) {
			return 10;
		} else if (imgfile.indexOf(".png") >= 0) {
			return 3;
		} else if (imgfile.indexOf(".gif") >= 0) {
			return 2;
		} else if (imgfile.indexOf(".jpg") >= 0) {
			return 1;
		}
		return 0;
	}
	
	public static String ImgSuffix(int imgflag) {
        switch (imgflag) {
        case 1:
            return "s.jpg";
        case 2:
            return "s.gif";
        case 3:
            return "s.png";
        case 10:
            return "l.jpg";
        default:
            return "nocover.jpg";
        }
	}

	public static void main(String[] args) {
		Article a = new Article();
		logger.info(a.Sql());
	}
}
