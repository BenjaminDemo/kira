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
package com.junicorn.kira.kit;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Httpkit {
	
	public static Map<String, Object> parseData(String data) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String[] split = data.split("&");
		for (String s : split) {
			int idx = s.indexOf('=');
			try {
				if (idx != -1) {
					ret.put(URLDecoder.decode(s.substring(0, idx), "UTF-8"), URLDecoder.decode(s.substring(idx + 1), "UTF-8"));
				} else {
					ret.put(URLDecoder.decode(s, "UTF-8"), "true");
				}
			} catch (UnsupportedEncodingException e) {
				// Why.
			}
		}
		return ret;
	}

	public static String capitalizeHeader(String header) {
		StringTokenizer st = new StringTokenizer(header, "-");
		StringBuilder out = new StringBuilder();
		while (st.hasMoreTokens()) {
			String l = st.nextToken();
			out.append(Character.toUpperCase(l.charAt(0)));
			if (l.length() > 1) {
				out.append(l.substring(1));
			}
			if (st.hasMoreTokens())
				out.append('-');
		}
		return out.toString();
	}
}
