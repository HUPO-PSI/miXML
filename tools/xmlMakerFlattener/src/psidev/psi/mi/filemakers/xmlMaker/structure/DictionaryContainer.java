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
package psidev.psi.mi.filemakers.xmlMaker.structure;

import java.util.ArrayList;

/**
 * This class displayed a TabbedPane that contains the TabFiles
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class DictionaryContainer {

	/**
	 * @return Returns the dictionaries.
	 * 
	 * @uml.property name="dictionaries"
	 */
	public ArrayList getDictionaries() {
		return dictionaries;
	}

	/**
	 * @param dictionaries
	 *            The dictionaries to set.
	 * 
	 * @uml.property name="dictionaries"
	 */
	public void setDictionaries(ArrayList dictionaries) {
		this.dictionaries = dictionaries;
	}

	/**
	 * 
	 * @uml.property name="dictionaries"
	 */
	public ArrayList dictionaries = new ArrayList();

	public Dictionary getFlatFile(int index) {
		return (Dictionary) dictionaries.get(index);
	}

	public void addDictionary(Dictionary d) {
		dictionaries.add(d);
	}

	/**
	 * returns the definition associated to a word in target dictionnary
	 * 
	 * @param dico
	 *            the dictionnary in which to look
	 * @param value
	 *            the value to search
	 * @param definitionNumber
	 *            the number of the definition to look for, as a value can have
	 *            more than one definition, i.e. the postition of the definition
	 *            on a line.
	 * @return the definition
	 */
	public String getReplacementValue(int dico, String value,
			int definitionNumber) {
		return ((Dictionary) dictionaries.get(dico)).getDefinition(value,
				definitionNumber);
	}

	/**
	 * the name of a dictionnary in the list
	 * 
	 * @param dico
	 *            index of the dictionnary in the list
	 * @return the name of the dictionnary selected on the list
	 */
	public String getName(int index) {
		return ((Dictionary) dictionaries.get(index)).toString();
	}
}