package com.cloudera.manager.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.fail;


public class UtilsTest {


    @Test
    public void testdownloadFilesFromDifferentHosts() throws IOException, URISyntaxException {
        HashMap<String, String> hosts = new HashMap<String, String>(2);
        hosts.put("repository.cloudera.com", "/artifactory/cloudera-repos/");
        hosts.put("repo1.maven.org", "/maven2/");



        String fileName = "hadoop-yarn-common-2.0.0-cdh4.4.0.jar";
        ArrayList<String> dependencies;
        dependencies = (ArrayList<String>) Utils.fileToStringList(new File(ClassLoader.getSystemResource("cdh4.4.0.dependencies").toURI()));

        File testFile = null;
        try {
            for (String path : dependencies) {
                testFile = Utils.downloadFilesFromDifferentHosts(hosts, path, path.substring(path.lastIndexOf("/")+1));
                Assert.assertTrue(testFile.exists());
                System.out.println(testFile.getAbsolutePath());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            fail("Test Download Files had an exception: " + e.getMessage());
        }
    }

    @Test
    public void testNexusReposFromFile() throws Exception {
        HashMap<String, String> hosts = new HashMap<String, String>(2);
        hosts.put("repository.cloudera.com", "/artifactory/cloudera-repos/");
        hosts.put("repo1.maven.org", "/maven2/");

        HashMap<String, String> testMap = Utils.nexusReposFromFile(new File(ClassLoader.getSystemResource("nexus.hosts").toURI()));

        for(String key : hosts.keySet()) {
            Assert.assertEquals("Keys and Contents should match between test map and loaded map ",  hosts.get(key),testMap.get(key));

        }


    }
}


