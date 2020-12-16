package studio.blacktech.furryblackplus.system.command;


import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

public class FriendCommand extends BasicCommand {


    private final Friend sender;


    public FriendCommand(Friend sender, MessageChain messages) {
        super(messages);
        this.sender = sender;
    }


    public Friend getSender() {
        return sender;
    }


}
