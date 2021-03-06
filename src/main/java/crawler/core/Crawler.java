package crawler.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import crawler.common.Api;
import crawler.common.Utils;

public class Crawler {
	private static Logger logger = Logger.getLogger(Crawler.class);
	private HttpClient httpClient_;

	public Crawler() {
		Init();
	}

	public byte[] FetchByPost(String url, Properties headProps,
			NameValuePair[] postParams) {
		PostMethod post = new PostMethod(url);

		if (headProps != null) {
			for (Map.Entry<Object, Object> entry : headProps.entrySet()) {
				post.setRequestHeader((String) entry.getKey(),
						(String) entry.getValue());
			}
		}

		if (postParams != null) {
			post.setRequestBody(postParams);
		}

		byte[] results = null;
		try {
			int result = httpClient_.executeMethod(post);
			if (result != Api.kHttp200) {
				logger.info("failed to fetch " + url + ", errno " + result);
				return null;
			}
			return Utils.ReadFromStream(post.getResponseBodyAsStream(),
					Api.kMaxPageSize);
		} catch (Exception e) {
			logger.error("Failed to Fetch " + url);
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		if (results == null) {
			Init();
		}
		return results;
	}
	
	public byte[] FetchByGet(String url, Properties headProps, int retry) {
		byte[]  result = null;
		
		for (; retry > 0; --retry) {
			result = FetchByGet(url, headProps);
			if (result != null) {
				break;
			}
			logger.error("Failed to fetch " + url +" retry:" + retry);
		}
		return result;
	}

	public byte[] FetchByGet(String url, Properties headProps) {
		GetMethod getter = new GetMethod(url);
		if (headProps != null) {
			for (Map.Entry<Object, Object> entry : headProps.entrySet()) {
				getter.setRequestHeader((String) entry.getKey(),
						(String) entry.getValue());
			}
		}

		byte[] result = null;
		try {
				int errno = httpClient_.executeMethod(getter);
				if (errno != Api.kHttp200) {
					logger.info("Failed to fetch " + url + " , errno " + errno);
				} else {
					result = Utils.ReadFromStream(getter.getResponseBodyAsStream(), 0);
				}
		} catch (Exception e) {
			logger.error("Failed to Fetch " + url);
			e.printStackTrace();
		} finally {
			getter.releaseConnection();
		}
		if (result == null) {
			Init();
		}

		return result;
	}

	public static Properties DefaultProperties() {
		Properties props = new Properties();
        props.put("User-Agent",
                //"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3");
        			//"Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
        			"Mozilla/5.0 (Linux; Android 4.4.4; HM NOTE 1LTE Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.93 Mobile Safari/537.36");
        	props.put("Accept-Language", "zh-cn");
        props.put("Accept-Encoding", "deflate");
        props.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        return props;
	}

	private void Init() {
		httpClient_ = new HttpClient();
		httpClient_.getHttpConnectionManager().getParams().setConnectionTimeout(Api.kConnectionTimeout);
		httpClient_.getHttpConnectionManager().getParams().setSoTimeout(Api.kFetchTimeout);
	}

	public static void main(String[] args) {
		//String url = "http://www.kaixinwx.com/book/35592.html";
		String url = "http://m.sm.cn/novelw/menu.php?page=5&title=%E6%88%91%E7%9A%84%E4%BB%99%E5%A5%B3%E5%A4%A7%E5%B0%8F%E5%A7%90&author=%E5%AF%92%E9%97%A8&order=asc&uc_param_str=dnntnwvepffrgibijbprsv";
		//url += "?uc_param_str=dnntnwvepffrgibijbprsv&#catal/%E6%88%91%E7%9A%84%E4%BB%99%E5%A5%B3%E5%A4%A7%E5%B0%8F%E5%A7%90/%E5%AF%92%E9%97%A8";
		Crawler crawler = new Crawler();
		byte[] result = crawler.FetchByGet(url, Crawler.DefaultProperties(), 1);
		//logger.info(new String(result));
		try {
			Utils.WriteFile(new String(result), "./article.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
