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
package com.junicorn.kira;

import java.io.FileNotFoundException;
import java.util.Map;

public class KiraContext {

	private String rootPath;
	
	private String publicPath;
	
	private Map<String, String> locale;
	
	private KiraContext(String rootPath){
		this.rootPath = rootPath;
	}
	
	public static KiraContext init(String rootDir) throws FileNotFoundException  {
		
		KiraContext context = new KiraContext(rootDir);

		return context;
	}

	public String getPublicPath() {
		return publicPath;
	}

	public void setPublicPath(String publicPath) {
		this.publicPath = publicPath;
	}
	
	public Map<String, String> getLocale() {
		return locale;
	}

	public void setLocale(Map<String, String> locale) {
		this.locale = locale;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
}
