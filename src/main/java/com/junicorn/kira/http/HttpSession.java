package com.junicorn.kira.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.junicorn.kira.Kira;

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
		channel.write(Kira.ENCODER.encode(CharBuffer.wrap(line + "\r\n")));
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
		
		/*
		StringBuilder header = new StringBuilder();
		header.append("HTTP/1.1").append(' ').append(resp.getStatus().getCode()).append(' ').append(resp.getStatus());
		header.append('\r').append('\n');
		// Set the content type header if it's not already set
		if (!resp.getHeaders().containsKey(HttpHeader.CONTENT_TYPE)) {
			resp.addHeader(HttpHeader.CONTENT_TYPE, "text/html; charset=utf-8");
//			resp.addHeader(HttpHeader.CONTENT_TYPE, "image/jpeg");
		}
		// Set the content length header if it's not already set
		if (!resp.getHeaders().containsKey(HttpHeader.CONTENT_LENGTH)) {
			resp.addHeader(HttpHeader.CONTENT_LENGTH, resp.getResponseLength());
		}
		// Copy in the headers
		for (Entry<String, List<Object>> entry : resp.getHeaders().entrySet()) {
			String headerName = HttpUtil.capitalizeHeader(entry.getKey());
			for(Object o : entry.getValue()) {
				header.append(headerName);
				header.append(':').append(' ');
				header.append(o);
				header.append('\r').append('\n');
			}
		}
		header.append('\r').append('\n');
		// Write the header
		output.write(header.toString().getBytes("UTF-8"));
		// Responses can be InputStreams or Strings
		if (resp.getResponse() instanceof InputStream) {
			// InputStreams will block the session thread (No big deal) and send
			// data without loading it into memory
			InputStream res = resp.getResponse();
			try {
				// Write the body
				byte[] buffer = new byte[1024];
				while (true) {
					int read = res.read(buffer, 0, buffer.length);
					if (read == -1)
						break;
					output.write(buffer, 0, read);
				}
			} finally {
				res.close();
			}
		} else if (resp.getResponse() instanceof String) {
			String responseString = (String) resp.getResponse();
			output.write(responseString.getBytes("UTF-8"));
		} else if (resp.getResponse() instanceof byte[]) {
			output.write((byte[]) resp.getResponse());
		}
		// Close it if required.
		if (close) {
			socket.close();
		}*/
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