package bobchat.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import bobchat.BC;

public class ColoredNickAppender {

	private final String nick;
	private final Map<String, Color> nickColors;

	public ColoredNickAppender(String nick) {
		this.nick = nick;
		this.nickColors = new HashMap<String, Color>();
		this.nickColors.put(nick, new Color(Display.getDefault(), 0, 0, 0));
	}

	public void appendColoredNick(StyledText styledText, String nick) {
		styledText.append(String.format("%24s", "<" + nick));

		StyleRange styleRange = new StyleRange();
		styleRange.start = styledText.getCharCount() - nick.length();
		styleRange.length = nick.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.underlineStyle = BC.USER;
		styleRange.data = nick;

		if (!this.nickColors.containsKey(nick)) {
			Color color = deriveColor(nick);
			this.nickColors.put(nick, color);
		}

		styleRange.foreground = this.nickColors.get(nick);

		styledText.setStyleRange(styleRange);

		styledText.append("> ");
	}

	private Color deriveColor(String nick) {
		return new Color(Display.getDefault(), new RGB(
				(float) (Math.random() * 360), 1f, 0.75f));
	}

	public void nickChanged(String oldNick, String newNick) {
		if (this.nickColors.containsKey(oldNick)) {
			this.nickColors.put(newNick, this.nickColors.get(oldNick));
			this.nickColors.remove(oldNick);
		}
	}

}
