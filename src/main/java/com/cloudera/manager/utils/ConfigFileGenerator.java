package com.cloudera.manager.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileGenerator {

    public static void main(String[] args) throws IOException {

        if (args.length == 3) {
            //If you only want the files and not merged just call this
            HashMap<String, File> bp = CMUtils.getConfigFiles(args[0], args[1], args[2]);
           //For each directory merge all XML files
           //Ignore this section if you want standard files
            for (Map.Entry<String, File> ks : bp.entrySet()) {
                System.out.println(ks.getKey() + " merged File exists at: " + Utils.mergeXMLConfigFiles(ks.getValue(), ks.getKey() + ".xml"));
                FileUtils.deleteDirectory(ks.getValue());

            }

        } else {
            System.out.println("Usage: CMHostName userName password");
        }

    }

}
