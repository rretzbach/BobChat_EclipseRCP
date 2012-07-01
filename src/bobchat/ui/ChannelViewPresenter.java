package bobchat.ui;

import jerklib.events.IRCEvent;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.PartEvent;
import jerklib.events.QuitEvent;
import jerklib.listeners.IRCEventListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bobchat.BC;
import bobchat.command.CommandInterpreter;
import bobchat.domain.Channel;

// TODO show topic somehow
public class ChannelViewPresenter {

	private final Channel channel;
	private final ChatMessageAppender formatter;

	public ChannelViewPresenter(final Channel channel, final ChannelView view) {
		System.out.println("Create a presenter for channel "
				+ channel.getName() + " and view " + view.getPartName());
		this.channel = channel;

		view.setPartName(channel.getName());

		// click on button send sends the text in input textbox
		if (!view.getSendButton().isDisposed()) {
			view.getSendButton().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					sendInputText(channel, view);
				}
			});
		}

		view.getInputText().addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				sendInputText(channel, view);
			}
		});

		final StyledText messagesText = view.getMessagesText();
		this.formatter = new CompositeChatMessageAppender(messagesText, channel);

		channel.setIRCEventListener(new IRCEventListener() {
			@Override
			public void receiveEvent(IRCEvent e) {
				ChannelViewPresenter.this.formatter.receiveEvent(e);
				if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
					MessageEvent me = (MessageEvent) e;
					if (me.getChannel().getName().equals(channel.getName())) {
						handleMessage(me.getNick(), me.getMessage());
					}
				}
				if (e.getType() == IRCEvent.Type.JOIN) {
					JoinEvent je = (JoinEvent) e;
					System.out.println("Someone joined: " + je.getNick());
				}
				if (e.getType() == IRCEvent.Type.PART) {
					PartEvent pe = (PartEvent) e;
					System.out.println("Someone parted: " + pe.getWho());
				}
				if (e.getType() == IRCEvent.Type.QUIT) {
					QuitEvent qe = (QuitEvent) e;
					System.out.println("Someone quitted: " + qe.getNick());
				}
				if (e.getType() == IRCEvent.Type.NICK_CHANGE) {
					NickChangeEvent nce = (NickChangeEvent) e;
					System.out.println("Someone renamed from "
							+ nce.getOldNick() + " to " + nce.getNewNick());
				}
			}
		});

		messagesText.addListener(SWT.MouseHover, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point mousePoint = new Point(event.x, event.y);
				int offset;
				try {
					offset = messagesText.getOffsetAtLocation(mousePoint);
				} catch (IllegalArgumentException e) {
					// no character at position
					return;
				}
				StyleRange style = messagesText.getStyleRangeAtOffset(offset);
				if ((style != null) && (style.underlineStyle == BC.USER)) {
					final String nick = (String) style.data;
					float[] nickColor = style.foreground.getRGB().getHSB();
					final Color highlightColor = new Color(messagesText
							.getDisplay(), new RGB(nickColor[0], 0.4f, 0.8f));

					// TODO set specific bg color
					final Color backgroundColor = new Color(messagesText
							.getDisplay(), 255, 255, 255);

					// TODO only affect visible lines
					final StyleRange[] styleRanges = messagesText
							.getStyleRanges();
					messagesText.addListener(SWT.MouseMove, new Listener() {
						@Override
						public void handleEvent(Event event) {
							messagesText.removeListener(SWT.MouseMove, this);

							highlightLines(messagesText, nick, backgroundColor,
									styleRanges);
						}
					});

					highlightLines(messagesText, nick, highlightColor,
							styleRanges);
				}
			}

			private void highlightLines(final StyledText messagesText,
					final String nick, final Color highlightColor,
					final StyleRange[] styleRanges) {
				for (StyleRange styleRange : styleRanges) {
					if ((styleRange.underlineStyle == BC.USER)
							&& styleRange.data.equals(nick)) {
						int lineIndex = messagesText
								.getLineAtOffset(styleRange.start);
						int beginLineOffset = messagesText
								.getOffsetAtLine(lineIndex);
						int lineLength = messagesText.getLine(lineIndex)
								.length();

						StyleRange hilightRange = new StyleRange();
						hilightRange.start = beginLineOffset;
						hilightRange.length = lineLength;
						hilightRange.background = highlightColor;
						messagesText.setStyleRange(hilightRange);

						// reapply old styles
						StyleRange oldStyleWithBG = new StyleRange(styleRange);
						// fontstyle is a StyleRange property and is not
						// included in TextStyle-copy-method
						oldStyleWithBG.fontStyle = styleRange.fontStyle;
						oldStyleWithBG.background = highlightColor;
						oldStyleWithBG.start = styleRange.start;
						oldStyleWithBG.length = styleRange.length;

						messagesText.setStyleRange(oldStyleWithBG);
					}
				}
			}
		});

		messagesText.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					int offset = messagesText.getOffsetAtLocation(new Point(
							event.x, event.y));
					StyleRange style = messagesText
							.getStyleRangeAtOffset(offset);
					if ((style != null) && style.underline
							&& (style.underlineStyle == SWT.UNDERLINE_LINK)) {
						org.eclipse.swt.program.Program
								.launch((String) style.data);
					}
				} catch (IllegalArgumentException e) {
					// no character under event.x, event.y
				}
			}
		});
	}

	private void sendInputText(final Channel channel, final ChannelView view) {
		String text = view.getInputText().getText();
		if (text.trim().isEmpty()) {
			return;
		}
		if (text.startsWith("/") && !text.startsWith("//")) {
			CommandInterpreter.execute(channel, text.substring(1));
		} else {
			text.replaceFirst("^//", "/");
			channel.sendText(text);
			handleMessage(channel.getNetwork().getNick(), text);
		}
		view.getInputText().setText("");
	}

	protected void handleMessage(String nick, String message) {
		this.formatter.append(nick, message);
	}
}
