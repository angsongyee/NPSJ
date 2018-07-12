package newServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SecureChatClientHandler2 extends SimpleChannelInboundHandler<Integer> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Integer msg) throws Exception {
		System.out.println("Integer msg received by handler: " + msg);
	}

}
