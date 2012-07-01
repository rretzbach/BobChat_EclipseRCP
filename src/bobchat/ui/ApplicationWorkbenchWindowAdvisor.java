package bobchat.ui;

import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.listeners.IRCEventListener;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import bobchat.domain.Network;
import bobchat.domain.Networks;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	// FIXME hostname must be able to change
	private void openChannel(final String hostname, final String channelName) {
		final Network network = Networks.getInstance().get(hostname);

		network.addIRCEventListener(new IRCEventListener() {
			@Override
			public void receiveEvent(IRCEvent e) {
				System.out.print(e.getType().toString() + " ");
				System.out.println(e.getRawEventData());

				if (e.getType() == IRCEvent.Type.CONNECT_COMPLETE) {
					ConnectionCompleteEvent cce = (ConnectionCompleteEvent) e;
					network.join(channelName);
				}

				if (e.getType() == IRCEvent.Type.JOIN_COMPLETE) {
					final JoinCompleteEvent jce = (JoinCompleteEvent) e;
					network.getChannel(jce.getChannel().getName()).setChannel(
							jce.getChannel());
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								ChannelViews.getInstance().showView(hostname,
										jce.getChannel().getName());
							} catch (PartInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}

		});
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);

		// TODO read configuration/prefs
		openChannel("irc.iz-smart.net", "#vegan-without-magni");
	}
}
