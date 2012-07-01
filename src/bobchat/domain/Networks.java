package bobchat.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.ConnectionLostEvent;
import jerklib.events.IRCEvent;
import jerklib.listeners.IRCEventListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class Networks {
	private static Networks instance;

	public static Networks getInstance() {
		if (instance == null) {
			instance = new Networks();
		}
		return instance;
	}

	private final ConnectionManager conman;
	private final List<Network> networks;
	private final String nick = "OstwindBC";

	private Networks() {
		this.conman = new ConnectionManager(new Profile(this.nick));
		this.networks = new ArrayList<>();
	}

	public Network find(String hostname) {
		for (Network network : this.networks) {
			if (network.getHostname().equals(hostname)) {
				return network;
			}
		}
		return null;
	}

	public Network get(String hostname) {
		Network network = find(hostname);
		if (network == null) {
			network = connectNetwork(hostname);
			register(network);
		}

		return network;
	}

	public void register(Network network) {
		if (!this.networks.contains(network)) {
			this.networks.add(network);
		}
	}

	// TODO allow multiple network reconnections
	private Network connectNetwork(final String hostname) {
		this.conman.requestConnection(hostname);

		final Session session = this.conman.getSession(hostname);
		final Network network = new Network(session, hostname);

		session.addIRCEventListener(new IRCEventListener() {
			@Override
			public void receiveEvent(IRCEvent e) {
				if (e.getType() == IRCEvent.Type.CONNECTION_LOST) {
					ConnectionLostEvent cle = (ConnectionLostEvent) e;
					// TODO inform properly about closed session
					System.out.println("Connection was lost");
					final Collection<IRCEventListener> eventListeners = session
							.getIRCEventListeners();

					final List<String> channels = new ArrayList<>();
					for (jerklib.Channel channel : session.getChannels()) {
						channels.add(channel.getName());
					}

					Job job = new Job("BobChat.job.reconnect") {
						private Session currentSession = null;
						private boolean running = true;
						protected long repeatDelay = 0;
						{
							this.repeatDelay = 60000;
						}

						@Override
						public boolean shouldSchedule() {
							return this.running;
						}

						public void stop() {
							this.running = false;
						}

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							System.out.println("attempting a reconnection");
							if (this.currentSession != null) {
								System.out
										.println("cleaning up previous reconnection attempt");
								Collection<IRCEventListener> currentListeners = new ArrayList<>(
										this.currentSession
												.getIRCEventListeners());
								for (IRCEventListener ircEventListener : currentListeners) {
									this.currentSession
											.removeIRCEventListener(ircEventListener);
								}

								this.currentSession.close("byebye");
							}

							this.currentSession = Networks.this.conman
									.requestConnection(hostname);

							System.out
									.println("listening for irc events on reconnection try");
							this.currentSession
									.addIRCEventListener(new IRCEventListener() {

										@Override
										public void receiveEvent(IRCEvent e) {
											System.out.println("event from reconnection loop"
													+ e.getType()
													+ "@"
													+ e.getRawEventData());

											if (e.getType() == IRCEvent.Type.ERROR) {
												schedule(repeatDelay);
											}

											if (e.getType() == IRCEvent.Type.CONNECT_COMPLETE) {
												stop();

												System.out
														.println("Reconnected");
												currentSession
														.removeIRCEventListener(this);

												network.setSession(currentSession);

												// reattach old listeners
												for (IRCEventListener ircEventListener : eventListeners) {
													currentSession
															.addIRCEventListener(ircEventListener);
												}

												for (String channelName : channels) {
													System.out
															.println("rejoining channel "
																	+ channelName);
													currentSession
															.join(channelName);
												}
											}
										}
									});

							return Status.OK_STATUS;
						}
					};

					job.schedule(10000);
				}
			}
		});

		return network;
	}
}
