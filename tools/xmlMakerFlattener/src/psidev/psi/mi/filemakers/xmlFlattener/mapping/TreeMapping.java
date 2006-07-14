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
package psidev.psi.mi.filemakers.xmlFlattener.mapping;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * This class is used as a Bean to keep all information about the mapping and
 * can be serialized in an XML file.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class TreeMapping {

	public ArrayList selections = new ArrayList();

	/**
	 * the separator for the flat file
	 * 
	 * @uml.property name="separator"
	 */
	public String separator = "|";

	public HashMap associatedNames = new HashMap();

	/**
	 * the node associated to a line of the flat file. if null, the printer will
	 * look for the deeper node that is an ancestor of every selection.
	 * 
	 * @uml.property name="lineNode"
	 */
	public String lineNode = null;

	/**
	 * @uml.property name="schemaFileName"
	 */
	public String schemaURL;

	/**
	 * @return Returns the lineNode.
	 */
	public String getLineNode() {
		return lineNode;
	}

	/**
	 * @param lineNode
	 *            The lineNode to set.
	 */
	public void setLineNode(String lineNode) {
		this.lineNode = lineNode;
	}

	/**
	 * @return Returns the schemaURI.
	 */
	public String getSchemaURL() {
		return schemaURL;
	}

	/**
	 * @param schemaURI
	 *            The schemaURI to set.
	 */
	public void setSchemaURL(String schemaURI) {
		this.schemaURL = schemaURI;
	}

	/**
	 * @return Returns the selections.
	 */
	public ArrayList getSelections() {
		return selections;
	}

	/**
	 * @param selections
	 *            The selections to set.
	 */
	public void setSelections(ArrayList selections) {
		this.selections = selections;
	}

	/**
	 * @return Returns the separator.
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            The separator to set.
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String documentURL;

	/**
	 * @return Returns the documentURL.
	 */
	public String getDocumentURL() {
		return documentURL;
	}

	/**
	 * @param documentURL
	 *            The documentURL to set.
	 */
	public void setDocumentURL(String documentURI) {
		this.documentURL = documentURI;
	}

	/**
	 * @return Returns the associatedNames.
	 */
	public HashMap getAssociatedNames() {
		return associatedNames;
	}

	/**
	 * @param associatedNames
	 *            The associatedNames to set.
	 */
	public void setAssociatedNames(HashMap associatedNames) {
		this.associatedNames = associatedNames;
	}

	/**
	 * this hashmap keep trace of choices made by user when expanding the tree.
	 * It is usefull for example in case of saving/loading . It associate a path
	 * (String) to a name.
	 */
	public ArrayList expendChoices = new ArrayList();

	/**
	 * @return Returns the expendChoices.
	 *  
	 */
	public ArrayList getExpendChoices() {
		return expendChoices;
	}

	/**
	 * @param expendChoices
	 *            The expendChoices to set.
	 *  
	 */
	public void setExpendChoices(ArrayList expendChoices) {
		this.expendChoices = expendChoices;
	}

	/**
	 * this map contains regular expression used to filter XML node if a node do
	 * not validate the regexp, itself or its parent element (in case of
	 * attribute) will be ignored
	 */
	public HashMap elementFilters = new HashMap();

	/**
	 * @return Returns the elementFilters.
	 */
	public HashMap getElementFilters() {
		return elementFilters;
	}

	/**
	 * @param elementFilters
	 *            The elementFilters to set.
	 */
	public void setElementFilters(HashMap elementFilters) {
		this.elementFilters = elementFilters;
	}

	public int numerotation_type;

	/**
	 * @return Returns the numerotation_type.
	 */
	public int getNumerotation_type() {
		return numerotation_type;
	}

	/**
	 * @param numerotation_type
	 *            The numerotation_type to set.
	 */
	public void setNumerotation_type(int numerotation_type) {
		this.numerotation_type = numerotation_type;
	}
}