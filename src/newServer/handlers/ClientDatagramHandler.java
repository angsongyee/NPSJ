package newServer.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import newServer.datagram.ClientDatagram;

public class ClientDatagramHandler extends SimpleChannelInboundHandler<ClientDatagram> {

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientDatagram msg) throws Exception {
		
		String recipient = msg.getRecipient();
		//get user and associated channel id;
		
//		for (Channel c: channels) {
//           if (c equals the recipient id whatever) {
//        	   c.writeAndFlush(msg);
//           }
//        }

	}

}
