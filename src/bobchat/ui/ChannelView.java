package bobchat.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

// TODO show nick list
// TODO auto scrollbars
// TODO word wrap
// TODO highlights
// TODO up/down cycle through previous messages
// TODO use TextMetrics
// TODO join all channels on restore (currently only the active view is restored)
public class ChannelView extends ViewPart {
	public static final String ID = "BobChat.view"; //$NON-NLS-1$
	private Text inputText;
	private StyledText messagesText;
	private Button sendButton;

	private void createLayout(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		this.messagesText = new StyledText(parent, SWT.BORDER);
		this.messagesText.setEditable(false);
		this.messagesText.setWordWrap(true);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		this.messagesText.setLayoutData(layoutData);

		this.inputText = new Text(parent, SWT.BORDER);
		this.inputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));

		this.sendButton = new Button(parent, SWT.NONE);
		this.sendButton.setText("Send");
		this.sendButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1));
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		createLayout(parent);
		ChannelViews.getInstance().getPresenter(this);
	}

	public Text getInputText() {
		return this.inputText;
	}

	public StyledText getMessagesText() {
		return this.messagesText;
	}

	public Button getSendButton() {
		return this.sendButton;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		this.inputText.setFocus();
	}

	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}
}