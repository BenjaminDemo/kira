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
package com.junicorn.kira.http;

/**
 * 文件类型枚取
 */
public enum FileType {
	
	/**
	 * JEPG.
	 */
	IMAGE_JPEG("FFD8FF"),
	
	/**
	 * PNG.
	 */
	IMAGE_PNG("89504E47"),
	
	/**
	 * GIF.
	 */
	IMAGE_GIF("47494638"),
	
	/**
	 * Windows Bitmap.
	 */
	IMAGE_BMP("424D");
	
	private String value = "";
	
	/**
	 * Constructor.
	 * @param value 文件类型
	 */
	private FileType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
