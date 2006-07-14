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

import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * The bean that can keep the mapping information.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class Mapping {

	/**
	 * @return Returns the dictionaries.
	 * 
	 * @uml.property name="dictionaries"
	 */
	public ArrayList getDictionaries() {
		return dictionaries;
	}

	/**
	 * 
	 * @uml.property name="date"
	 */
	public Date date;

	/**
	 * @return Returns the flatFiles.
	 * 
	 * @uml.property name="flatFiles"
	 */
	public ArrayList getFlatFiles() {
		return flatFiles;
	}

	/**
	 * @param flatFiles
	 *            The flatFiles to set.
	 * 
	 * @uml.property name="flatFiles"
	 */
	public void setFlatFiles(ArrayList flatFiles) {
		this.flatFiles = flatFiles;
	}

	/**
	 * 
	 * @uml.property name="tree"
	 */
	public TreeMapping tree;

	/**
	 * 
	 * @uml.property name="dictionaries"
	 */
	public ArrayList dictionaries = new ArrayList();

	/**
	 * 
	 * @uml.property name="flatFiles"
	 */
	public ArrayList flatFiles = new ArrayList();

	/**
	 * @return Returns the date.
	 * 
	 * @uml.property name="date"
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            The date to set.
	 * 
	 * @uml.property name="date"
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the tree.
	 * 
	 * @uml.property name="tree"
	 */
	public TreeMapping getTree() {
		return tree;
	}

	/**
	 * @param tree
	 *            The tree to set.
	 * 
	 * @uml.property name="tree"
	 */
	public void setTree(TreeMapping tree) {
		this.tree = tree;
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

}