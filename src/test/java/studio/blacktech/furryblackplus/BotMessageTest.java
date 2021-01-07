package studio.blacktech.furryblackplus;


import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.system.command.Command;


public class BotMessageTest {


    @Test
    void test() {

        String[] sample = {
                "admin",
                "admin exec",
                "admin exec --module",
                "admin exec --module=shui",
                "admin exec --module=shui create",
                "admin exec --module=shui create `SELECT * FROM chat_record LIMIT 10`"
        };

        for (String temp : sample) {
            Command command = new Command(temp);
            System.out.println(command.toString());
        }

    }


}
