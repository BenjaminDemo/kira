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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件类型判断类
 */
public final class FileTypeJudge {
	
	/**
	 * Constructor
	 */
	private FileTypeJudge() {}
	
	/**
	 * 将文件头转换成16进制字符串
	 * 
	 * @param 原生byte
	 * @return 16进制字符串
	 */
	private static String bytesToHexString(byte[] src){
		
        StringBuilder stringBuilder = new StringBuilder();   
        if (src == null || src.length <= 0) {   
            return null;   
        }   
        for (int i = 0; i < src.length; i++) {   
            int v = src[i] & 0xFF;   
            String hv = Integer.toHexString(v);   
            if (hv.length() < 2) {   
                stringBuilder.append(0);   
            }   
            stringBuilder.append(hv);   
        }   
        return stringBuilder.toString();   
    }
   
	/**
	 * 得到文件头
	 * 
	 * @param filePath 文件路径
	 * @return 文件头
	 * @throws IOException
	 */
	private static String getFileContent(String filePath) throws IOException {
		
		byte[] b = new byte[28];
		
		InputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(filePath);
			inputStream.read(b, 0, 28);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		return bytesToHexString(b);
	}
	
	/**
	 * 判断文件类型
	 * 
	 * @param filePath 文件路径
	 * @return 文件类型
	 */
	public static FileType getType(String filePath) throws IOException {
		
		String fileHead = getFileContent(filePath);
		
		if (fileHead == null || fileHead.length() == 0) {
			return null;
		}
		
		fileHead = fileHead.toUpperCase();
		
		FileType[] fileTypes = FileType.values();
		
		for (FileType type : fileTypes) {
			if (fileHead.startsWith(type.getValue())) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * 判断文件类型
	 * 
	 * @param inputStream 文件流
	 * @return 文件类型
	 */
	public static String getType(InputStream inputStream) throws IOException {
		byte[] b = new byte[28];
		try {
			inputStream.read(b, 0, 28);
			String fileHead = bytesToHexString(b);
			
			if (fileHead == null || fileHead.length() == 0) {
				return null;
			}
			
			fileHead = fileHead.toUpperCase();
			
			FileType[] fileTypes = FileType.values();
			
			for (FileType type : fileTypes) {
				if (fileHead.startsWith(type.getValue())) {
					return type.toString().toLowerCase().replace("_", "/");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		return null;
	}
}
