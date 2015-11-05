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
