package implario.games5e.minder;

import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierServer;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Minder {

    public static void main(String[] args) {

        OptionParser optionParser = new OptionParser(true);
        OptionSpec<Integer> portSpec = optionParser.accepts("port").withRequiredArg().required().ofType(Integer.class);

        OptionSet options = optionParser.parse(args);

        Integer port = portSpec.value(options);

        NettierServer server = Nettier.createServer();



        server.start(port);

    }

}
