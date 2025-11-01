/*
 * Copyright (C) 2021 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.jvidia.aimlbda.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
public class ResourceAccessUtils {

	private static final Logger log = LoggerFactory.getLogger(ResourceAccessUtils.class);

	private static final String SRC_MAIN_RESOURCE = "src/main/resources";

	public static void main(String[] args) {
		try {
			File file = getResourceFile("application.yml");
			log.info("application.yml: ", Arrays.toString(file.list()));
		}
		catch (Exception ex) {
			log.error("Error pringting file {}", ex);
		}
	}

	public ResourceAccessUtils() {
	}

	public static File getResourceFile(String filename) {
		File file = null;

        try {
            String res = File.separator + filename;
            URL resource = ResourceAccessUtils.class.getResource(filename);
            log.debug("File {} Url Created {}", res, resource);
			file = Paths.get(resource.toURI()).toFile();
		}
		catch (URISyntaxException ex) {
			log.error("Error getResourceFile filename {}", filename, ex);
		}

		return file;
	}

	public static File createResourceFile(String filename) {
		File file = null;

		try {
			String fileWithDir = new File(SRC_MAIN_RESOURCE).getAbsolutePath() + File.separator + filename;
			log.info("fileWithDir {}", fileWithDir);
			file = new File(fileWithDir);
			if (!file.exists()) {
				file.delete();
			}
			file.createNewFile();
			file.setWritable(true);
		}
		catch (IOException ex) {
			log.error("Error createResourceFile filename {}", filename, ex);
		}

		return file;
	}

}
