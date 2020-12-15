package studio.blacktech.furryblackplus;


import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.system.command.BasicCommand;


@SuppressWarnings("deprecation")
public class BasicCommandTest {


    @Test
    void test() {

        String[] sample = {
                "/admin",
                "/admin exec",
                "/admin exec --module",
                "/admin exec --module=shui",
                "/admin exec --module=shui create",
                "/admin exec --module=shui create `SELECT * FROM chat_record LIMIT 10`"
        };

        for (String temp : sample) {
            BasicCommand basicCommand = new BasicCommand(temp);

            System.out.println(basicCommand.toString());
        }

    }


}
