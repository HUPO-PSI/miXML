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
import java.util.HashMap;

/**
 * 
 * The bean that can keep the mapping information.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class TreeMapping {

	public String name = "";

	/**
	 * id for autogeneration: type MINT-001
	 *  
	 */
	public String id = "ID";

	/**
	 * keep references between an element and the column in the corresponding
	 * tab file
	 *  
	 */
	public HashMap associatedFields = new HashMap();

	public HashMap associatedDuplicableFields = new HashMap();

	/**
	 * associate a default value to a node
	 *  
	 */
	public HashMap associatedValues = new HashMap();

	/**
	 * associate a list dictionnary value to a node
	 *  
	 */
	public HashMap associatedOpenDictionary = new HashMap();

	/**
	 * associate a list dictionnary value to a node
	 *  
	 */
	public HashMap associatedClosedDictionary = new HashMap();

	/**
	 * associate the index of the column containing the replacement value (i.e.
	 * the postition of the definition on a line.) in the dictionnary associated
	 * to a node.
	 *  
	 */
	public HashMap associatedDictionaryColumn = new HashMap();

	/**
	 * list of the nodes for wich the value has to be generated
	 *  
	 */
	public ArrayList associatedAutogeneration = new ArrayList();

	/**
	 * list of the nodes for wich not use filters
	 *  
	 */
	public ArrayList unduplicableNodes = new ArrayList();

	/**
	 * list of the nodes at which are associated each flat file
	 *  
	 */
	public ArrayList associatedFlatFiles = new ArrayList();

	/**
	 * if this variable is setted to true (by the constructor) while expanding a
	 * node that discribe a choice, the tree will give to the user the
	 * possibility to choose what element to expand. Else all possibility is
	 * displayed
	 */
	public boolean manageChoices;

	/**
	 * if this variable is setted to true (by the constructor) while expanding a
	 * node, the tree will automaticaly create the minimum amount of nodes
	 * required according by the schema
	 *  
	 */
	public boolean autoDuplicate;

	/**
	 * this hashmap keep trace of choices made by user when expanding the tree.
	 * It is usefull for example in case of saving/loading . It associate a path
	 * (String) to a name.
	 *  
	 */
	public ArrayList expendChoices = new ArrayList();

	/**
	 * @return Returns the associatedAutogeneration.
	 *  
	 */
	public ArrayList getAssociatedAutogeneration() {
		return associatedAutogeneration;
	}

	/**
	 * @param associatedAutogeneration
	 *            The associatedAutogeneration to set.
	 *  
	 */
	public void setAssociatedAutogeneration(ArrayList associatedAutogeneration) {
		this.associatedAutogeneration = associatedAutogeneration;
	}

	/**
	 * @return Returns the associatedDictionaryColumn.
	 *  
	 */
	public HashMap getAssociatedDictionaryColumn() {
		return associatedDictionaryColumn;
	}

	/**
	 * @param associatedDictionaryColumn
	 *            The associatedDictionaryColumn to set.
	 *  
	 */
	public void setAssociatedDictionaryColumn(HashMap associatedDictionaryColumn) {
		this.associatedDictionaryColumn = associatedDictionaryColumn;
	}

	/**
	 * @return Returns the associatedFields.
	 *  
	 */
	public HashMap getAssociatedFields() {
		return associatedFields;
	}

	/**
	 * @param associatedFields
	 *            The associatedFields to set.
	 *  
	 */
	public void setAssociatedFields(HashMap associatedFields) {
		this.associatedFields = associatedFields;
	}

	/**
	 * @return Returns the associatedFlatFiles.
	 *  
	 */
	public ArrayList getAssociatedFlatFiles() {
		return associatedFlatFiles;
	}

	/**
	 * @param associatedFlatFiles
	 *            The associatedFlatFiles to set.
	 *  
	 */
	public void setAssociatedFlatFiles(ArrayList associatedFlatFiles) {
		this.associatedFlatFiles = associatedFlatFiles;
	}

	/**
	 * @return Returns the associatedValues.
	 *  
	 */
	public HashMap getAssociatedValues() {
		return associatedValues;
	}

	/**
	 * @param associatedValues
	 *            The associatedValues to set.
	 *  
	 */
	public void setAssociatedValues(HashMap associatedValues) {
		this.associatedValues = associatedValues;
	}

	/**
	 * @return Returns the autoDuplicate.
	 *  
	 */
	public boolean isAutoDuplicate() {
		return autoDuplicate;
	}

	/**
	 * @param autoDuplicate
	 *            The autoDuplicate to set.
	 */
	public void setAutoDuplicate(boolean autoDuplicate) {
		this.autoDuplicate = autoDuplicate;
	}

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
	 * @return Returns the id.
	 *  
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 *  
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the manageChoices.
	 *  
	 */
	public boolean isManageChoices() {
		return manageChoices;
	}

	/**
	 * @param manageChoices
	 *            The manageChoices to set.
	 *  
	 */
	public void setManageChoices(boolean manageChoices) {
		this.manageChoices = manageChoices;
	}

	/**
	 * @return Returns the name.
	 *  
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 *  
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String schemaURL;

	public String getSchemaURL() {
		return schemaURL;
	}

	public void setSchemaURL(String schemaURI) {
		this.schemaURL = schemaURI;
	}

	/**
	 * @return Returns the associatedDuplicableFields.
	 */
	public HashMap getAssociatedDuplicableFields() {
		return associatedDuplicableFields;
	}

	/**
	 * @param associatedDuplicableFields
	 *            The associatedDuplicableFields to set.
	 */
	public void setAssociatedDuplicableFields(HashMap associatedDuplicableFields) {
		this.associatedDuplicableFields = associatedDuplicableFields;
	}

	/**
	 * @return Returns the associatedClosedDictionary.
	 */
	public HashMap getAssociatedClosedDictionary() {
		return associatedClosedDictionary;
	}

	/**
	 * @param associatedClosedDictionary
	 *            The associatedClosedDictionary to set.
	 */
	public void setAssociatedClosedDictionary(HashMap associatedClosedDictionary) {
		this.associatedClosedDictionary = associatedClosedDictionary;
	}

	/**
	 * @return Returns the associatedOpenDictionary.
	 */
	public HashMap getAssociatedOpenDictionary() {
		return associatedOpenDictionary;
	}

	/**
	 * @param associatedOpenDictionary
	 *            The associatedOpenDictionary to set.
	 */
	public void setAssociatedOpenDictionary(HashMap associatedOpenDictionary) {
		this.associatedOpenDictionary = associatedOpenDictionary;
	}

	/**
	 * @return Returns the unduplicableNodes.
	 */
	public ArrayList getUnduplicableNodes() {
		return unduplicableNodes;
	}

	/**
	 * @param unduplicableNodes
	 *            The unduplicableNodes to set.
	 */
	public void setUnduplicableNodes(ArrayList unduplicableNodes) {
		this.unduplicableNodes = unduplicableNodes;
	}

	public HashMap validationRegexps = new HashMap();

	/**
	 * @return Returns the validationRegexps.
	 */
	public HashMap getValidationRegexps() {
		return validationRegexps;
	}

	/**
	 * @param validationRegexps
	 *            The validationRegexps to set.
	 */
	public void setValidationRegexps(HashMap validationRegexps) {
		this.validationRegexps = validationRegexps;
	}
}

