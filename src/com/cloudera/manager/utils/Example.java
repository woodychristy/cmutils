package com.cloudera.manager.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class Example {

	public static void main(String[] args) throws IOException {

		if(args.length==3){
			File bp = CMUtils.getConfigFiles(args[0], args[1], args[2]);

			System.out.println("Merged File exists at: "+Utils.mergeXMLConfigFiles(bp));
			FileUtils.deleteDirectory(bp);		
			
			
			
		
		
		}
		else{
			System.out.println("Usage: CMHostName userName password");
		}

	}

}
