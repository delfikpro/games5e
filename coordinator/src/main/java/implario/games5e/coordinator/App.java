package implario.games5e.coordinator;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import implario.Environment;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

public class App {

    public static void main(String[] args) {

        OptionParser optionParser = new OptionParser(true);
        OptionSpec<Integer> portSpec = optionParser.accepts("port").withRequiredArg().ofType(Integer.class);

        OptionSet options = optionParser.parse(args);

        int port = portSpec.value(options);
        if (port == 0) port = Environment.getInt("GAMES5E_MINDER_PORT", 0);

        if (port == 0) {
            System.err.println("Please specify the port either with --port option or " +
                    "with GAMES5E_MINDER_PORT environment variable");
            return;
        }

        Injector injector = Guice.createInjector(binder -> {
            binder.bind(Balancer.class).to(SimpleBalancer.class);
        });
        injector.getInstance(CoordinatorEndpoint.class).start(port);

    }

}
