package com.junicorn.kira.http;

/**
 * 请求处理器接口
 */
public interface HttpRequestHandler {
	
	public HttpResponse handleRequest(HttpRequest request);
	
}