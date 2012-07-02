package bobchat.domain;

import jerklib.listeners.IRCEventListener;

public class Channel {

    private final String name;
    private final Network network;

    public Channel(Network network, String name) {
        this.network = network;
        this.name = name;
    }

    public void addIRCEventListener(IRCEventListener listener) {
        this.network.getSession().addIRCEventListener(listener);
    }

    public String getName() {
        return this.name;
    }

    public Network getNetwork() {
        return this.network;
    }

    public void sendText(String text) {
        this.network.getSession().getChannel(this.name).say(text);
    }

    @Deprecated
    public void setIRCEventListener(IRCEventListener listener) {
        this.network.getSession().addIRCEventListener(listener);
    }

}
