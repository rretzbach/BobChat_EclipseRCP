package bobchat.service;


public class IRCService {
	private static IRCService instance;

	public static IRCService getInstance() {
		if (instance == null) {
			instance = new IRCService();
		}
		return instance;
	}

	private IRCService() {

	}

}
