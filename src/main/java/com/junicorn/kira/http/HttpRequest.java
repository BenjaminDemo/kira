package com.junicorn.kira.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.junicorn.kira.util.HttpUtil;


/**
 * 请求对象
 * @author biezhi
 *
 */
public class HttpRequest {
	
	private String raw;
	private String uri;
	private HttpMethod method;
	private HttpSession session;
	private String queryString;
	private String data;
	private Map<String, Object> getData;
	private Map<String, Object> postData;
	private Map<String, HttpCookie> cookies;
	
	/**
	 * The request headers
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	public HttpRequest(HttpSession session) {
		this.session = session;
		parse();
	}

	private void parse() {
		
		this.raw = session.line();
		
		// parse the first line
		StringTokenizer tokenizer = new StringTokenizer(raw);
		String method_name = tokenizer.nextToken().toUpperCase();
		method = HttpMethod.valueOf(method_name);
		
		String uri = tokenizer.nextToken();
		this.uri = uri;
		
		int questionIdx = uri.indexOf('?');
		if(questionIdx != -1) {
			String queryString = uri.substring(questionIdx+1);
			this.setQueryString(queryString);
			this.setGetData(HttpUtil.parseData(queryString));
			
			uri = uri.substring(0, questionIdx);
			this.setUri(uri);
		}
		
		// parse the headers
		String[] lines = raw.split("\r\n");
		for (int i = 1; i < lines.length; i++) {
			String[] keyVal = lines[i].split(":", 2);
			headers.put(keyVal[0], keyVal[1]);
		}
		
		// set cookie
		if(headers.containsKey(HttpHeader.COOKIE)) {
			List<HttpCookie> cookies = new LinkedList<HttpCookie>();
			StringTokenizer tok = new StringTokenizer(headers.get(HttpHeader.COOKIE), ";");
			while(tok.hasMoreTokens()) {
				String token = tok.nextToken();
				int eqIdx = token.indexOf('=');
				if(eqIdx == -1) {
					// Invalid cookie
					continue;
				}
				String key = token.substring(0, eqIdx);
				String value = token.substring(eqIdx + 1);
				
				cookies.add(new HttpCookie(key, value));
			}
			setCookies(cookies);
		}
	}
	
	public String getRaw() {
		return raw;
	}

	public HttpSession getSession() {
		return session;
	}

	public String getUri() {
		return uri;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public HttpMethod getMethod() {
		return method;
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Map<String, Object> getGetData() {
		return getData;
	}

	public void setGetData(Map<String, Object> getData) {
		this.getData = getData;
	}

	public Map<String, Object> getPostData() {
		return postData;
	}

	public void setPostData(Map<String, Object> postData) {
		this.postData = postData;
	}

	public HttpCookie getCookie(String name) {
		return cookies.get(name);
	}
	
	public Map<String, HttpCookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<HttpCookie> cookies) {
		Map<String, HttpCookie> map = new HashMap<String, HttpCookie>();
		for(HttpCookie cookie : cookies) {
			map.put(cookie.getName(), cookie);
		}
		this.cookies = map;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public void finalize() {
		if (postData != null) {
			for (Object value : postData.values()) {
				if (value instanceof HttpFileUpload) {
					HttpFileUpload u = (HttpFileUpload) value;
					if (!u.getTempFile().delete()) {
						u.getTempFile().deleteOnExit();
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "HTTPRequest [uri=" + uri + ", method=" + method + ", session=" + session + ", queryString="
				+ queryString + ", data=" + data + ", getData=" + getData + ", postData=" + postData + ", cookies="
				+ cookies + ", headers=" + headers + "]";
	}
	
}