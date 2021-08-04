package studio.blacktech.furryblackplus.test;

import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.TreeCompleter.Node;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.widget.AutopairWidgets;

import java.util.ArrayList;
import java.util.List;

public class JLineTest {


    public static void main(String[] args) {


        List<Node> nodes1 = new ArrayList<>();
        nodes1.add(new Node(new StringsCompleter("enable"), new ArrayList<>()));
        nodes1.add(new Node(new StringsCompleter("disable"), new ArrayList<>()));

        List<Node> nodes2 = new ArrayList<>();
        nodes2.add(new Node(new StringsCompleter("import"), new ArrayList<>()));
        nodes2.add(new Node(new StringsCompleter("unload"), new ArrayList<>()));

        LineReader lineReader = LineReaderBuilder.builder().completer(

            new TreeCompleter(
                new Node(
                    new StringsCompleter("debug"),
                    nodes1
                ),
                new Node(
                    new StringsCompleter("module"),
                    nodes2
                )
            )

        ).build();

        AutopairWidgets autopairWidgets = new AutopairWidgets(lineReader);
        autopairWidgets.enable();

        while (true) {
            lineReader.readLine();
        }

    }

}
