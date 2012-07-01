package bobchat.ui;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.events.IRCEvent;
import jerklib.events.NickChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import bobchat.domain.Channel;

// TODO clickable links
public class CompositeChatMessageAppender extends ChatMessageAppender {

	private static final Pattern URL_PATTERN = Pattern
			.compile("(?:https?|ftp)://\\S+");
	protected final Channel channel;
	private final ColoredNickAppender nickFormatter;

	public CompositeChatMessageAppender(StyledText messages, Channel channel) {
		super(messages);
		this.channel = channel;
		this.nickFormatter = new ColoredNickAppender(channel.getNetwork()
				.getNick());
	}

	@Override
	protected void appendMessage(String message) {
		this.styledText.append(message);

		// TODO support different link decorators
		Matcher matcher = URL_PATTERN.matcher(message);
		while (matcher.find()) {
			StyleRange styleRange = new StyleRange();
			styleRange.start = (this.styledText.getCharCount() - message
					.length()) + matcher.start();
			styleRange.length = matcher.end() - matcher.start();
			styleRange.underline = true;
			styleRange.underlineStyle = SWT.UNDERLINE_LINK;
			styleRange.data = matcher.group();
			this.styledText.setStyleRange(styleRange);
		}
	}

	@Override
	protected void appendNick(String nick) {
		this.nickFormatter.appendColoredNick(this.styledText, nick);
	}

	@Override
	protected void appendTimestamp(Date time) {
		super.appendTimestamp(time);
		this.styledText.append(" ");
	}

	@Override
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == IRCEvent.Type.NICK_CHANGE) {
			NickChangeEvent nce = (NickChangeEvent) e;
			this.nickFormatter.nickChanged(nce.getOldNick(), nce.getNewNick());
		}
	}

}
