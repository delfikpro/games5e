games5e (произносится gameservice) - сервис для быстрого развёртывания игр

API координатора позволяет запускать игры, получать информацию об играх, создавать очереди и добавлять в них игроков.

API воркеров позволяет разрабатывать геймплей, гораздо меньше задумываясь 
о развёртывании, упрощает запуск нескольких игр на одном сервере, 
а также умеет работать в автономном режиме (без координатора).

## Как работает games5e

### Очереди



## Как использовать games5e

### Разработка игр

Games5e довольно интенсивно использует библиотеку [bukkit-tools](https://github.com/implario/bukkit-tools), 
и для работы некоторых её функций необходима реализация платформы.

Добавить реализацию платформы можно либо установив соответствующую платформу как 
плагин в папку `plugins/`, либо зашейдив её внутрь вашего плагина и вызвав `Platforms.set(...)` вручную.

Разработка игр с геймсервисом не сильно отличается от обычной в плане структуры кода:

```java
@Override
public void onEnable() {
    
    Platforms.set(new PlatformDarkPaper());
    
    // Через объект GameNode можно создавать и запускать игры,
    // Получать местоположение игроков
    GameNode node = GameNode();
    
    // Когда координатор создаёт игру, он ищет подходящие ноды по префиксам.
    // Например наша нода поддерживает все типы билдбатла:
    // Добавляем префикс "build-battle", и координатор будет кидать нам всё, 
    // например "build-battle crazy", "build-battle normal", и т. д.
    node.supportedImagePrefixes.add("your-game");
    
    // Линкер регистрирует всякие штуки для изоляции игроков между играми
    // Также линкер отвечает за распределение игрока в игру
    // Дефолтный SessionBukkitLinker определяет игру при AsyncPlayerPreLoginEvent, 
    // Добавляет игрока в game.players при PlayerInitialSpawnEvent,
    // Изолирует чат, видимость игроков, отключает сообщения входа/выхода/серти,
    // А также пробрасывает Game#getSpawnLocation в эвенты входа и респавна
    node.setLinker(SessionBukkitLinker.link(node));
    
    // Инициализатор игры может вернуть null, и тогда координатор поймёт,
    // что игру создать не удалось.
    node.setGameCreator(YourGame::new);

    // Для тестирования мы можем создать игру напрямую 
    // (даже не подключая координатор)
    node.createGame(UUID.randomUUID(), null, null);
    
}
```

Классы игр имеют следующую структуру:

```java
public class YourGame extends Game {

    @Override
    public boolean acceptPlayer(AsyncPlayerPreLoginEvent event) {
        // Здесь нужно как-то решить, входит ли игрок на эту игру
    }
    
    @Override
    public Location getSpawnLocation(UUID playerId) {
        // Игроки этой игры должны где-то появляться при входе
    }
    
    public YourGame(UUID gameId, String imageId, JsonElement settings) {
        super(gameId);
        
        // Игры обычно проходят в каком-то месте в каком-то мире,
        // Например игра может прогрузить себе отдельный мир.
        World world = Bukkit.createWorld(...);
        
        // У каждой игры есть свой собственный контекст 
        // для эвентов и тасок шедулера.
        // Когда мы определились с местом протекания игры,
        // его нужно добавить в этот конткст
        context.appendOption(new WorldEventFilter(world));

        // Теперь можно слушать любые эвенты, и они будут вызываться
        // только для этого конкретного мира!
        context.on(PlayerJoinEvent.class, () -> {
            player.sendMessage("무궁화 꼬찌 피엇 소리다");
        });

    }

}
```

Заготовка кода (на котлине) для игры с кристаликсовской интеграцией геймсервиса:
```kotlin
class SquidGame(gameId: UUID): Game(gameId) {

    private val cristalix: Cristalix = Cristalix.connectToCristalix(this, "SQD", "Игра в Кальмара")

    private val map = WorldMeta(MapLoader().load("SquidGame", "game"))

    override fun acceptPlayer(event: AsyncPlayerPreLoginEvent) = cristalix.acceptPlayer(event)

    override fun getSpawnLocation(playerId: UUID): Location = map.getLabels("spawn")[0]

    init {

        context.appendOption(WorldEventFilter(map.world))

        context.on<PlayerJoinEvent> {
            player.sendMessage("무궁화 꼬찌 피엇 소리다")
        }
        
    }

}
```


Зависимости для быстрого старта (требуется доступ к репозиторию cristalix):
```groovy
    compileOnly 'cristalix:bukkit-core:21.01.30'
    compileOnly 'cristalix:dark-paper:21.02.03'
    compileOnly 'dev.xdark:feder:1.0-SNAPSHOT'
    implementation 'dev.implario.bukkit:bukkit-tools:4.4.0'
    // implementation 'dev.implario.bukkit:kotlin-api:1.1.1'
    implementation 'dev.implario.bukkit:dark-paper:1.0.0'
    implementation 'dev.implario.games5e:bukkit-worker-core:2.0.4'
    implementation 'ru.cristalix:games5e-integration:1.2.4'
```
