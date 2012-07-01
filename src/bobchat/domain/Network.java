package bobchat.domain;

import java.util.HashMap;
import java.util.Map;

import jerklib.ConnectionManager;
import jerklib.Session;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.listeners.IRCEventListener;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;

import bobchat.ui.ChannelViews;

public class Network {
	private final Map<String, Channel> channels;

	private ConnectionManager conman;

	private final String hostname;

	private final String nick;

	private Session session;

	public Network(Session session, final String hostname) {
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

	public Session getSession() {
		return this.session;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((this.hostname == null) ? 0 : this.hostname.hashCode());
		return result;
	}

	public boolean isConnected() {
		return this.session.isConnected();
	}

	public void join(final String channelName) {
		if (this.channels.containsKey(channelName)) {
			return;
		}

		this.channels.put(channelName, null);

		final Session currentSession = this.session;

		currentSession.addIRCEventListener(new IRCEventListener() {
			@Override
			public void receiveEvent(IRCEvent e) {
				if (e.getType() == IRCEvent.Type.JOIN_COMPLETE) {
					removeIRCEventListener(this);
					final JoinCompleteEvent jce = (JoinCompleteEvent) e;

					if (jce.getChannel().getName().equals(channelName)) {
						Channel channel = new Channel(Network.this, channelName);
						Network.this.channels.put(channelName, channel);

						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									ChannelViews.getInstance().showView(
											"irc.rizon.net"
											/* FIXME Network.this.hostname */,
											jce.getChannel().getName());
								} catch (PartInitException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				}
			}
		});

		if (!isConnected()) {
			currentSession.addIRCEventListener(new IRCEventListener() {
				@Override
				public void receiveEvent(IRCEvent e) {
					if (e.getType() == IRCEvent.Type.CONNECT_COMPLETE) {
						ConnectionCompleteEvent cce = (ConnectionCompleteEvent) e;
						currentSession.join(channelName);
						removeIRCEventListener(this);
					}
				}
			});
		} else {
			currentSession.join(channelName);
		}
	}

	public void removeIRCEventListener(IRCEventListener listener) {
		this.session.removeIRCEventListener(listener);
	}

	public void setSession(Session newSession) {
		this.session = newSession;
	}
}
