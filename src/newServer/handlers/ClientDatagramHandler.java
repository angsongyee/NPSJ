package newServer.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import newServer.datagram.ClientDatagram;

public class ClientDatagramHandler extends SimpleChannelInboundHandler<ClientDatagram> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientDatagram msg) throws Exception {
		System.err.println("Client datagram received");
	}

}
