package implario.games.node.loader.download;

import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenImageProvider implements ImageProvider {

    // Repo url, group id, artifact id, version
    public static final Pattern mavenImagePattern = Pattern.compile("bukkit-maven (https?://[A-Za-z0-9.-_/]+) ([^:]+) ([^:]+) ([^:]+)");

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    {
        try {
            // Disable XXE
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new MavenImageProvider().provideImage("bukkit-maven https://repo.implario.dev/public ru.cristalix client-api latest-SNAPSHOT");
    }

    @SneakyThrows
    public File provideImage(String imageId) {

        Matcher matcher = mavenImagePattern.matcher(imageId);
        if (!matcher.matches()) {
            if (imageId.startsWith("bukkit-maven ")) {
                throw new IllegalArgumentException("invalid image id");
            }
            return null;
        }

        String repoUrl = matcher.group(1);
        String groupId = matcher.group(2);
        String artifactId = matcher.group(3);
        String version = matcher.group(4);

        groupId = groupId.replace('.', '/');
        if (repoUrl.endsWith("/")) repoUrl = repoUrl.substring(0, repoUrl.length() - 1);

        String artifactUrl = repoUrl + "/" + groupId + "/" + artifactId + "/" + version;

        String mavenMetadata = readUrl(artifactUrl + "/maven-metadata.xml");
        System.out.println(mavenMetadata);

        Document document = documentBuilderFactory.newDocumentBuilder()
                .parse(artifactUrl + "/maven-metadata.xml");

        document.getDocumentElement().normalize();

        Node timestampNode = document.getElementsByTagName("timestamp").item(0);
        Node buildNumberNode = document.getElementsByTagName("buildNumber").item(0);

        String buildNumber = buildNumberNode.getTextContent();
        String timestamp = timestampNode.getTextContent();

        String versionUrl = artifactUrl + "/" + artifactId + "-" + version.replace("-SNAPSHOT", "") + "-" + timestamp + "-" + buildNumber;

        String hash = readUrl(versionUrl + ".jar.md5");

        File file = new File("images", hash + ".jar");

        if (file.exists()) {
            System.out.println("Using cached image " + artifactId + ":" + version + ":" + hash);
            return file;
        }

        System.out.println("Downloading image " + artifactId + ":" + version + ":" + hash);
        file.getParentFile().mkdir();
        downloadFile(versionUrl + ".jar", file);

        return file;
    }

    @SneakyThrows
    public static String readUrl(String url) {
        return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
    }

    @SneakyThrows
    public static void downloadFile(String url, File file) {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, file.toPath());
        }
    }

}
