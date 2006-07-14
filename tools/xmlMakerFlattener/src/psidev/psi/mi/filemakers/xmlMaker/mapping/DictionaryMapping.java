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
package psidev.psi.mi.filemakers.xmlMaker.mapping;

/**
 * 
 * The bean that can keep the mapping information about the dictionaries.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class DictionaryMapping {

	/**
	 * 
	 * @uml.property name="fileURL"
	 */
	public String fileURL;

	/**
	 * 
	 * @uml.property name="caseSensitive"
	 */
	public boolean caseSensitive;

	/**
	 * 
	 * @uml.property name="separator"
	 */
	public String separator;

	/**
	 * @return Returns the caseSensitive.
	 * 
	 * @uml.property name="caseSensitive"
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive
	 *            The caseSensitive to set.
	 * 
	 * @uml.property name="caseSensitive"
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return Returns the separator.
	 * 
	 * @uml.property name="separator"
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            The separator to set.
	 * 
	 * @uml.property name="separator"
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return Returns the fileURI.
	 * 
	 * @uml.property name="fileURL"
	 */
	public String getFileURL() {
		return fileURL;
	}

	/**
	 * @param fileURI
	 *            The fileURI to set.
	 * 
	 * @uml.property name="fileURL"
	 */
	public void setFileURL(String fileURI) {
		this.fileURL = fileURI;
	}

}