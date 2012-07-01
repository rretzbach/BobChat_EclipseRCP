package bobchat.extension;

import bobchat.domain.Channel;

public interface ICommand {

	void execute(Channel channel, String[] args);

}
