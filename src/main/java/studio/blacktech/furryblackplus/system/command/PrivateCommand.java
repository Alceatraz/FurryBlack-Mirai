package studio.blacktech.furryblackplus.system.command;


import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;

public class PrivateCommand extends BasicCommand {


    private final Member sender;


    public PrivateCommand(Member sender, MessageChain messages) {
        super(messages);
        this.sender = sender;
    }


    public Member getSender() {
        return sender;
    }


}
