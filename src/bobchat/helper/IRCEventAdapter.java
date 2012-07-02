package bobchat.helper;

import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.ConnectionLostEvent;
import jerklib.events.ErrorEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.NickChangeEvent;
import jerklib.listeners.IRCEventListener;

public abstract class IRCEventAdapter implements IRCEventListener {

    public void onChannelMessage(MessageEvent e) {
    }

    public void onConnectComplete(ConnectionCompleteEvent e) {
    }

    public void onConnectionLost(ConnectionLostEvent e) {
    }

    public void onError(ErrorEvent e) {
    }

    public void onJoin(JoinEvent e) {
    }

    public void onJoinComplete(JoinCompleteEvent e) {
    }

    public void onNickChange(NickChangeEvent e) {
    }

    public void onPrivateMessage(MessageEvent e) {
    }

    @Override
    public void receiveEvent(IRCEvent e) {
        if (e.getType().equals(IRCEvent.Type.AWAY_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.CHANNEL_LIST_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.CHANNEL_MESSAGE)) {
            onChannelMessage((MessageEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.CONNECT_COMPLETE)) {
            onConnectComplete((ConnectionCompleteEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.CONNECTION_LOST)) {
            onConnectionLost((ConnectionLostEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.CTCP_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.DCC_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.DEFAULT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.ERROR)) {
            onError((ErrorEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.EXCEPTION)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.INVITE_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.JOIN)) {
            onJoin((JoinEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.JOIN_COMPLETE)) {
            onJoinComplete((JoinCompleteEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.KICK_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.MODE_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.MOTD)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.NICK_CHANGE)) {
            onNickChange((NickChangeEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.NICK_IN_USE)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.NOTICE)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.PART)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.PRIVATE_MESSAGE)) {
            onPrivateMessage((MessageEvent) e);
            return;
        }
        if (e.getType().equals(IRCEvent.Type.QUIT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.SERVER_INFORMATION)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.SERVER_VERSION_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.TOPIC)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.UPDATE_HOST_NAME)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.WHO_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.WHOIS_EVENT)) {

            return;
        }
        if (e.getType().equals(IRCEvent.Type.WHOWAS_EVENT)) {

            return;
        }
    }

}
