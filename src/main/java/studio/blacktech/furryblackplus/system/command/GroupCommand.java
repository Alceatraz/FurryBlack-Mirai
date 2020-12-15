package studio.blacktech.furryblackplus.system.command;


import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;

public class GroupCommand extends BasicCommand {


    private final Group group;
    private final Member sender;


    public GroupCommand(Group group, Member sender, MessageChain messages) {
        super(messages);
        this.group = group;
        this.sender = sender;
    }


    public Group getGroup() {
        return group;
    }


    public Member getSender() {
        return sender;
    }


}
