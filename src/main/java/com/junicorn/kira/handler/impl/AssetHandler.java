/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.junicorn.kira.handler.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.junicorn.kira.Const;
import com.junicorn.kira.handler.RequestHandler;
import com.junicorn.kira.http.HttpRequest;
import com.junicorn.kira.http.HttpResponse;
import com.junicorn.kira.http.HttpStatus;
import com.junicorn.kira.kit.PathKit;

import blade.kit.log.Logger;

/**
 * 磁盘静态文件实现的Handler
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class AssetHandler implements RequestHandler {

	private static final Logger LOGGER = Logger.getLogger();
	
	private File rootFile;

	private String rootPath;

	public AssetHandler(File rootFile) {
		this.rootFile = rootFile;
		this.rootPath = rootFile.getPath();
	}
	
	public AssetHandler(String rootPath) {
		this(new File(rootPath));
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String uri = request.getUri();
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			uri = uri.replace("%20", " ");
		}
		
		LOGGER.info("Request URI > " + uri);
		
		uri = PathKit.fixPath(uri);
		
		if(uri.equals("/")){
			uri = "/" + Const.DEFAULT_ROOTFILE;
		}
		
		File file = new File(rootFile, uri);
		if (file.exists() && !file.isDirectory()) {
			try {
				if (rootPath == null) {
					rootPath = rootFile.getAbsolutePath();
					if (rootPath.endsWith("/") || rootPath.endsWith(".")) {
						rootPath = rootPath.substring(0, rootPath.length() - 1);
					}
				}
				
				String requestPath = file.getCanonicalPath();
				if (requestPath.endsWith("/")) {
					requestPath = requestPath.substring(0, requestPath.length() - 1);
				}
				if (!requestPath.startsWith(rootPath)) {
					return new HttpResponse().reason(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
				}
				
				HttpResponse res = new HttpResponse(HttpStatus.OK, new FileInputStream(file));
				res.setLength(file.length());
				return res;
				
			} catch (IOException e) {
				return new HttpResponse().reason(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			}
		}
		return null;
	}

}
