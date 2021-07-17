package implario.games.node.loader.download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Downloader {

    public static void main(String[] args) {




        Map<String, String> parsed = parse(args);

        String repositoryURL = parsed.get("repository");
        String artifact = parsed.get("artifact");

        String user = null;
        String password = null;

        if(parsed.containsKey("user") && parsed.containsKey("password")) {
            user = parsed.get("user");
            password = parsed.get("password");
        }

        try {
            String[] mass = artifact.split(":");
            String url = repositoryURL + mass[0].replaceAll("\\.", "/") + "/" + mass[1] + "/";
            String latest = getLatestVersion(url, user, password);

            URL downloadFileUrl = new URL(url + latest + "/" + mass[1] + "-" + latest + ".jar");
            URL downloadFileMd5Url = new URL(url + latest + "/" + mass[1] + "-" + latest + ".jar.md5");

            File fileMd5 = new File("artifacts/" + mass[1] + "-" + latest + ".jar.md5");

            final File artifactsDir = new File("artifacts");

            if(!artifactsDir.exists()) {
                artifactsDir.mkdirs();
            } else {
                if(fileMd5.exists()) {
                    FileInputStream fis = new FileInputStream(fileMd5);

                    StringBuilder content = new StringBuilder();

                    Scanner scanner = new Scanner(fis);

                    while (scanner.hasNext()) {
                        content.append(scanner.next());
                    }

                    if(getData(downloadFileMd5Url, user, password).equals(content.toString())) {
                        System.out.println("This version is already downloaded");
                        return;
                    }
                }
            }

            download(downloadFileUrl, user, password);
            download(downloadFileMd5Url, user, password);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void download(URL url, String user, String password) {
        try {
            System.out.println("Downloading: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if(user != null && password != null) {
                String auth = user + ":" + password;
                connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)));
            }

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try(InputStream stream = connection.getInputStream()) {
                    String[] mass = url.toString().split("/");
                    byte[] buffer = new byte[stream.available()];
                    int bytesRead = -1;

                    File file = new File("artifacts/" + mass[mass.length - 1]);
                    file.createNewFile();

                    FileOutputStream outputStream = new FileOutputStream(file);

                    while((bytesRead = stream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String getLatestVersion(String url, String user, String password) {
        try {
            URL artifact = new URL(url + "latest");

            return getData(artifact, user, password);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static String getData(URL url, String user, String password) {
        try {
            System.out.println("Getting data from " + url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if(user != null && password != null) {
                String auth = user + ":" + password;
                connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)));
            }

            int responseCode = connection.getResponseCode();

            System.out.println(responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();

                try(InputStream stream = connection.getInputStream()) {
                    Scanner scanner = new Scanner(stream);

                    while(scanner.hasNext()) {
                        response.append(scanner.next());
                    }
                }

                return response.toString();
            } else {
                return null;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> parse(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                map.put(arg.replace("-", ""), args[i+1]);
            }
        }

        return map;
    }
}