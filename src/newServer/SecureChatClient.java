/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package newServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import newServer.datagram.ClientDatagram;
import newServer.datagram.ServerDatagram;

/**
 * Simple SSL chat client modified from {@link TelnetClient}.
 */
public final class SecureChatClient extends Application {

	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

	public static void main(String[] args) throws Exception {
		// Configure SSL.
		final SslContext sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new SecureChatClientInitializer(sslCtx));

			// Start the connection attempt.
			Channel ch = b.connect(HOST, PORT).sync().channel();

			// Read commands from the stdin.
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}

				if (line.equalsIgnoreCase("sdg")) {
					System.out.println("ok");
					lastWriteFuture = ch.writeAndFlush(new ServerDatagram("Hi"));
				} else if (line.equalsIgnoreCase("cdg")) {
					lastWriteFuture = ch.writeAndFlush(new ClientDatagram(line, line, null));
					ClientDatagram c = new ClientDatagram("hi", "bye", null);
				} else {
					// Sends the received line to the server.
					lastWriteFuture = ch.writeAndFlush(line + "\r\n");
				}

				// If user typed the 'bye' command, wait until the server closes
				// the connection.
				if ("bye".equals(line.toLowerCase())) {
					ch.closeFuture().sync();
					break;
				}
			}

			// Wait until all messages are flushed before closing the channel.
			if (lastWriteFuture != null) {
				lastWriteFuture.sync();
			}
		} finally

		{
			// The connection is closed automatically on shutdown.
			group.shutdownGracefully();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("SecuChat");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
