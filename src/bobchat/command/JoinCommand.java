package bobchat.command;

import bobchat.domain.Channel;
import bobchat.extension.ICommand;

public class JoinCommand implements ICommand {

	public JoinCommand() {
	}

	@Override
	public void execute(Channel channel, String[] args) {
		channel.getNetwork().join(args[0]);
	}
}
