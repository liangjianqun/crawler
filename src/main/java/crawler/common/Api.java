package crawler.common;

public class Api {
	
	public static int kMaxPageSize = 5242880; // 5MB
	
	
	// Http error
	public static int kHttp200 = 200;
	
	public static int kFetchRetry = 5;
	
	public static int kArticleHashNum = 1000;
	public static String kDeployRoot = "/usr/local/tomcat/webapps/ROOT/";
	public static String kDeployTxt = "txt/";
	public static String kDeployCover = "cover/";
}
