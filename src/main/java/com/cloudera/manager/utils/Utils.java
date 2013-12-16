package com.cloudera.manager.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;




/**
 * The Class Utils.
 */
public class Utils {

    /**
     * The Constant DIR_SEP.
     */
    public final static String DIR_SEP = System.getProperty("file.separator");
    /**
     * The Constant NEW_LINE.
     */
    public final static String NEW_LINE = System.getProperty("line.separator");

    /**
     * Unzip config.
     *
     * @param zipFile the zip file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File unzipConfig(File zipFile) throws IOException {
        ZipInputStream zipIs = null;
        File basePath = createTempDirectory();
        File zipOutput = null;
        basePath.deleteOnExit();
        // FileUtils.getTempDirectory();
        try {
            zipIs = new ZipInputStream(new BufferedInputStream(
                    new FileInputStream(zipFile)));
            ZipEntry zipEntry;

            while ((zipEntry = zipIs.getNextEntry()) != null) {
                byte[] tmp = new byte[4 * 1024];

                String outputFilePath = basePath.getAbsolutePath() + DIR_SEP
                        + zipEntry.getName();

                zipOutput = new File(outputFilePath);
                @SuppressWarnings("Unused")
                boolean mkdirs = zipOutput.getParentFile().mkdirs();


                FileOutputStream fos = new FileOutputStream(outputFilePath);

                int size;
                while ((size = zipIs.read(tmp)) != -1) {
                    fos.write(tmp, 0, size);
                }
                fos.flush();
                fos.close();

            }
        } finally {
            if (null != zipIs)
                zipIs.close();
        }
        if (null == zipOutput)
            return null;
        return zipOutput.getParentFile();

    }

    /**
     * Creates the temp directory.
     *
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: "
                    + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: "
                    + temp.getAbsolutePath());
        }

        return (temp);
    }

    /**
     * Merge xml configuration files into one larger config file.
     *
     * @param dir      Where all the configuration files are located in one directory. Will merger all files that end with .xml
     * @param fileName The name of the final XML file. All spaces will be removed to make it easier to deal with final file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File mergeXMLConfigFiles(File dir, String fileName) throws IOException {

        if (null == dir)
            return null;


        File mergedFile = new File(Utils.createTempDirectory() + DIR_SEP
                + fileName.replaceAll("\\s+", ""));
        StringBuffer sb = new StringBuffer(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEW_LINE
                        + "<configuration>");
        File[] files = dir.listFiles();
        Pattern p = Pattern.compile("<configuration>(.*)</configuration>",
                Pattern.DOTALL);
        if (null != files) {
            for (File f : files) {

                if (f.toString().toLowerCase().endsWith(".xml")) {
                    String fileContents = fileContentsToString(f);

                    Matcher m = p.matcher(fileContents);
                    while (m.find()) {
                        sb.append(NEW_LINE);
                        sb.append("<!-- start of ");
                        sb.append(f.getName());
                        sb.append("-->");
                        sb.append(NEW_LINE);
                        sb.append(m.group(1));
                        sb.append(NEW_LINE);
                        sb.append("<!-- end of ");
                        sb.append(f.getName());
                        sb.append("-->");
                        sb.append(NEW_LINE);
                    }
                }
            }
        } else {
            sb.append("<!-- The directory was empty or not found and no files were found to merge -->");
        }
        sb.append(NEW_LINE);
        sb.append("</configuration>");
        FileUtils.write(mergedFile, sb, "UTF-8");
        return mergedFile;
    }

    /**
     * File contents to string.
     *
     * @param file the file
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static String fileContentsToString(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();

    }

    public static File downloadFilesFromDifferentHosts(Map<String, String> hosts, String path, String fileName) throws MalformedURLException, FileNotFoundException {

        File download = new File(path + DIR_SEP + fileName);
        for (Map.Entry<String, String> host : hosts.entrySet()) {
            URL url = new URL("http", host.getKey(), host.getValue() + path);
            try {
                FileUtils.copyURLToFile(url, download, 10000, 10000);
                return download;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        throw new FileNotFoundException("Unable to download " + fileName);
    }

    public static List<String> fileToStringList(File file) throws IOException {

        return FileUtils.readLines(file);

    }


    public static List<String> fileToStringList(InputStream is) throws IOException{
        List<String> stringList = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        String s;
        while((s=in.readLine() )!=null)
        {
            stringList.add(s);
        }


        return stringList;

    }

    public static HashMap<String, String> nexusReposFromFile(File file) throws IOException {

        HashMap<String, String> repos = new HashMap<String, String>();

        for (String s :  fileToStringList(file)) {


            String[] repo = s.split("\\s+");
            if (repo.length > 1) {
                repos.put(repo[0], repo[1]);
            }


        }
        return repos;

    }
    public static HashMap<String, String> nexusReposFromFile(InputStream is) throws IOException {

        HashMap<String, String> repos = new HashMap<String, String>();

        for (String s :  fileToStringList(is)) {


            String[] repo = s.split("\\s+");
            if (repo.length > 1) {
                repos.put(repo[0], repo[1]);
            }


        }
        return repos;

    }


    public static void downloadDependencies(String nexusRepoFile, String finalPathForFiles, String CDHVersion) throws URISyntaxException, IOException {
        HashMap<String, String> hosts = Utils.nexusReposFromFile(ClassLoader.getSystemResourceAsStream(nexusRepoFile));


        ArrayList<String> dependencies;
        dependencies = (ArrayList<String>) Utils.fileToStringList(ClassLoader.getSystemResourceAsStream(CDHVersion));
        File f;

            for (String path : dependencies) {
                f = Utils.downloadFilesFromDifferentHosts(hosts, finalPathForFiles, path.substring(path.lastIndexOf("/") + 1));
                System.out.println("Donwloaded " + f.getAbsolutePath());
            }


    }

}
