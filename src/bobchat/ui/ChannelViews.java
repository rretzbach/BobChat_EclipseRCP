package bobchat.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bobchat.domain.Channel;
import bobchat.domain.Network;
import bobchat.domain.Networks;
import bobchat.helper.SecondaryIDCodec;

public class ChannelViews {

	private static ChannelViews instance;

	public static ChannelViews getInstance() {
		if (instance == null) {
			instance = new ChannelViews();
		}
		return instance;
	}

	private ChannelViews() {

	}

	public ChannelViewPresenter getPresenter(ChannelView view) {
		String hostname = SecondaryIDCodec.extractHostname(view.getViewSite()
				.getSecondaryId());
		String channelName = SecondaryIDCodec.extractChannelName(view
				.getViewSite().getSecondaryId());

		ChannelViewPresenter channelViewPresenter = null;
		Network network = Networks.getInstance().get(hostname);
		Channel channel = network.getChannel(channelName);

		channelViewPresenter = new ChannelViewPresenter(channel, view);
		return channelViewPresenter;
	}

	public void showView(String hostname, String channelName)
			throws PartInitException {
		PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.showView(
						ChannelView.ID,
						SecondaryIDCodec
								.buildSecondaryId(hostname, channelName),
						IWorkbenchPage.VIEW_ACTIVATE);
	}
}
