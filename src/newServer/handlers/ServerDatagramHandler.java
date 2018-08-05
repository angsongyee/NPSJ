package newServer.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import newServer.datagram.ServerDatagram;

public class ServerDatagramHandler extends SimpleChannelInboundHandler<ServerDatagram> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ServerDatagram msg) throws Exception {
	}
}
