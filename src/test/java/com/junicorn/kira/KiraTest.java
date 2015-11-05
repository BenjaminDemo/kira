package com.junicorn.kira;

import java.io.IOException;

import com.junicorn.kira.handler.impl.StaticFileHandler;

/**
 * 测试Kira服务
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class KiraTest {
	
	public static void main(String[] args) {
		try {
			Kira kira = new Kira(8089);
			kira.addHandler(new StaticFileHandler("F:\\hb\\izone"));
			kira.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
