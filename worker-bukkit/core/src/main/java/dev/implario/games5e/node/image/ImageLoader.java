package dev.implario.games5e.node.image;

import com.google.gson.Gson;
import dev.implario.games5e.node.GameCreator;
import dev.implario.games5e.node.Game;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.UUID;
import java.util.jar.JarFile;

public class ImageLoader {

    public static GameImage load(File file) throws BadImageException {

        URLClassLoader classLoader = null;
        try {
            try {

                classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ImageLoader.class.getClassLoader());


                String mainClassName;

                try (JarFile jar = new JarFile(file)) {
                    mainClassName = jar.getManifest().getMainAttributes().getValue("Main-Class");
                    if (mainClassName == null) throw new NullPointerException("Main-Class");
                } catch (Exception ex) {
                    throw new BadImageException("Unable to get main class from manifest", ex);
                }

                Class<?> mainClass;
                try {

                    mainClass = Class.forName(mainClassName, true, classLoader);

                } catch (ClassNotFoundException e) {
                    throw new BadImageException("Unable to load main class " + mainClassName);
                }

                if (!Game.class.isAssignableFrom(mainClass))
                    throw new BadImageException("Main class " + mainClassName + " does not extend " + Game.class.getName());


                Constructor<?>[] constructors = mainClass.getConstructors();

                if (constructors.length == 0)
                    throw new BadImageException("No public default constructor is present in " + mainClassName);

                Constructor<?> constructor = constructors[0];

                // ToDo: Isn't this dirty?
                constructor.setAccessible(true);

                System.out.println(constructor.getParameterTypes()[0].getName());

                if (!constructor.isAccessible() ||
                        constructor.getParameterCount() != 2 ||
                        !constructor.getParameterTypes()[0].isAssignableFrom(UUID.class))
                    throw new BadImageException("No public constructor(UUID gameId, T settings) is present in " + mainClassName);

                GameCreator gameProvider = (gameId, image, gameSettings) -> {
                    try {
                        Class<?> settingsClass = constructor.getParameterTypes()[1];
                        Object settings = new Gson().fromJson(gameSettings, settingsClass);
                        return (Game) constructor.newInstance(gameId, settings);
                    } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                        throw new BadImageException("Unable to initialize main class " + mainClassName, e);
                    }
                };

                return new GameImage(classLoader, file, gameProvider);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException ignored) { }
            }
            throw e;
        }

    }

}
