package implario.games5e.minder;

import dev.implario.nettier.NettierRemote;
import lombok.Data;

import java.util.List;

@Data
public class GameNode {

    private final NettierRemote remote;
    private final List<MindedGame> runningGames;

}
