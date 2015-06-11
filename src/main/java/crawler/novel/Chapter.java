package crawler.novel;

public class Chapter {
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
			
	private int articleno = 1;
	private String articlename = "笑傲江湖";
	private int chaptertype = 10;
	private String chaptername = "第一章 西湖底下";
	private int size = 0;
	private boolean isvip = false;
	private String postdate = "2015-06-09 23:39:50.471";
	private String publishtime = "2015-06-09 23:39:50.471";
	private boolean ispublish = false;
	private String lastchecktime = "2015-06-09 23:39:50.471";
	private boolean deleteflag = false;
	private int modifyuserno = 0;
	private String modifytime = "2015-06-09 23:39:50.471";
	
	public String Sql() {
		String sql = SqlChapterPrefix + " (";
		sql = sql + articleno + ",'" + articlename + "',"  + chaptertype + ",";
		sql = sql + "'" + chaptername + "',"  + size + ",";
		sql = sql + String.valueOf(isvip) + ",'" + postdate + "','" + publishtime + "',";
		sql = sql + String.valueOf(ispublish) + ",'" + lastchecktime + "',";
		sql = sql + String.valueOf(deleteflag) + "," + modifyuserno + ",'" + modifytime + "');";
		return sql;
	} 
	
	public static void main(String[] args) {
		Chapter c = new Chapter();
		System.out.println(c.Sql());
	}

}
