package implario.games.node.loader.download;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.net.URL;
import java.util.Scanner;

public class MavenImageDownloader {

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
        new MavenImageDownloader().provideImage("ru.cristalix", "client-api", "latest-SNAPSHOT");
    }

    @SneakyThrows
    public File provideImage(String groupId, String artifactId, String version) {

        groupId = groupId.replace('.', '/');

        String artifactUrl = "https://repo.implario.dev/public/" + groupId + "/" + artifactId + "/" + version;

        String mavenMetadata = readUrl(artifactUrl + "/maven-metadata.xml");

        Document document = documentBuilderFactory.newDocumentBuilder()
                .parse(artifactUrl + "/maven-metadata.xml");

        document.getDocumentElement().normalize();

        Node timestampNode = document.getElementsByTagName("timestamp").item(0);
        Node buildNumberNode = document.getElementsByTagName("buildNumber").item(0);

        System.out.println(timestampNode.getTextContent() + " " + buildNumberNode.getTextContent());

        String versionUrl = artifactUrl + "/" + artifactId + "-" + version + "-" + artifactUrl + "-" + timestampNode;

        String hash = readUrl(versionUrl + ".jar.md5");

        System.out.println(hash);

//        System.out.println(document.getDocumentElement().getNodeName());

        return null;
    }

    @SneakyThrows
    public static String readUrl(String url) {
        return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
    }

}
