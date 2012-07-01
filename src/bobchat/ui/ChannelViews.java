package bobchat.ui;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

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

	public void getPresenter(final ChannelView view) {
		String hostname = SecondaryIDCodec.extractHostname(view.getViewSite()
				.getSecondaryId());
		final String channelName = SecondaryIDCodec.extractChannelName(view
				.getViewSite().getSecondaryId());

		// temporary title as long as the channel was not yet joined
		view.setPartName("(" + channelName + ")");

		final Network network = Networks.getInstance().get(
		/* FIXME hostname */"irc.rizon.net");
		network.join(channelName);
	}

	public void showView(String hostname, String channelName)
			throws PartInitException {
		String buildSecondaryId = SecondaryIDCodec.buildSecondaryId(hostname,
				channelName);

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		ChannelView view = null;

		IViewReference[] viewReferences = activePage.getViewReferences();
		for (IViewReference iViewReference : viewReferences) {
			if (iViewReference.getSecondaryId().equals(buildSecondaryId)) {
				view = (ChannelView) iViewReference.getView(false);
				break;
			}
		}

		if (view == null) {
			view = (ChannelView) activePage.showView(ChannelView.ID,
					buildSecondaryId, IWorkbenchPage.VIEW_ACTIVATE);
		}

		new ChannelViewPresenter(Networks.getInstance().get(hostname)
				.getChannel(channelName), view);
	}
}
