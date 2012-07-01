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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bobchat.domain.Channel;

// TODO show topic somehow
public class ChannelViewPresenter {

	private final Channel channel;
	private final ChatMessageAppender formatter;

	public ChannelViewPresenter(final Channel channel, final ChannelView view) {
		this.channel = channel;

		view.setPartName(channel.getName());

		// click on button send sends the text in input textbox
		view.getSendButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = view.getInputText().getText();
				if (text.startsWith("/") && !text.startsWith("//")) {
					CommandInterpreter.execute(channel, text.substring(1));
				} else {
					text.replaceFirst("^//", "/");
					channel.sendText(text);
					handleMessage(channel.getNetwork().getNick(), text);
					view.getInputText().setText("");
				}
			}
		});

		this.formatter = new CompositeChatMessageAppender(
				view.getMessagesText(), channel);

		channel.setIRCEventListener(new IRCEventListener() {
			@Override
			public void receiveEvent(IRCEvent e) {
				ChannelViewPresenter.this.formatter.receiveEvent(e);
				if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
					MessageEvent me = (MessageEvent) e;
					handleMessage(me.getNick(), me.getMessage());
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

		view.getMessagesText().addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					int offset = view.getMessagesText().getOffsetAtLocation(
							new Point(event.x, event.y));
					StyleRange style = view.getMessagesText()
							.getStyleRangeAtOffset(offset);
					if ((style != null) && style.underline
							&& (style.underlineStyle == SWT.UNDERLINE_LINK)) {
						System.out.println("clicked on link "
								+ (String) style.data);
						org.eclipse.swt.program.Program
								.launch((String) style.data);
					}
				} catch (IllegalArgumentException e) {
					// no character under event.x, event.y
				}
			}
		});
	}

	protected void handleMessage(String nick, String message) {
		this.formatter.append(nick, message);
	}
}
