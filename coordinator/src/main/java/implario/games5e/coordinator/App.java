package implario.games5e.coordinator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import implario.Environment;
import implario.LoggerUtils;
import implario.games5e.coordinator.queue.QueueManager;
import implario.games5e.coordinator.queue.SimpleQueueManager;
import implario.games5e.coordinator.workers.Balancer;
import implario.games5e.coordinator.workers.GameStarter;
import implario.games5e.coordinator.workers.SimpleBalancer;
import implario.games5e.coordinator.workers.SimpleGameStarter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {

    @SneakyThrows
    public static void main(String[] args) {

        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = LoggerUtils.simpleLogger("").getHandlers();
        Field field = Logger.class.getDeclaredField("handlers");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") List<Handler> rootHandlers = (List<Handler>) field.get(rootLogger);
        rootHandlers.clear();
        rootHandlers.add(handlers[handlers.length - 1]);
//        Logger.getGlobal().addHandler(handlers[handlers.length - 1]);
        OptionParser optionParser = new OptionParser(true);
        OptionSpec<Integer> portSpec = optionParser.accepts("port").withRequiredArg().ofType(Integer.class);

        OptionSet options = optionParser.parse(args);

        Integer port = portSpec.value(options);
        if (port == null) port = Environment.getInt("GAMES5E_MINDER_PORT", 0);

        if (port == 0) {
            System.err.println("Please specify the port either with --port option or " +
                    "with GAMES5E_MINDER_PORT environment variable");
            return;
        }

        Injector injector = Guice.createInjector(binder -> {
            binder.bind(Scheduler.class).to(SimpleScheduler.class);
            binder.bind(Balancer.class).to(SimpleBalancer.class);
            binder.bind(GameStarter.class).to(SimpleGameStarter.class);
            binder.bind(QueueManager.class).to(SimpleQueueManager.class);
        });

        injector.getInstance(CoordinatorEndpoint.class).start(port);

    }

}
