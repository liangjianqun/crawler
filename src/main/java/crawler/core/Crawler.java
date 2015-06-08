package crawler.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import crawler.common.Api;
import crawler.common.Utils;

public class Crawler {
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
				System.out.println("failed to fetch, errno " + result);
				return null;
			}
			return Utils.ReadFromStream(post.getResponseBodyAsStream(),
					Api.kMaxPageSize);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return results;
	}

	public byte[] FetchByGet(String url, Properties headProps) {
		GetMethod getter = new GetMethod(url);
		if (headProps != null) {
			for (Map.Entry<Object, Object> entry : headProps.entrySet()) {
				getter.setRequestHeader((String) entry.getKey(),
						(String) entry.getValue());
			}
		}

		try {
			int errno = httpClient_.executeMethod(getter);
			if (errno != Api.kHttp200) {
				System.out.println("failed to fetch, errno " + errno);
				return null;
			}
			// Cookie[] myCookies = httpClient_.getState().getCookies();
			return Utils.ReadFromStream(getter.getResponseBodyAsStream(),
					Api.kMaxPageSize);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			getter.releaseConnection();
		}

		return null;
	}

	
	
	public static Properties DefaultProperties() {
		Properties props = new Properties();
        props.put("User-Agent",
                "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3");
        props.put("Accept-Language", "zh-cn");
        props.put("Accept-Encoding", "deflate");
        props.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        return props;
	}

	private void Init() {
		httpClient_ = new HttpClient();
	}

	public static void main(String[] args) {
		String url = "http://www.kaixinwx.com/book/35592.html";
		
		Crawler crawler = new Crawler();
		byte[] result = crawler.FetchByGet(url, Crawler.DefaultProperties());
		//System.out.println(new String(result));
		try {
			Utils.WriteFile(new String(result), "./article.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
