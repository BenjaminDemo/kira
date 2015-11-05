package com.junicorn.kira;

import java.io.IOException;

public class Bootstrap {

	public static void main(String[] args) throws IOException {
		
		if(null == args || args.length < 1){
			return;
		}
		
		String first = args[0];
		
		// 预览和监听
		if(first.equals("-s") || first.equals("server")){
			
		}
		
		// 生成静态资源
		if(first.equals("-g") || first.equals("generate")){
			
		}
		
	}
	
}
