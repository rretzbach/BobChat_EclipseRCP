package bobchat.domain;

import java.util.ArrayList;
import java.util.List;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;

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

	private Network connectNetwork(String hostname) {
		Session session = this.conman.requestConnection(hostname);
		Network network = new Network(session, hostname);
		return network;
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

}
