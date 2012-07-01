package bobchat.command;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import bobchat.domain.Channel;
import bobchat.extension.ICommand;

public class CommandInterpreter {

	private static final String ICOMMAND_ID = "BobChat.command";

	public static void execute(final Channel channel, final String text) {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ICOMMAND_ID);
		try {
			for (IConfigurationElement e : config) {
				final String keyword = e.getAttribute("keyword");

				if (!text.startsWith(keyword + " ")) {
					continue;
				}
				final String name = e.getAttribute("name");
				final Object o = e.createExecutableExtension("class");
				if (o instanceof ICommand) {
					final ICommand command = (ICommand) o;
					ISafeRunnable runnable = new ISafeRunnable() {
						@Override
						public void handleException(Throwable e) {
							System.out
									.println("Exception while executing Command extension "
											+ name);
						}

						@Override
						public void run() throws Exception {
							command.execute(
									channel,
									text.substring(keyword.length() + 1).split(
											"\\s+"));
						}
					};
					SafeRunner.run(runnable);
					break;
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
	}

}
