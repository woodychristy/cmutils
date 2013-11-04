package com.cloudera.manager.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class Example {

	public static void main(String[] args) throws IOException {

		if(args.length==3){
			HashMap<String,File >bp = CMUtils.getConfigFiles(args[0], args[1], args[2]);

			for(Map.Entry<String, File> ks: bp.entrySet()){
				System.out.println(ks.getKey()+" merged File exists at: "+Utils.mergeXMLConfigFiles(ks.getValue(),ks.getKey()+".xml"));
				FileUtils.deleteDirectory(ks.getValue());		
				
			}
			
		}
		else{
			System.out.println("Usage: CMHostName userName password");
		}

	}

}
