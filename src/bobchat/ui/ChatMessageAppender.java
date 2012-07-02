package bobchat.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import jerklib.events.IRCEvent;
import jerklib.listeners.IRCEventListener;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class ChatMessageAppender implements IRCEventListener {

    protected StyledText styledText;

    public ChatMessageAppender(StyledText styledText) {
        this.styledText = styledText;
    }

    public final void append(final String nick, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                prepend();
                appendParts(new Date(), nick, message);
            }
        });

    }

    @Override
    public void receiveEvent(IRCEvent e) {
    }

    private void appendParts(Date time, String nick, String message) {
        appendTimestamp(time);
        appendNick(nick);
        appendMessage(message);
    }

    protected void appendMessage(String message) {
        this.styledText.append(message);
    }

    protected void appendNick(String nick) {
        this.styledText.append(String.format(" %25s ", nick));
    }

    protected void appendTimestamp(Date time) {
        this.styledText.append(new SimpleDateFormat("HH:mm:ss").format(time));
    }

    protected void prepend() {
        if (!ChatMessageAppender.this.styledText.getText().isEmpty()) {
            ChatMessageAppender.this.styledText.append("\n");
        }
    }

}
