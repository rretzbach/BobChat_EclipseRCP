package bobchat.domain;

import jerklib.listeners.IRCEventListener;

public class Channel {

	private jerklib.Channel channel;
	private final String name;
	private final Network network;

	public Channel(Network network, String name) {
		this.network = network;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Network getNetwork() {
		return this.network;
	}

	public void sendText(String text) {
		this.channel.say(text);
	}

	public void setChannel(jerklib.Channel channel) {
		this.channel = channel;
	}

	public void setIRCEventListener(IRCEventListener listener) {
		this.channel.getSession().addIRCEventListener(listener);
	}

}
