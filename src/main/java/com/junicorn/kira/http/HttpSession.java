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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.junicorn.kira.Const;

public class HttpSession {

	private SocketChannel channel;
	private ByteBuffer buffer = ByteBuffer.allocate(2048);
	private StringBuilder readLines = new StringBuilder();
	private int mark = 0;

	public HttpSession(SocketChannel channel) {
		this.channel = channel;
	}

	public String line() {
		return readLines.toString();
	}

	/**
	 * Try to read a line.
	 */
	public String read() throws IOException {
		StringBuilder sb = new StringBuilder();
		int l = -1;
		while (buffer.hasRemaining()) {
			char c = (char) buffer.get();
			sb.append(c);
			if (c == '\n' && l == '\r') {
				// mark our position
				mark = buffer.position();
				// append to the total
				readLines.append(sb);
				// return with no line separators
				return sb.substring(0, sb.length() - 2);
			}
			l = c;
		}
		return null;
	}

	/**
	 * Get more data from the stream.
	 * @throws IOException 
	 */
	public void readData() throws IOException {
		buffer.limit(buffer.capacity());
		int read = channel.read(buffer);
		if (read != -1) {
			buffer.flip();
			buffer.position(mark);
		}
	}

	public void writeLine(String line) throws IOException {
		channel.write(Const.ENCODER.encode(CharBuffer.wrap(line + "\r\n")));
	}
	
	public void sendResponse(HttpResponse response) {
		response.addDefaultHeaders();
		try {
			writeLine(response.getVersion() + " " + response.getStatusCode() + " " + response.getReason());
			for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
				writeLine(header.getKey() + ": " + header.getValue());
			}
			writeLine("");
			
			byte[] content = response.bytes();
			
			getChannel().write(ByteBuffer.wrap(content));
		} catch (IOException ex) {
			// slow silently
			System.out.println("abo");
		}
	}
	
	public SocketChannel getChannel() {
		return channel;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public StringBuilder getReadLines() {
		return readLines;
	}

	public int getMark() {
		return mark;
	}

	public void close() {
		try {
			channel.close();
		} catch (IOException ex) {
		}
	}
}