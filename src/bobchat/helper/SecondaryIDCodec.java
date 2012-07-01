package bobchat.helper;

public class SecondaryIDCodec {
	public static String buildSecondaryId(String hostname, String channelName) {
		return hostname + "|" + channelName;
	}

	public static String extractChannelName(String secondaryId) {
		return secondaryId.split("\\|")[1];
	}

	public static String extractHostname(String secondaryId) {
		return secondaryId.split("\\|")[0];
	}
}
