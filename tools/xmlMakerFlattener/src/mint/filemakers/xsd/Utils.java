/*  Copyright 2004 Arnaud CEOL

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.

 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package mint.filemakers.xsd;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cocoon.util.NetUtils;

/**
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class Utils {
	public static String lastVisitedDirectory = ".";

	public static String lastVisitedSchemaDirectory = null;

	public static String lastVisitedDictionaryDirectory = null;

	public static String lastVisitedFlatFileDirectory = null;

	public static String lastVisitedDocumentDirectory = null;

	public static String lastVisitedOutputDirectory = null;

	/**
	 * 
	 * @param url
	 * @return the relative url if it describes a file (ie: protocol is file and
	 *         not http)
	 */
	public static URL relativizeURL(URL url) {
		if (url == null)
			return url;
		if (url.getProtocol() == "http")
			return url;
		try {
			if (url.toString().startsWith(System.getProperty("user.dir"))) /*
																		    * relativize
																		    * only
																		    * if
																		    * the
																		    * file
																		    * is
																		    * on a
																		    * subpath
																		    */
				return new URL("file:"
						+ NetUtils.relativize("file:"
								+ System.getProperty("user.dir") + "/", url
								.toString()));
			else
				return url;
		} catch (MalformedURLException e) {
			System.out.println(e.toString());
			return url;
		}
	}

	/**
	 * 
	 * @param url
	 * @return the relative url if it describes a file (ie: protocol is file and
	 *         not http)
	 */
	public static URL absolutizeURL(URL url) {
		System.out.println("url:" + url);
		if (url == null)
			return url;
		if (url.getProtocol() == "http") {
			return url;
		}
		try {
			return new URL("file://" + url.toString());
		} catch (MalformedURLException e) {
			return url;
		}
	}

	public static URL absolutizeURL(String url) {
		try {
			if (url.startsWith("http:") || url.startsWith("file:")) {
				return new URL(url);
			} else {
				return new URL("file:" + url);
			}
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public static String getFileName(URL url) {
		return getFileName(url.getPath());
	}

	public static String getFileName(String url) {
		int index = url.lastIndexOf("/");
		if (index > 0)
			return url.substring(index + 1);
		return url;
	}

}