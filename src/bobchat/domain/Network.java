package bobchat.domain;

import java.util.HashMap;
import java.util.Map;

import jerklib.ConnectionManager;
import jerklib.Session;
import jerklib.listeners.IRCEventListener;

public class Network {
	private final Map<String, Channel> channels;

	private ConnectionManager conman;

	private final String hostname;

	private final String nick;

	private final Session session;

	public Network(Session session, String hostname) {
		this.session = session;
		this.hostname = hostname;
		this.nick = session.getNick();
		this.channels = new HashMap<>();
	}

	public void addIRCEventListener(IRCEventListener listener) {
		this.session.addIRCEventListener(listener);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Network other = (Network) obj;
		if (this.hostname == null) {
			if (other.hostname != null) {
				return false;
			}
		} else if (!this.hostname.equals(other.hostname)) {
			return false;
		}
		return true;
	}

	public Channel getChannel(String channelName) {
		return this.channels.get(channelName);
	}

	public String getHostname() {
		return this.hostname;
	}

	public String getNick() {
		return this.session.getNick();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((this.hostname == null) ? 0 : this.hostname.hashCode());
		return result;
	}

	public Channel join(String channelName) {
		Channel channel = new Channel(this, channelName);
		this.channels.put(channelName, channel);
		this.session.join(channelName);
		return channel;
	}
}
