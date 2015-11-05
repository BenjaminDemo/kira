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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.junicorn.kira.handler.RequestHandler;
import com.junicorn.kira.http.HttpRequest;
import com.junicorn.kira.http.HttpResponse;

import blade.kit.log.Logger;

public class Kira {

	private static final Logger LOGGER = Logger.getLogger(Kira.class);
	
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
	 * @param requestHandler
	 */
	public Kira addHandler(RequestHandler requestHandler) {
		handlers.add(requestHandler);
		return this;
	}

	/**
	 * 移除一个请求处理器
	 * 
	 * @param requestHandler
	 */
	public void removeRequestHandler(RequestHandler requestHandler) {
		handlers.remove(requestHandler);
	}
	
	/**
	 * 启动服务
	 */
	public void start(){
		this.execute(new KiraServer(this));
		LOGGER.info("Kira Server Listen on 0.0.0.0:" + server.socket().getLocalPort());
	}
	
	private ExecutorService executor;
    private Future<?> execute(Runnable runnable) {
        if (this.executor == null) {
            this.executor = Executors.newCachedThreadPool();
        }
        return executor.submit(runnable);
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
