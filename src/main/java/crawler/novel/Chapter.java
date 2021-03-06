package crawler.novel;

import org.apache.log4j.Logger;

import crawler.common.Utils;

public class Chapter {
	private static Logger logger = Logger.getLogger(Chapter.class);
/*
	chapterno serial NOT NULL,
    articleno int,
    articlename varchar(100),
    chaptertype smallint,
    chaptername varchar(100),
    size int DEFAULT 0,
    isvip boolean,
    postdate timestamp,
    publishtime timestamp,
    ispublish boolean DEFAULT 'false',
    lastchecktime timestamp,
    deleteflag boolean DEFAULT 'false',
    modifyuserno integer,
    modifytime timestamp without time zone,
    PRIMARY KEY (chapterno) 
 */
	public static String SqlChapterPrefix = "INSERT INTO t_chapter (articleno,articlename,chaptertype,chaptername,"+
			"size,isvip,postdate,publishtime,ispublish,lastchecktime,deleteflag,modifyuserno,modifytime) VALUES ";
			

	private int chapterno = 0;
	private int articleno = 1;
	private String articlename = "笑傲江湖";
	private int chaptertype = 0;
	private String chaptername = "第一章 西湖底下";
	private int size = 0;
	private boolean isvip = false;
	private String postdate = Utils.TimeOfDay();
	private String publishtime = Utils.TimeOfDay();
	private boolean ispublish = true;
	private String lastchecktime = Utils.TimeOfDay();
	private boolean deleteflag = false;
	private int modifyuserno = 0;
	private String modifytime = Utils.TimeOfDay();
	
	public String Sql() {
		String sql = SqlChapterPrefix + " (";
		sql = sql + articleno + ",'" + articlename + "',"  + chaptertype + ",";
		sql = sql + "'" + chaptername + "',"  + size + ",";
		sql = sql + String.valueOf(isvip) + ",'" + postdate + "','" + publishtime + "',";
		sql = sql + String.valueOf(ispublish) + ",'" + lastchecktime + "',";
		sql = sql + String.valueOf(deleteflag) + "," + modifyuserno + ",'" + modifytime + "')";
		return sql;
	} 

	public static String getSqlChapterPrefix() {
		return SqlChapterPrefix;
	}

	public static void setSqlChapterPrefix(String sqlChapterPrefix) {
		SqlChapterPrefix = sqlChapterPrefix;
	}
	
	public int getChapterno() {
		return chapterno;
	}

	public void setChapterno(int chapterno) {
		this.chapterno = chapterno;
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
	}

	public int getChaptertype() {
		return chaptertype;
	}

	public void setChaptertype(int chaptertype) {
		this.chaptertype = chaptertype;
	}

	public String getChaptername() {
		return chaptername;
	}

	public void setChaptername(String chaptername) {
		this.chaptername = chaptername;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isIsvip() {
		return isvip;
	}

	public void setIsvip(boolean isvip) {
		this.isvip = isvip;
	}

	public String getPostdate() {
		return postdate;
	}

	public void setPostdate(String postdate) {
		this.postdate = postdate;
	}

	public String getPublishtime() {
		return publishtime;
	}

	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}

	public boolean isIspublish() {
		return ispublish;
	}

	public void setIspublish(boolean ispublish) {
		this.ispublish = ispublish;
	}

	public String getLastchecktime() {
		return lastchecktime;
	}

	public void setLastchecktime(String lastchecktime) {
		this.lastchecktime = lastchecktime;
	}

	public boolean isDeleteflag() {
		return deleteflag;
	}

	public void setDeleteflag(boolean deleteflag) {
		this.deleteflag = deleteflag;
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
	
	public static void main(String[] args) {
		Chapter c = new Chapter();
		logger.info(c.Sql());
	}

}
