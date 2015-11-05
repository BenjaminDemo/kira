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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.junicorn.kira.http.HttpRequest;
import com.junicorn.kira.http.HttpSession;

/**
 * 服务主类
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public class KiraServer implements Runnable {
	
	private boolean isRunning = false;
	
	private Selector selector = null;
	
	private ServerSocketChannel server = null;
	
	private Kira kira;
	
	public KiraServer(Kira kira) {
		this.kira = kira;
		this.isRunning = kira.isRunning();
		this.selector = kira.getSelector();
		this.server = kira.getServer();
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
									kira.handle(new HttpRequest(session));
									session.close();
								}
							}
						}
					} catch (Exception ex) {
						System.err.println("Error handling client: " + key.channel());
						if (kira.isDebug()) {
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
				kira.shutdown();
				throw new RuntimeException(ex);
			}
		}
	}

}
