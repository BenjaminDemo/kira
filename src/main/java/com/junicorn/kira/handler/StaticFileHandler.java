package com.junicorn.kira.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.junicorn.kira.http.HttpRequest;
import com.junicorn.kira.http.HttpRequestHandler;
import com.junicorn.kira.http.HttpResponse;
import com.junicorn.kira.http.HttpStatus;

public class StaticFileHandler implements HttpRequestHandler {

	private File documentRoot;

	private String documentRootPath;

	public StaticFileHandler(File documentRoot) {
		this.documentRoot = documentRoot;
	}
	
	public StaticFileHandler(String rootPath) {
		this(new File(rootPath));
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri();
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			uri = uri.replace("%20", " ");
		}
		File file = new File(documentRoot, uri);
		if (file.exists() && !file.isDirectory()) {
			try {
				if (documentRootPath == null) {
					documentRootPath = documentRoot.getAbsolutePath();
					if (documentRootPath.endsWith("/") || documentRootPath.endsWith(".")) {
						documentRootPath = documentRootPath.substring(0, documentRootPath.length() - 1);
					}
				}
				String requestPath = file.getCanonicalPath();
				if (requestPath.endsWith("/")) {
					requestPath = requestPath.substring(0, requestPath.length() - 1);
				}
				if (!requestPath.startsWith(documentRootPath)) {
					return new HttpResponse().reason(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
				}
			} catch (IOException e) {
				return new HttpResponse().reason(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			}
			try {
				HttpResponse res = new HttpResponse(HttpStatus.OK, new FileInputStream(file));
				res.setLength(file.length());
				return res;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
