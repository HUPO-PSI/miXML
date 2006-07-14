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

import java.util.HashMap;

/**
 * 
 * The bean that can keep the mapping information about the flat files.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class FlatFileMapping {

	/**
	 * @return Returns the lineSeparator.
	 * 
	 * @uml.property name="lineSeparator"
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * @param lineSeparator
	 *            The lineSeparator to set.
	 * 
	 * @uml.property name="lineSeparator"
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * @return Returns the separators.
	 * 
	 * @uml.property name="separators"
	 */
	public HashMap getSeparators() {
		return separators;
	}

	/**
	 * @param separators
	 *            The separators to set.
	 * 
	 * @uml.property name="separators"
	 */
	public void setSeparators(HashMap separators) {
		this.separators = separators;
	}

	/**
	 * 
	 * @uml.property name="fileURL"
	 */
	public String fileURL;

	/**
	 * line separator: if null, lines will be readed one by one in the file else
	 * the readline function will read the file untill it find the separator
	 * 
	 * @uml.property name="lineSeparator"
	 */
	public String lineSeparator = null;

	/**
	 * associate a path to a separator
	 * 
	 * @uml.property name="separators"
	 */
	public HashMap separators = new HashMap();

	/**
	 * 
	 * @uml.property name="fisrtLineForTitle"
	 */
	public boolean fisrtLineForTitle;

	/**
	 * @return Returns the fisrtLineForTitle.
	 * 
	 * @uml.property name="fisrtLineForTitle"
	 */
	public boolean isFisrtLineForTitle() {
		return fisrtLineForTitle;
	}

	/**
	 * @param fisrtLineForTitle
	 *            The fisrtLineForTitle to set.
	 * 
	 * @uml.property name="fisrtLineForTitle"
	 */
	public void setFisrtLineForTitle(boolean fisrtLineForTitle) {
		this.fisrtLineForTitle = fisrtLineForTitle;
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