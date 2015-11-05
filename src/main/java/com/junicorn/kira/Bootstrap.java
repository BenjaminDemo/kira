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
