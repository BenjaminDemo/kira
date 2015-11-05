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
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.junicorn.kira.handler.RequestHandler;
import com.junicorn.kira.http.HttpRequest;
import com.junicorn.kira.http.HttpResponse;
import com.junicorn.kira.http.HttpSession;

/**
 * 服务主类
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public class Kira implements Runnable {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final CharsetEncoder ENCODER = CHARSET.newEncoder();
	private Selector selector = Selector.open();

	// socket服务
	private ServerSocketChannel server = ServerSocketChannel.open();

	// 默认运行
	private boolean isRunning = true;

	// debug模式
	private boolean debug = true;

	// 请求处理链
	private List<RequestHandler> handlers = new LinkedList<RequestHandler>();

	// 路由列表
	private List<String> routers = new ArrayList<String>();
	
	/**
	 * 创建一个Socket
	 * 
	 * @param address
	 * @throws IOException
	 */
	public void bind(InetSocketAddress address) throws IOException {
		server.socket().bind(address);
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void bind(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}

	/**
	 * 设置一个端口
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Kira(int port) throws IOException {
		this.bind(port);
	}

	/**
	 * 添加一个请求处理器
	 * 
	 * @param httpRequestHandler
	 */
	public Kira addHandler(RequestHandler httpRequestHandler) {
		handlers.add(httpRequestHandler);
		return this;
	}

	/**
	 * 移除一个请求处理器
	 * 
	 * @param httpRequestHandler
	 */
	public void removeRequestHandler(RequestHandler httpRequestHandler) {
		handlers.remove(httpRequestHandler);
	}

	public void start() {
		isRunning = true;
		Thread t = new Thread(this);
		t.setName("BiezhiHttpServer");
		t.start();
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				selector.selectNow();
				Iterator<SelectionKey> i = selector.selectedKeys().iterator();
				while (i.hasNext()) {
					SelectionKey key = i.next();
					i.remove();
					if (!key.isValid()) {
						continue;
					}
					try {
						// 获得新连接
						if (key.isAcceptable()) {
							// 接收socket
							SocketChannel client = server.accept();

							// 非阻塞模式
							client.configureBlocking(false);

							// 注册选择器到socket
							client.register(selector, SelectionKey.OP_READ);

							// 从连接上读
						} else if (key.isReadable()) {

							// 获取socket通道
							SocketChannel client = (SocketChannel) key.channel();
							// 获取回话
							HttpSession session = (HttpSession) key.attachment();

							// 如果没有则创建一个回话
							if (session == null) {
								session = new HttpSession(client);
								key.attach(session);
							}

							// 读取回话数据
							session.readData();

							// 消息解码
							String line;
							while ((line = session.read()) != null) {
								if (line.isEmpty()) {
									this.handle(new HttpRequest(session));
									session.close();
								}
							}
						}
					} catch (Exception ex) {
						System.err.println("Error handling client: " + key.channel());
						if (isDebug()) {
							ex.printStackTrace();
						} else {
							System.err.println(ex);
							System.err.println("\tat " + ex.getStackTrace()[0]);
						}
						if (key.attachment() instanceof HttpSession) {
							((HttpSession) key.attachment()).close();
						}
					}
				}
			} catch (IOException ex) {
				// 停止服务
				shutdown();
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * 处理请求
	 * 
	 * @param request	请求对象
	 * @throws IOException
	 */
	protected void handle(HttpRequest request) throws IOException {

		for (RequestHandler requestHandler : handlers) {
			HttpResponse resp = requestHandler.handle(request);
			if (resp != null) {
				request.getSession().sendResponse(resp);
				return;
			}
		}
	}

	/**
	 * 停止服务
	 */
	public void shutdown() {
		isRunning = false;
		try {
			selector.close();
			server.close();
		} catch (IOException ex) {
		}
	}

	public Selector getSelector() {
		return selector;
	}
	
	public ServerSocketChannel getServer() {
		return server;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isDebug() {
		return debug;
	}

	public void addRoute(String route) {
		routers.add(route);
		System.out.println("Add route:\t" + route);
	}

	public List<String> getRoutes() {
		return routers;
	}
}
