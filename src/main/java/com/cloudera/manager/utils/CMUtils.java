package com.cloudera.manager.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.cloudera.api.model.ApiParcel;
import com.cloudera.api.model.ApiParcelList;
import com.cloudera.api.v5.ClustersResourceV5;
import com.cloudera.api.v5.RootResourceV5;
import org.apache.commons.io.FileUtils;

import com.cloudera.api.ApiRootResource;
import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.model.ApiService;
import com.cloudera.api.v3.ClustersResourceV3;
import com.cloudera.api.v3.RootResourceV3;
import com.cloudera.api.v3.ServicesResourceV3;

/**
 * The Class CMUtils is used to
 */
public class CMUtils {

    public final static String PARCEL_STAGE_ACTIVATED="ACTIVATED";
    public final static String PRODUCT_CDH="CDH";
    public final static String UNKNOWN_VERSION="UNKNOWN";

	/**
	 * Gets the config files.
	 *
	 * @param hostname
	 *            the hostname where Cloudera Manager is running
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the config files base directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static HashMap<String, File> getConfigFiles(String hostname,
			String username, String password) throws IOException {

		ApiRootResource root = new ClouderaManagerClientBuilder()
				.withHost(hostname).withUsernamePassword(username, password)
				.build();

		if (root.getCurrentVersion().compareTo("v3") < 0) {
			throw new RuntimeException(
					"Cloudera Manager must support version 3 of REST API or higher and is currently at "
							+ root.getCurrentVersion());
		}

		RootResourceV3 v3 = root.getRootV3();

		ClustersResourceV3 clustersResource = v3.getClustersResource();

		HashMap<String, File> configFiles = new HashMap<String, File>(
				clustersResource.readClusters(DataView.FULL).size());
		for (ApiCluster cluster : clustersResource.readClusters(DataView.FULL)) {

			ServicesResourceV3 servicesResource = clustersResource
					.getServicesResource(cluster.getName());

			for (ApiService service : servicesResource
					.readServices(DataView.FULL)) {

				if (service.getType().equals("MAPREDUCE")) {

					configFiles.put(cluster.getName(), Utils.unzipConfig(getConfigFiles(servicesResource,
                            service.getName())));

				}

			}
		}
		return configFiles;
	}

	/**
	 * Gets the config files.
	 *
	 * @param servicesResource
	 *            the services resource
	 * @param serviceName
	 *            the service name
	 * @return the config files
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File getConfigFiles(ServicesResourceV3 servicesResource,
			String serviceName) throws IOException {
		InputStream is = servicesResource.getClientConfig(serviceName)
				.getInputStream();
		File f = new File(Utils.createTempDirectory() + Utils.DIR_SEP
				+ serviceName + ".zip");
		f.deleteOnExit();
		FileUtils.copyInputStreamToFile(is, f);
		return f;

	}



    public static HashMap<String, String> getCDHVersion(String hostname,
                                                        String username, String password) {

        ApiRootResource root = new ClouderaManagerClientBuilder()
                .withHost(hostname).withUsernamePassword(username, password)
                .build();
        RootResourceV5 v3 = root.getRootV5();

        ClustersResourceV5 clustersResource = v3.getClustersResource();

        HashMap<String, String> versions = new HashMap<String, String>(clustersResource.readClusters(DataView.FULL).size());
        for (ApiCluster cluster : clustersResource.readClusters(DataView.FULL)) {

           ApiParcelList ap= clustersResource.getParcelsResource(cluster.getName()).readParcels(DataView.FULL) ;

            for (ApiParcel p: ap){
                if(p.getStage().equals(PARCEL_STAGE_ACTIVATED)&& p.getProduct().equals(PRODUCT_CDH))
                {
                    versions.put(cluster.getName(), productVersionFromParcelVersion(p.getVersion()));
                    break;
                }



            }
            //couldn't find it set it to unknown
            if(!versions.containsKey(cluster.getName()))  {
                versions.put(cluster.getName(),UNKNOWN_VERSION);
            }
        }
        return versions;

    }
             private static String productVersionFromParcelVersion( String parcelVersion){
                 return parcelVersion.substring(0,parcelVersion.indexOf("-"));

             }

}
