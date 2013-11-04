package com.cloudera.manager.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.cloudera.api.ApiRootResource;
import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.v3.ClustersResourceV3;
import com.cloudera.api.v3.RootResourceV3;

public class CMUtilsTest {

	//These need to change to your cluster and yes I know this should be a config file ;)
	
	private final String CMHOSTNAME="woody-ec2-c5cluster-2.ent.cloudera.com";
	private final String USERNAME="read";
	private final String PASSWORD="readonly";
	
	@Test
	public void testGetConfigFilesStringStringString()  {
		
		try {
			HashMap<String,File> hm=CMUtils.getConfigFiles(CMHOSTNAME, USERNAME, PASSWORD);
			
			
			ApiRootResource root = new ClouderaManagerClientBuilder()
			.withHost(CMHOSTNAME).withUsernamePassword(USERNAME, PASSWORD)
			.build();
			RootResourceV3 v3 = root.getRootV3();
			ClustersResourceV3 clustersResource = v3.getClustersResource();
			
			Assert.assertEquals("Should return config for each cluster",clustersResource.readClusters(DataView.FULL).size(),hm.size());
			
			for(ApiCluster cluster:clustersResource.readClusters(DataView.FULL)){
				if(null!=hm.get(cluster.getName())){
				Assert.assertTrue("File for cluster: "+cluster.getName() +" should exist", hm.get(cluster.getName()).exists());
				}
				else
				{
					fail("Cluster " + cluster.getName() +"returned null file");
				}
			}
			
			
		} catch (IOException e) {
			
			fail("Test failed because of IOException "+e.getMessage());
		}
		
		
	}



}
