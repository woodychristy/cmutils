package com.cloudera.manager.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


public class DependencyDownloader {
    public static void main(String[] args) throws IOException, URISyntaxException {

        if (args.length == 2) {

            FileUtils.forceMkdir(new File(args[0]));
            Utils.downloadDependencies("nexus.hosts",args[0],args[1]);

        } else {
            System.out.println("Usage: downloadPath CDHVersion");
            System.out.println("Example: /path/to/download/too  4.3.0");
        }

    }
}
