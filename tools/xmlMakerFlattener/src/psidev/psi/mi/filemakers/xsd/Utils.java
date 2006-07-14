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
package psidev.psi.mi.filemakers.xsd;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.apache.cocoon.util.NetUtils;
/**
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class Utils {
	/** dimension for buttons */
	private static final Dimension buttonsDimension = new Dimension(60, 17);
		
	public static String lastVisitedDirectory = ".";

	public static void setDefaultSize(JButton button) {
		button.setPreferredSize(Utils.buttonsDimension);
		button.setMinimumSize(Utils.buttonsDimension);
		button.setMaximumSize(new Dimension(Short.MAX_VALUE, (short)Utils.buttonsDimension.getHeight()));	
	}
	
	
	public static String lastVisitedMappingDirectory = null;
	/**
	 * break a path down into individual elements and add to a list. example :
	 * if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
	 * 
	 * @param f
	 *            input file
	 * @return a List collection with the individual elements of the path in
	 *         reverse order
	 */
	private static List getPathList(File f) {
		List l = new ArrayList();
		File r;
		try {
			r = f.getCanonicalFile();
			while (r != null) {
				l.add(r.getName());
				r = r.getParentFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			l = null;
		}
		return l;
	}

	public static String lastMappingFile = null;
	public static String lastVisitedSchemaDirectory = null;

	public static String lastVisitedDictionaryDirectory = null;

	/**
	 * get relative path of File 'f' with respect to 'home' directory example :
	 * home = /a/b/c f = /a/d/e/x.txt s = getRelativePath(home,f) =
	 * ../../d/e/x.txt
	 * 
	 * @param home
	 *            base path, should be a directory, not a file, or it doesn't
	 *            make sense
	 * @param f
	 *            file to generate path for
	 * @return path from home to f as a string
	 */
	public static String getRelativePath(File home, File f) {
		File r;
		List homelist;
		List filelist;
		String s;
	
		homelist = getPathList(home);
		filelist = getPathList(f);
		s = matchPathLists(homelist, filelist);
	
		return s;
	}

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

	/**
	 * figure out a string representing the relative path of 'f' with respect to
	 * 'r'
	 * 
	 * @param r
	 *            home path
	 * @param f
	 *            path of file
	 */
	private static String matchPathLists(List r, List f) {
		int i;
		int j;
		String s;
		// start at the beginning of the lists
		// iterate while both lists are equal
		s = "";
		i = r.size() - 1;
		j = f.size() - 1;
	
		// first eliminate common root
		while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
			i--;
			j--;
		}
	
		// for each remaining level in the home path, add a ..
		for (; i >= 0; i--) {
			s += ".." + File.separator;
		}
	
		// for each level in the file path, add the path
		for (; j >= 1; j--) {
			s += f.get(j) + File.separator;
		}
	
		// file name
		s += f.get(j);
		return s;
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