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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import psidev.psi.mi.filemakers.xmlMaker.mapping.TreeMapping;
import psidev.psi.mi.filemakers.xsd.FileMakersException;
import psidev.psi.mi.filemakers.xsd.MessageManagerInt;
import psidev.psi.mi.filemakers.xsd.Utils;
import psidev.psi.mi.filemakers.xsd.XsdNode;

/**
 * 
 * This class overides the abstract class AbstractXslTreeStruct to provide a
 * tree representation of a XML schema, with management of marshalling of
 * several flat files to a xml file that respects the schema
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XsdTreeStructImpl extends
		psidev.psi.mi.filemakers.xsd.AbstractXsdTreeStruct {

	/** keep the number of the line curently parsed */
	private int lineNumber = 0;
	
	private final static int marshalling = 0;

	private final static int checking = 1;

	/**
	 * use when checking a node. if checkMode is marshalling, the checking is
	 * done on the value for the node, if checkMode is checking, just check if
	 * the node is associated or not.
	 */
	private static int checkMode = checking;

	/**
	 * if set to false, XML code will not been checked
	 */
	public boolean checkXml = false;

	/** TODO: give choice for checkin' XML */

	/**
	 * current indentation in the XML document: a string containing only
	 * tabulations
	 */
	private String indentation = "";

	/**
	 * Observer for the marshalling
	 */
	public MarshallingObservable observable = new MarshallingObservable();

	/**
	 * 
	 * @uml.property name="dictionaries"
	 */
	public DictionaryContainer dictionaries = new DictionaryContainer();

	public ArrayList unduplicableNodes = new ArrayList();

	public HashMap validationRegexps = new HashMap();

	public String getRegexp(XsdNode node) {
		if (validationRegexps.containsKey(node))
			return (String) validationRegexps.get(node);
		return "";
	}

	private String pathFilter;

	/**
	 * 
	 * @uml.property name="flatFiles"
	 */
	public FlatFileContainer flatFiles = new FlatFileContainer();

	/**
	 * 
	 * @uml.property name="name"
	 */
	public String name = "";

	/**
	 * id for autogeneration: type MINT-001
	 * 
	 * @uml.property name="id"
	 */
	public String id = "";

	/**
	 * last id used
	 * 
	 * @uml.property name="lastId"
	 */
	public int lastId = 0;

	/**
	 * text area for warnings and error messages
	 * 
	 * @uml.property name="associatedFields"
	 */
	public HashMap associatedFields = new HashMap();

	/**
	 * 
	 * @uml.property name="associatedDuplicableFields"
	 */
	public HashMap associatedDuplicableFields = new HashMap();

	/**
	 * keep current values for referenced fields
	 * 
	 * @uml.property name="associatedValues"
	 */
	public HashMap associatedValues = new HashMap();

	/**
	 * associate a list dictionnary value to a node. The original value will be
	 * kept for this node if the value is not found in the dictionary.
	 * 
	 * @uml.property name="associatedDictionary"
	 */
	public HashMap associatedOpenDictionary = new HashMap();

	/**
	 * associate a list dictionnary value to a node. No value will be returned
	 * for this node if the value is not found in the dictionary.
	 * 
	 * @uml.property name="associatedDictionary"
	 */
	public HashMap associatedClosedDictionary = new HashMap();

	/**
	 * associate the index of the column containing the replacement value (i.e.
	 * the postition of the definition on a line.) in the dictionnary associated
	 * to a node.
	 * 
	 * @uml.property name="associatedDictionaryColumn"
	 */
	public HashMap associatedDictionaryColumn = new HashMap();

	/**
	 * list of the nodes for wich the value has to be generated
	 * 
	 * @uml.property name="associatedAutogeneration"
	 */
	public ArrayList associatedAutogeneration = new ArrayList();

	/**
	 * list of the nodes at which are associated each flat file
	 * 
	 * @uml.property name="associatedFlatFiles"
	 */
	public ArrayList associatedFlatFiles = new ArrayList();

	public ArrayList flatFilesStack = new ArrayList();

	public FlatFile getCurrentFlatFile() {
		if (flatFilesStack.size() > 0)
			return (FlatFile) flatFilesStack.get(flatFilesStack.size() - 1);
		return null;
	}

	/**
	 * create a new instance of XslTree The nodes will be automaticaly
	 * duplicated if the schema specify that more than one element of this type
	 * are mandatory
	 */
	public XsdTreeStructImpl() {
		//		super(true, true);
		super(false, true);
		associatedFlatFiles.add(null);
	}

	/**
	 * this method should reinitialize every variable makin reference to the
	 * actual tree, such as any <code>List</code> used to make associations to
	 * externals objects.
	 * 
	 * reinitializes associations of nodes with columns, default values,
	 * dictionnaries, autogeneration of value and associations to flat files
	 */
	public void emptySelectionLists() {
		associatedFields = new HashMap();
		associatedValues = new HashMap();
		associatedClosedDictionary = new HashMap();
		associatedOpenDictionary = new HashMap();
		associatedDictionaryColumn = new HashMap();
		associatedAutogeneration = new ArrayList();
		associatedFlatFiles = new ArrayList();
		expendChoices = new ArrayList();
	}

	/**
	 * set the FlatFile in which getting the values
	 * 
	 * @param f
	 *            a FlatFile
	 */
	public void pushFlatFile(FlatFile f) {
		//        getMessageManager().sendMessage("push " + f.fileURL.toString());
		flatFilesStack.add(f);
	}

	public void popFlatFile() {
		//        getMessageManager().sendMessage("pop ");
		flatFilesStack.remove(flatFilesStack.size() - 1);
	}

	/**
	 * Check if a path is not the subPath of another one
	 * 
	 * @param path1
	 * @param path2
	 * @return
	 */
	public boolean areSubPaths(TreeNode[] path1, TreeNode[] path2) {
		int minLength;
		if (path1.length < path2.length)
			minLength = path1.length;
		else
			minLength = path2.length;

		for (int i = 0; i < minLength; i++) {
			if (path1[i] != path2[i])
				return true;
		}
		return false;
	}

	/**
	 * Check if the node has a root node for ancestor. It is usefull when
	 * associating a node to a flat file as two root nodes (nodes associated to
	 * a flat file) should not have children in common
	 * 
	 * @param node
	 * @return
	 */
	public boolean isChildOfRootPaths(XsdNode node) {
		TreeNode[] path = node.getPath();

		for (int i = 0; i < associatedFlatFiles.size(); i++) {
			if (associatedFlatFiles.get(i) != null
					&& !areSubPaths(path,
							((XsdNode) associatedFlatFiles.get(i)).getPath()))
				return false;
		}

		return true;
	}

	/**
	 * associate the node selected to the FlatFile selected in the associated
	 * FlatFileTabbedPanel.
	 *  
	 */
	public void associateFlatFile(XsdNode node, int flatFile) {

		XsdNode previousAssociation = null;

		/*
		 * if the file was already associated, warn the user that all
		 * associations to this file will be lost
		 */
		while (associatedFlatFiles.size() <= flatFile) {
			associatedFlatFiles.add(null);
		}

		previousAssociation = (XsdNode) associatedFlatFiles.get(flatFile);

		int previousFlatfileAssociated = associatedFlatFiles.indexOf(node);
		if (previousFlatfileAssociated > -1)
			associatedFlatFiles.set(previousFlatfileAssociated, null);

		check((XsdNode) treeModel.getRoot());
		if (previousAssociation != null)
			treeModel.reload(previousAssociation);

		associatedFlatFiles.set(flatFile, node);
//		System.out.println(flatFile+ "#"+ node.getName());
		/* root node is mandatory */
		rootNode.use();
	}

	/**
	 * associate a default value to the node selected
	 */
	public void associateDefaultValue(XsdNode node, String value) {
		cancelAllAssociations(node);

		associatedValues.put(node, value);
		node.useOnlyThis();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	/**
	 * associate a dictionnary to the node selected. Each time a value will be
	 * requested for this node, it will be changed for its replacement value in
	 * target list if it exists
	 */
	public void associateDictionnary(XsdNode node, int dictionary, int column,
			boolean closedAssociation) {
		associatedClosedDictionary.remove(node);
		associatedOpenDictionary.remove(node);
		if (closedAssociation)
			associatedClosedDictionary.put(node, new Integer(dictionary));
		else
			associatedOpenDictionary.put(node, new Integer(dictionary));
		associatedDictionaryColumn.put(node, new Integer(column));
	}

	/**
	 * associate the node selected to a cell by its pat representation
	 *  
	 */
	public void associateField(XsdNode node, String path,
			boolean isUnduplicableAssociation) {
		cancelAllAssociations(node);
		boolean error = false;
		associatedFields.put(node, path);
		if (isUnduplicableAssociation)
			unduplicableNodes.add(node);
		node.use();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	public void associateField(XsdNode node, String path) {
		associateField(node, path, false);
	}

	public void associateValidationRegexp(XsdNode node, String regexp) {
		validationRegexps.put(node, regexp);
	}

	public void associateDuplicableField(XsdNode node, String path) {
		boolean error = false;
		associatedDuplicableFields.put(node, path);
		node.use();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	/**
	 * removes the association of the node selected with a dictionnary
	 */
	public void cancelAssociateDictionnary(XsdNode node) {
		associatedClosedDictionary.remove(node);
		associatedOpenDictionary.remove(node);
		associatedDictionaryColumn.remove(node);
	}

	/**
	 * removes the association of the node selected with a cell
	 */
	public void cancelAssociateField(XsdNode node) {
		if (!associatedFields.containsKey(node))
			return;

		associatedFields.remove(node);
		node.unuse();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	/**
	 * removes the association of the node selected with a cell
	 */
	public void cancelAssociateFlatFile(XsdNode node) {
		if (!associatedFlatFiles.contains(node))
			return;

		associatedFlatFiles.set(associatedFlatFiles.indexOf(node), null);
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	public void cancelDuplicableField(XsdNode node) {
		if (!associatedDuplicableFields.containsKey(node))
			return;
		associatedDuplicableFields.remove(node);
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	/**
	 * removes the association of the node selected with any default value
	 */
	public void cancelDefaultValue(XsdNode node) {
		if (!associatedValues.containsKey(node))
			return;

		associatedValues.remove(node);
		node.unuseOnlyThis();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	/**
	 * checks if the node is associated to a default value
	 * 
	 * @param node
	 *            a node
	 * @return true if such an association exists
	 */
	public boolean hasDefaultValue(XsdNode node) {
		return associatedValues.containsKey(node);
	}

	/**
	 * checks if the node is associated to a cell
	 * 
	 * @param node
	 *            a node
	 * @return true if such an association exists
	 */
	public boolean isAffected(XsdNode node) {
		return associatedFields.containsKey(node);
	}

	/**
	 * check if target nod eis mapped to anything.
	 * 
	 * @param node
	 * @return
	 */
	public boolean isMapped(XsdNode node) {
		if (isAffected(node))
			return true;
		if (hasDefaultValue(node))
			return true;
		if (associatedAutogeneration.contains(node))
			return true;
		return false;
	}

	/**
	 * get the value for a node
	 * 
	 * @param node
	 *            a node
	 * @return the value in the field associated to this node if the association
	 *         exists (eventually replaced by a replacement value in a
	 *         dictionnary), if not a automaticaly generated value if the node
	 *         has been setted to request one, if not the default value if one
	 *         has been associated to the node. Else return null
	 */
	public String getValue(XsdNode node) {
		/* node affected to a field */
		if (isAffected(node)) {
			String path = (String) associatedFields.get(node);
			String modelPath = path;
			String g = pathFilter;
			/* remember not to use the filter for unduplicable nodes */
			if (pathFilter != null && !unduplicableNodes.contains(node)) {
				String[] filters = pathFilter.split("\\.");
				String[] paths = path.split("\\.");
				String filteredPath = "";
				for (int i = 0; i < filters.length; i++) {
					try {
					paths[i] = String.valueOf(Integer.parseInt(filters[i])
							+ Integer.parseInt(paths[i]));
					} catch (IndexOutOfBoundsException e) {					
						return "";
					}
				}
				for (int i = 0; i < paths.length - 1; i++) {
					filteredPath += paths[i] + ".";
				}
				filteredPath += paths[paths.length - 1];
				path = filteredPath;
			}
//			getMessageManager().sendMessage("filtered path: "+ path + ", " + node);
			String value = flatFiles.getValue(path, modelPath);
			if (value == null) {
				return null;
			}

			if (validationRegexps.containsKey(node)) {
				if (!value.matches((String) validationRegexps.get(node)))
					return null;
			}

			if (associatedClosedDictionary.containsKey(node)) {
				String replacementValue = dictionaries.getReplacementValue(
						((Integer) associatedClosedDictionary.get(node))
								.intValue(), value,
						((Integer) associatedDictionaryColumn.get(node))
								.intValue());

				if (replacementValue == null) { // || replacementValue.length()
					// == 0) {
					getMessageManager().sendMessage(printPath(node.getPath()) 
							+ ": no value found for " + value 
							+ " in dictionary! (line : " + lineNumber + ")", MessageManagerInt.warningMessage);
					return null;
				}
				return getXmlValue(replacementValue.trim());
			} else if (associatedOpenDictionary.containsKey(node)) {
				String replacementValue = dictionaries.getReplacementValue(
						((Integer) associatedOpenDictionary.get(node))
								.intValue(), value,
						((Integer) associatedDictionaryColumn.get(node))
								.intValue());

				if (replacementValue != null) {
					//return getXmlValue(value.trim());
					value = replacementValue;
				}
			}
			if (value.trim().length() == 0)
				return null;

			return getXmlValue(value.trim());
		}

		/* node with value autogenerated */
		if (associatedAutogeneration.contains(node)) {
			String value = id + lastId;
			lastId++;
			return getXmlValue(value);
		}

		/* node with default value */
		if (hasDefaultValue(node)) {
			return getXmlValue((String) associatedValues.get(node));
		}
		return "";
	}

	/**
	 * return a new String where specials characters are public
	 */
	public String getXmlValue(String value) {
		return value.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;").replaceAll("\'", "&apos;").replaceAll(
						"\"", "&quot;");
	}

	/**
	 * get informations about the node in an understandable String
	 */
	public String getInfos(XsdNode node) {
		if (node == null)
			return "No node selected!";

		String infos = super.getInfos(node);
		// column associated
		infos += getAssociationInfo(node);
		return infos;
	}

	public String getAssociationInfo(XsdNode node) {
		String infos = "";

		String duplicableField = (String) associatedDuplicableFields.get(node);

		if (duplicableField != null) {
			infos += "this node will be automaticaly duplicated: "
					+ "\nfile: "
					+ Utils
							.relativizeURL(
									((FlatFile) flatFiles
											.getFlatFile(Integer
													.parseInt(duplicableField
															.substring(
																	0,
																	duplicableField
																			.indexOf("."))))).fileURL)
							.getPath()
					+ "\nfield: "
					+ duplicableField
							.substring(duplicableField.indexOf(".") + 1)
					+ ".[1..*]\n";
		}

		String field = (String) associatedFields.get(node);
		if (field != null) {
			infos += "associated field: "
					+ "\nfile: "
					+ Utils.relativizeURL(
							((FlatFile) flatFiles.getFlatFile(Integer
									.parseInt(field.substring(0, field
											.indexOf("."))))).fileURL)
							.getPath() + "\nfield: "
					+ field.substring(field.indexOf(".") + 1) + "\n";
		}
		// default value
		if (hasDefaultValue(node)) {
			infos += "associated value: " + associatedValues.get(node) + "\n";
		}
		// dictionnary
		if (associatedOpenDictionary.containsKey(node)) {
			infos += "find replacement value in dictionnary: "
					+ dictionaries.getName(((Integer) associatedOpenDictionary
							.get(node)).intValue())
					+ " or keep orginal value.\n";
		}
		// dictionnary
		if (associatedClosedDictionary.containsKey(node)) {
			infos += "find replacement value in dictionnary: "
					+ dictionaries
							.getName(((Integer) associatedClosedDictionary
									.get(node)).intValue()) + "\n";
		}
		if (associatedAutogeneration.contains(node)) {
			infos += "A value will be automaticaly generated for this node.";
		}

		if (associatedFlatFiles.contains(node)) {
			infos += Utils.relativizeURL(
					((FlatFile) flatFiles.getFlatFile(associatedFlatFiles
							.indexOf(node))).fileURL).getPath()
					+ "\n";
		}
		if (unduplicableNodes.contains(node)) {
			infos += "unduplicable";
		}
		if (this.validationRegexps.containsKey(node)) {
			infos += "validated by regular expression:"
					+ validationRegexps.get(node);
		}
		return infos;
	}

	public boolean checkAttribute(XsdNode node) {
		if (node.isRequired 
				/** TODO: check if it works, 2006-05-25 */ 
				&& !isAffected(node) && !hasDefaultValue(node)
				&& !associatedAutogeneration.contains(node))
//				&& (getValue(node) == null || getValue(node).length()==0))
				{
			node.isCheckedOk = false;
//			getMessageManager().sendMessage(printPath(node.getPath()) + " missing value", MessageManagerInt.errorMessage);
			return false;
		} else {
			node.isCheckedOk = true;
			return true;
		}
	}

	public boolean checkElement(XsdNode node) {
		if (!node.transparent && !node.isUsed && !node.isRequired) {
			node.isCheckedOk = true;
			return true;
		}

		if (node.transparent) {
			boolean checkedOk = true;
//			Enumeration children = node.children();

//			while (children.hasMoreElements()) {
			for (Iterator it = getChildren(node); it.hasNext(); ) {
//				XsdNode child = (XsdNode) children.nextElement();
				XsdNode child = (XsdNode)it.next();//children.nextElement();
				if (!check(child)) {
//					getMessageManager().sendMessage(printPath(child.getPath()) +" missing value", MessageManagerInt.errorMessage);
					checkedOk = false;
				}
			}
			node.isCheckedOk = checkedOk;
			return checkedOk;
		}

		XMLType type = ((ElementDecl) node.getUserObject()).getType();

		if (type == null) {
//			getMessageManager().sendMessage(printPath(node.getPath()) +" no type declaration"
//					+ node.toString(), MessageManagerInt.warningMessage);
			return true;
		}

		/* simpleType */
		if (type.isSimpleType()) {
			if (node.isRequired  
					&& !isAffected(node) && !hasDefaultValue(node)
					&& !associatedAutogeneration.contains(node)
//					&& (getValue(node) == null || getValue(node).length()==0)
					) {
				node.isCheckedOk = false;			
//				getMessageManager().sendMessage(printPath(node.getPath()) +" missing value"
//						, MessageManagerInt.errorMessage);
				return false;
			} else {
				node.isCheckedOk = true;
				return true;
			}
		} else { /* complexType, ie: attributes + group */
			return checkGroup(node);
		}
	}

	/*
	 * a group can only be a choice (else it would be expanded if we find it, it
	 * means user has to make a choice
	 */
	public boolean checkGroup(XsdNode node) {
		
		boolean hasUsedChild = false;
		
		if (node.transparent) {
			boolean checkedOk = true;
			Enumeration children = node.children();

			while (children.hasMoreElements()) {
				XsdNode child = (XsdNode) children.nextElement();
				if (!check(child)) {
//					getMessageManager().sendMessage(printPath(node.getPath()) +" missing value", MessageManagerInt.errorMessage);
					checkedOk = false;
				}
			}
			node.isCheckedOk = checkedOk;
			return checkedOk;
		}

		boolean errors = false;
		/* check if number of subelts is correct */
		HashMap maxOccurs = new HashMap();
		HashMap minOccurs = new HashMap();

		Enumeration children = node.children();

		while (children.hasMoreElements()) {
			XsdNode child = (XsdNode) children.nextElement();
			
			int nbDuplications = 1;
			String previousFilter = pathFilter;
			String filter = "";

			if (associatedDuplicableFields.containsKey(child)) {
				nbDuplications = flatFiles
						.nbElements((String) associatedDuplicableFields
								.get(child));

				for (int i = 0; i < ((String) associatedDuplicableFields
						.get(child)).split("\\.").length; i++) {
					filter += "0.";
				}
				filter += "0";
				pathFilter = filter;
			}

			for (int i = 0; i < nbDuplications; i++) {
				if (i > 0) {
					int lastFilterIdx = Integer.parseInt(filter
							.substring(filter.lastIndexOf(".") + 1))
							+ i;
					pathFilter = filter.substring(0,
							filter.lastIndexOf(".") + 1)
							+ lastFilterIdx;
				}
				boolean isChildOk = check(child);

				switch (((Annotated) child.getUserObject()).getStructureType()) {
				case Structure.ATTRIBUTE:
					if (!isChildOk) 
						errors = true;
					break;
				case Structure.GROUP:
					if (((Group) child.getUserObject()).getOrder().getType() == Order.CHOICE && !child.isExtended) {
//						getMessageManager().sendMessage(printPath(child.getPath()) +" please expand this node ", MessageManagerInt.errorMessage);
						errors = true;
					} else if (!isChildOk) {
						errors = true;
					}
					break;
				case Structure.ELEMENT:
					/* initialisation if first occurence of the element */
					if (!maxOccurs.containsKey(child.toString())) {
						int max = child.max;
						if (max != -1) {
							maxOccurs.put(child.toString(), new Integer(max));
						} else {
							maxOccurs.put(child.toString(), "UNBOUNDED");
						}
						minOccurs.put(child.toString(), new Integer(child.min));
					}

					if (child.isCheckedOk) {
						try {
							maxOccurs.put(child.toString(), new Integer(
									((Integer) maxOccurs.get(child.toString()))
											.intValue() - 1));
						} catch (ClassCastException e) {
							/*
							 * ok, max is unbounded and exception is throws when
							 * trying to cast String to Integer
							 */
						}
						minOccurs.put(child.toString(), new Integer(
								((Integer) minOccurs.get(child.toString()))
										.intValue() - 1));
					}
				}

				pathFilter = previousFilter;
				
				if (child.isUsed)
					hasUsedChild=true;
			}

		}
		Iterator names = minOccurs.keySet().iterator();

		Iterator mins = minOccurs.values().iterator();
		Iterator maxs = maxOccurs.values().iterator();
		while (names.hasNext()) {
//			String name = (String)
			names.next();
			// if a min is > 0, it means that an element is missing
			/////////////////////// dat one
			if (((Integer) mins.next()).intValue() > 0) {
//				getMessageManager().sendMessage(printPath(node.getPath()) +" a " + name
//						+ " is missing", MessageManagerInt.errorMessage);
				errors = true;
			}

			/* if a max is < 0, it means there are too much elements */
			try {
				if (((Integer) maxs.next()).intValue() < 0) {
//					getMessageManager().sendMessage(printPath(node.getPath()) +" a " + name
//									+ " should be removed", MessageManagerInt.errorMessage);
					errors = true;
				}

			} catch (ClassCastException e) {
				/*
				 * ok, max is unbounded and exception is throws when trying to
				 * cast String to Integer
				 */
			}
		}
		node.isCheckedOk = !errors;
		
		if (node.isRequired && !hasUsedChild)
			node.isCheckedOk = false;


		return node.isCheckedOk;
	}

	/**
	 * return XML code to close the element
	 * 
	 * @param node
	 *            a node
	 * @param isEmptyElement
	 *            if the node does not have neither attribute nor value or sub
	 *            elements
	 * @return XML code
	 */
	public String closeElement(XsdNode node, boolean isEmptyElement) {
		if (isEmptyElement)
			return "";
		return "</" + node.toString() + ">";
	}

	/**
	 * write the XML code to close the element
	 * 
	 * @param node
	 *            a node
	 * @param isEmptyElement
	 *            if the node does not have neither attribute nor value or sub
	 *            elements
	 * @param out
	 *            the writer used to write the code
	 */
	public void closeElement(XsdNode node, boolean isEmptyElement, Writer out)
			throws IOException {
		if (isEmptyElement)
			return;
		out.write("</" + node.toString() + ">");
	}

	/**
	 * check if these are enough associations according to the shema
	 * 
	 * condition for being "checkedOK": attributes: if is associated to a value
	 * or not required simpleType elements: if is associated to a value element,
	 * complex type: if all sub Elements are checkedOk group: if the count of
	 * subElements "checkedOk" is good
	 * 
	 * condition for errors: elements or group is not "checkedOk"
	 *  
	 */
	public boolean check(XsdNode node) {
//		return true;
		//ArrayList errorMessages = new ArrayList();
		switch (((Annotated) node.getUserObject()).getStructureType()) {

		case Structure.ATTRIBUTE:
			return checkAttribute(node);
		case Structure.ELEMENT:
			return checkElement(node);
		case Structure.GROUP:
			return checkGroup(node);
		default:
//			getMessageManager().sendMessage(printPath(node.getPath()) +" type not found: "
//					+ ((Annotated) node.getUserObject()).getStructureType(), MessageManagerInt.errorMessage);
			return false;
		}
	}


	public void validateXml(File xmlFile, Writer log) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);

		factory.setAttribute(SCHEMA_LANGUAGE, XML_SCHEMA);
		factory.setAttribute(SCHEMA_SOURCE, schemaFile);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(xmlErrorHandler);
			Document document = builder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			/** TODO: manage excepton */
		} catch (SAXException e2) {
			/** TODO: manage excepton */
		} catch (OutOfMemoryError ooo) {
			try {
				log
						.write("ERROR: not enougth memory to perform XML validation");
			} catch (IOException e3) {
				/** TODO: manage excepton */
			}
			getMessageManager().sendMessage("ERROR: not enougth memory to perform XML validation", MessageManagerInt.errorMessage);
		} catch (IOException e3) {
			/** TODO: manage excepton */
		}

	}

//	public void logoutPrintErrors(XsdNode node, Writer logoutPrintWriter)
//			throws IOException {
//		if (!node.isRequired)
//			return;
//
//		int errorType;
//		if (node.isRequired)
//			errorType =MessageManagerInt.errorMessage;
//		else
//			errorType = MessageManagerInt.warningMessage;
//
//		switch (((Annotated) node.getUserObject()).getStructureType()) {
//		case Structure.ATTRIBUTE:
//			if (!node.isCheckedOk) {
//				getMessageManager().sendMessage("in element "
//						+ printPath(node.getPath()), errorType);
//				getMessageManager().sendMessage("attribute "
//						+ node.toString() + " ignored (" + getNodeProblem(node)
//						+ ")", errorType);
//			}
//			break;
//		case Structure.ELEMENT:
//			XMLType type = ((ElementDecl) node.getUserObject()).getType();
//			if (type == null) {
//				getMessageManager().sendMessage("no type declaration for element "
//								+ node.toString(), MessageManagerInt.warningMessage);
//				return;
//			}
//			/* simpleType */
//			if (type.isSimpleType()) {
//				if (!node.isCheckedOk) {
//					getMessageManager().sendMessage("in element "
//							+ printPath(node.getPath()), errorType);
//					getMessageManager().sendMessage("element "
//							+ node.toString() + " ignored ("
//							+ getNodeProblem(node) + ")",errorType);
//				}
//			} else { /* complex type: go deeper */
//				Enumeration children = node.children();
//
//				while (children.hasMoreElements()) {
//					logoutPrintErrors((XsdNode) children.nextElement(),
//							logoutPrintWriter);
//				}
//			}
//			break;
//		case Structure.GROUP:
//			logoutPrintWriter
//					.write("	[WARNING] maybe something is missing in  "
//							+ node.toString()
//							+ " (you have to click on the node and make a choice).\n");
//			break;
//		default:
//			getMessageManager().sendMessage("type not found "
//					+ ((Annotated) node.getUserObject()).getStructureType(), errorType);
//			node.isCheckedOk = false;
//		}
//	}


	/**
	 * return XML code to open the element
	 * 
	 * @param node
	 *            a node
	 * @param isEmptyElement
	 *            if the node does not have neither attribute nor value or sub
	 *            elements
	 * @param attributes
	 *            a string containing the XML code for the attributes of this
	 *            element
	 * @return the XML code for the element
	 */
	public String openElement(XsdNode node, String attributes,
			boolean isEmptyElement) {
		if (isEmptyElement)
			return "\n<" + node.toString() + attributes + "/>";
		return "\n<" + node.toString() + " " + attributes + ">";
	}

	public String openElement(XsdNode node, ArrayList attributes,
			boolean isEmptyElement) {
		
		String attributesString = "";
		Iterator it = attributes.iterator();
		while (it.hasNext()) {
			String attributeName = (String)it.next();
			String attributeValue = (String)it.next();
			if (!attributeValue.equals(""))
				attributesString += " " +attributeName+"=\""+attributeValue+"\"";
//			attributesString += " " + (String)it.next();
//			attributesString += "=\""+(String)it.next()+"\"";
		}
		attributesString = attributesString.trim();
		if (attributesString.length() > 0) 
			attributesString = " " + attributesString;
		else if (isEmptyElement)
			return null;
		if (isEmptyElement)
			return "\n" + indentation + "<" + node.toString() + attributesString + "/>";
		return "\n" + indentation + "<" + node.toString() + attributesString + ">";
	}


	/**
	 * get a preview of the XML file with only data taken from one line in flat
	 * files
	 * 
	 * @return xml code for this preview
	 */
	public String preview() {
		return previewNode((XsdNode) treeModel.getRoot());
	}

	public String preview(XsdNode node) {
		return previewNode(node);
	}

	public String previewAttribute(XsdNode node) {
		String value = getValue(node);

		if (value != null && !value.equals(""))
			return " " + ((AttributeDecl) node.getUserObject()).getName()
					+ "=\"" + value + "\"";
		else
			return null;

	}

	public String previewElement(XsdNode node) {

		if (!node.isUsed)
			return null;

		String attributes = "";
		String elements = "";
		String value;
		/*
		 * get every childs of the node get the structureType of the userElement
		 * and use the apropriate marshaller
		 */
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			XsdNode child = (XsdNode) children.nextElement();
			switch (((Annotated) child.getUserObject()).getStructureType()) {
			case Structure.ATTRIBUTE:
				String attribute = previewAttribute(child);
				if (attribute != null)
					attributes += attribute;
				break;
			case Structure.ELEMENT:
				String element = previewElement(child);
				if (element != null)
					elements += element;
				break;
			case Structure.GROUP:
				String group;
				group = previewGroup(child);
				if (group != null)
					elements += group;
				break;
			}
		} /* get the value affected to this element */
		value = getValue(node);

		boolean isEmptyElement = ((value == null || value.equals("")) && elements.length() == 0);
		
		if (elements.length() != 0)
			elements = elements + "\n";
		if (attributes.length() == 0 && isEmptyElement)
			return null;

		if (value == null)
			value = "";

		return openElement(node, attributes, isEmptyElement) + value + elements
				+ closeElement(node, isEmptyElement);
	}

	public String previewGroup(XsdNode node) {

		String group = "";
		Enumeration elements = node.children();
		while (elements.hasMoreElements()) {
			String element = previewNode((XsdNode) elements.nextElement());
			if (element != null)
				group += element;
		}
		return group;
	}

	public String previewNode(XsdNode node) {
		switch (((Annotated) node.getUserObject()).getStructureType()) {
		case Structure.ATTRIBUTE:
			return previewAttribute(node);
		case Structure.GROUP:
			return previewGroup(node);
		case Structure.ELEMENT:
			return previewElement(node);
		default:
			return "<error: unmanaged elementt/>";
		}
	}

	public void cancelAllAssociations(XsdNode node) {
		unduplicableNodes.remove(node);
		validationRegexps.remove(node);
		associatedAutogeneration.remove(node);
		associatedFields.remove(node);
		associatedValues.remove(node);
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	public void associateAutoGenerateValue(XsdNode node) {
		cancelAllAssociations(node);
		associatedAutogeneration.add(node);

		name = node.toString();

		node.useOnlyThis();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	public void cancelAutogenerate(XsdNode node) {
		if (!associatedAutogeneration.contains(node))
			return;

		associatedAutogeneration.remove(node);
		node.unuseOnlyThis();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);
	}

	public String getNodeProblem(XsdNode node) {
		if (isAffected(node)) {
			String value = getValue(node);

			if (value == null || value.length() == 0) {
				return "the field "
						+ ((String) associatedFields.get(node))
								.substring(((String) associatedFields.get(node))
										.indexOf(".") + 1) + " is empty";
			}

			if (associatedClosedDictionary.containsKey(node)) {
				String replacementValue = dictionaries.getReplacementValue(
						((Integer) associatedClosedDictionary.get(node))
								.intValue(), value,
						((Integer) associatedDictionaryColumn.get(node))
								.intValue());

				if (replacementValue == null || replacementValue.length() == 0) {
					return "no replacement value found for value "
							+ value
							+ " (in dictionnary: "
							+ dictionaries
									.getName(((Integer) associatedClosedDictionary
											.get(node)).intValue())
							+ ", field "
							+ ((String) associatedFields.get(node))
									.substring(((String) associatedFields
											.get(node)).indexOf(".") + 1);
				}
			}
			/*
			 * nuthin else to do 4 opening dictionaries: always at least the
			 * original value
			 */
		}
		return "no association to this node";
	}

	public TreeMapping getMapping() {
		Iterator it;

		TreeMapping mapping = new TreeMapping();

		mapping.setId(this.id);
		mapping.setAutoDuplicate(this.autoDuplicate);
		mapping.setManageChoices(this.manageChoices);
		mapping
				.setSchemaURL(Utils.relativizeURL(this.getSchemaURL())
						.getPath());
		ArrayList associatedAutogeneration = new ArrayList();
		for (int i = 0; i < this.associatedAutogeneration.size(); i++) {
			associatedAutogeneration
					.add(getPathForNode((XsdNode) this.associatedAutogeneration
							.get(i)));
		}
		mapping.setAssociatedAutogeneration(associatedAutogeneration);

		ArrayList unduplicableNodes = new ArrayList();
		for (int i = 0; i < this.unduplicableNodes.size(); i++) {
			unduplicableNodes
					.add(getPathForNode((XsdNode) this.unduplicableNodes.get(i)));
		}
		mapping.setUnduplicableNodes(unduplicableNodes);

		mapping.setExpendChoices(this.expendChoices);

		HashMap associatedFields = new HashMap();
		it = this.associatedFields.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			associatedFields.put(getPathForNode(node), this.associatedFields
					.get(node));
		}
		mapping.setAssociatedFields(associatedFields);

		HashMap associatedDuplicableFields = new HashMap();
		it = this.associatedDuplicableFields.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			associatedDuplicableFields.put(getPathForNode(node),
					this.associatedDuplicableFields.get(node));
		}
		mapping.setAssociatedDuplicableFields(associatedDuplicableFields);

		HashMap associatedValues = new HashMap();
		it = this.associatedValues.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			String field = (String) this.associatedValues.get(node);
			associatedValues.put(getPathForNode(node), this.associatedValues
					.get(node));
		}
		mapping.setAssociatedValues(associatedValues);

		HashMap validationRegexps = new HashMap();
		it = this.validationRegexps.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			String field = (String) this.validationRegexps.get(node);
			validationRegexps.put(getPathForNode(node), this.validationRegexps
					.get(node));
		}
		mapping.setValidationRegexps(validationRegexps);

		HashMap associatedOpenDictionary = new HashMap();
		it = this.associatedOpenDictionary.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			associatedOpenDictionary.put(getPathForNode(node),
					this.associatedOpenDictionary.get(node));
		}
		mapping.setAssociatedOpenDictionary(associatedOpenDictionary);

		HashMap associatedClosedDictionary = new HashMap();
		it = this.associatedClosedDictionary.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			associatedClosedDictionary.put(getPathForNode(node),
					this.associatedClosedDictionary.get(node));
		}
		mapping.setAssociatedClosedDictionary(associatedClosedDictionary);

		HashMap associatedDictionaryColumn = new HashMap();
		it = this.associatedDictionaryColumn.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			associatedDictionaryColumn.put(getPathForNode(node),
					this.associatedDictionaryColumn.get(node));
		}
		mapping.setAssociatedDictionaryColumn(associatedDictionaryColumn);

		ArrayList associatedFlatFiles = new ArrayList();
		for (int i = 0; i < this.associatedFlatFiles.size(); i++) {
			associatedFlatFiles
					.add(getPathForNode((XsdNode) this.associatedFlatFiles
							.get(i)));
		}
		mapping.setAssociatedFlatFiles(associatedFlatFiles);

		return mapping;
	}

//	/**
//	 * create a copy of the node and add it to the parent of this node if the
//	 * node is not duplicable or if the maximum amount of this type of node
//	 * according to the schema has been reached, do nothing
//	 * 
//	 * @param node
//	 *            the node to duplicate
//	 */
//	public void duplicateNode(XsdNode node) {
//		if (!node.isDuplicable())
//			return;
//		if (node.max == getChildrenCount((XsdNode) node.getParent(), node
//				.toString()))
//			return;
//
//		XsdNode child = node.createBrother();
//
//		XsdNode parentNode = (XsdNode) node.getParent();
//
//		/* add to the end for not corrupting the mapping */
//		treeModel.insertNodeInto(child, parentNode, parentNode.getChildCount());
//
//		/* be sure that this node is not already used */
//		child.init();
//
//		if (((Annotated) child.getUserObject()).getStructureType() != Structure.GROUP)
//			extendPath(child);
//		else if (((Group) child.getUserObject()).getOrder().getType() != Order.CHOICE)
//			extendPath(child);
//		super.duplicateNode(node);
//		
//		check((XsdNode) treeModel.getRoot());
//		treeModel.reload((XsdNode) treeModel.getRoot());
//	}

	public void loadMapping(TreeMapping mapping) throws MalformedURLException {
		Iterator it;
		this.setId(mapping.id);
		this.setSchemaURL(new File(mapping.getSchemaURL()).toURL());
		this.setAutoDuplicate(mapping.autoDuplicate);
		this.setManageChoices(mapping.manageChoices);

//		this.setExpendChoices(mapping.expendChoices);

		int i = 0;
		while (i < mapping.expendChoices.size()) {
			String path = (String) mapping.expendChoices.get(i);
			i++;
			String choice = (String) mapping.expendChoices.get(i);
			i++;
			int index = path.indexOf(".");
			String subpath = path;

			if (choice != null) {
				redoChoice(path, choice);
			} else { /* duplication */
				duplicateNode(getNodeByPath(path));
			}
			
		}

		for (i = 0; i < mapping.associatedAutogeneration.size(); i++) {
			XsdNode node = getNodeByPath((String) mapping.associatedAutogeneration
					.get(i));
			node.useOnlyThis();
			associateAutoGenerateValue(node);
		}

		it = mapping.associatedValues.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			String field = (String) mapping.associatedValues.get(path);
			XsdNode node = getNodeByPath(path);
			node.useOnlyThis();
			this.associatedValues.put(node, mapping.associatedValues.get(path));
		}

		it = mapping.validationRegexps.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			String field = (String) mapping.validationRegexps.get(path);
			XsdNode node = getNodeByPath(path);
			this.validationRegexps.put(node, mapping.validationRegexps
					.get(path));
		}

		it = mapping.associatedOpenDictionary.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			this.associatedOpenDictionary.put(getNodeByPath(path),
					mapping.associatedOpenDictionary.get(path));
		}

		it = mapping.associatedClosedDictionary.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			this.associatedClosedDictionary.put(getNodeByPath(path),
					mapping.associatedClosedDictionary.get(path));
		}

		it = mapping.associatedDictionaryColumn.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			associatedDictionaryColumn.put(getNodeByPath(path),
					mapping.associatedDictionaryColumn.get(path));
		}

		for (i = 0; i < mapping.associatedFlatFiles.size(); i++) {
			rootNode.use();
			this.associatedFlatFiles
					.add(getNodeByPath((String) mapping.associatedFlatFiles
							.get(i)));
		}

		it = mapping.associatedDuplicableFields.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			XsdNode node = getNodeByPath(path);
			node.useOnlyThis();
			associatedDuplicableFields.put(getNodeByPath(path),
					mapping.associatedDuplicableFields.get(path));
		}

		it = mapping.associatedFields.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			XsdNode node = getNodeByPath(path);
			node.useOnlyThis();
			associatedFields.put(getNodeByPath(path), mapping.associatedFields
					.get(path));
		}

		for (i = 0; i < mapping.unduplicableNodes.size(); i++) {
			XsdNode node = getNodeByPath((String) mapping.unduplicableNodes
					.get(i));
			unduplicableNodes.add(node);
		}
	}

	/**
	 * @return Returns the associatedAutogeneration.
	 * 
	 * @uml.property name="associatedAutogeneration"
	 */
	public ArrayList getAssociatedAutogeneration() {
		return associatedAutogeneration;
	}

	/**
	 * @param associatedAutogeneration
	 *            The associatedAutogeneration to set.
	 * 
	 * @uml.property name="associatedAutogeneration"
	 */
	public void setAssociatedAutogeneration(ArrayList associatedAutogeneration) {
		this.associatedAutogeneration = associatedAutogeneration;
	}

	/**
	 * @return Returns the associatedDictionaryColumn.
	 * 
	 * @uml.property name="associatedDictionaryColumn"
	 */
	public HashMap getAssociatedDictionaryColumn() {
		return associatedDictionaryColumn;
	}

	/**
	 * @param associatedDictionaryColumn
	 *            The associatedDictionaryColumn to set.
	 * 
	 * @uml.property name="associatedDictionaryColumn"
	 */
	public void setAssociatedDictionaryColumn(HashMap associatedDictionaryColumn) {
		this.associatedDictionaryColumn = associatedDictionaryColumn;
	}

	/**
	 * @return Returns the associatedFields.
	 * 
	 * @uml.property name="associatedFields"
	 */
	public HashMap getAssociatedFields() {
		return associatedFields;
	}

	/**
	 * @param associatedFields
	 *            The associatedFields to set.
	 * 
	 * @uml.property name="associatedFields"
	 */
	public void setAssociatedFields(HashMap associatedFields) {
		this.associatedFields = associatedFields;
	}

	/**
	 * @return Returns the associatedFlatFiles.
	 * 
	 * @uml.property name="associatedFlatFiles"
	 */
	public ArrayList getAssociatedFlatFiles() {
		return associatedFlatFiles;
	}

	/**
	 * @param associatedFlatFiles
	 *            The associatedFlatFiles to set.
	 * 
	 * @uml.property name="associatedFlatFiles"
	 */
	public void setAssociatedFlatFiles(ArrayList associatedFlatFiles) {
		this.associatedFlatFiles = associatedFlatFiles;
	}

	/**
	 * @return Returns the associatedValues.
	 * 
	 * @uml.property name="associatedValues"
	 */
	public HashMap getAssociatedValues() {
		return associatedValues;
	}

	/**
	 * @param associatedValues
	 *            The associatedValues to set.
	 * 
	 * @uml.property name="associatedValues"
	 */
	public void setAssociatedValues(HashMap associatedValues) {
		this.associatedValues = associatedValues;
	}

	/**
	 * @return Returns the dictionaries.
	 * 
	 * @uml.property name="dictionaries"
	 */
	public DictionaryContainer getDictionaries() {
		return dictionaries;
	}

	/**
	 * @param dictionaries
	 *            The dictionaries to set.
	 * 
	 * @uml.property name="dictionaries"
	 */
	public void setDictionaries(DictionaryContainer dictionaries) {
		this.dictionaries = dictionaries;
	}

	/**
	 * @return Returns the flatFiles.
	 * 
	 * @uml.property name="flatFiles"
	 */
	public FlatFileContainer getFlatFiles() {
		return flatFiles;
	}

	/**
	 * @param flatFiles
	 *            The flatFiles to set.
	 * 
	 * @uml.property name="flatFiles"
	 */
	public void setFlatFiles(FlatFileContainer flatFiles) {
		this.flatFiles = flatFiles;
	}

	/**
	 * @return Returns the id.
	 * 
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 * 
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the lastId.
	 * 
	 * @uml.property name="lastId"
	 */
	public int getLastId() {
		return lastId;
	}

	/**
	 * @param lastId
	 *            The lastId to set.
	 * 
	 * @uml.property name="lastId"
	 */
	public void setLastId(int lastId) {
		this.lastId = lastId;
	}

	/**
	 * @return Returns the name.
	 * 
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 * 
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param expendChoices
	 *            The expendChoices to set.
	 */
	public void setExpendChoices(ArrayList expendChoices) {
		super.expendChoices = expendChoices;
	}

	/**
	 * @return Returns the expendChoices.
	 */
	public ArrayList getExpandChoices() {
		return super.expendChoices;
	}

	/**
	 * @return Returns the associatedDuplicableFields.
	 * 
	 * @uml.property name="associatedDuplicableFields"
	 */
	public HashMap getAssociatedDuplicableFields() {
		return associatedDuplicableFields;
	}

	/**
	 * @param associatedDuplicableFields
	 *            The associatedDuplicableFields to set.
	 * 
	 * @uml.property name="associatedDuplicableFields"
	 */
	public void setAssociatedDuplicableFields(HashMap associatedDuplicableFields) {
		this.associatedDuplicableFields = associatedDuplicableFields;
	}

	////////////////////////////////////////////////////////////////////////////////////////////

	public void print2(File outFile) throws IOException {
		Writer out = new BufferedWriter(new FileWriter(outFile));

		observable.setMessage("output file: " + outFile.getName());
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out
				.write("<!-- created using XmlMakerFlattener v1.0 (http://cvs.sourceforge.net/viewcvs.py/psidev/psi/mi/tools/) -->");
		getMessageManager().sendMessage("start marshalling to file :"
				+ outFile.getName() + " at " + new Date() , MessageManagerInt.simpleMessage);
		try {
			out.write(xmlMake());
		} catch (FileMakersException fme) {
			getMessageManager().sendMessage("Exception in main loop: " + fme, MessageManagerInt.errorMessage);
			/** TODO : manage exception */
		} catch (java.lang.NullPointerException npe) {
			getMessageManager().sendMessage("marshalling failed", MessageManagerInt.errorMessage);
		}

//		String warning = this.errorManager.getAllErrors(this, (XsdNode) treeModel.getRoot(),
//				ErrorManager.warning);
//		String errors = this.errorManager.getAllErrors(this, (XsdNode) treeModel.getRoot(),
//				ErrorManager.error);
//		if (warning.length() > 0 || errors.length() > 0) {
//			logoutPrintWriter.write("line :" //+ lineNumber + "\n"
//					+ warning + "\n" + errors + "\n");
//		}
		
		getMessageManager().sendMessage("marshalling done, finished at " + new Date()
				, MessageManagerInt.simpleMessage);

		out.flush();
		out.close();

		observable.setMessage("marshalling done");
		observable.notifyObservers(observable.getMessage());
		observable.deleteObservers();
	}

	/**
	 * write the whole XML file
	 * 
	 * @param out
	 *            the writer used to write the file
	 * @throws IOException
	 */
	public String xmlMake() throws IOException, FileMakersException {
		return xmlMake((XsdNode) treeModel.getRoot());
	}

	/**
	 * write the XML code for a node
	 *  
	 */
	public String xmlMake(XsdNode node) throws IOException, FileMakersException {
		lastId = 0;
		
		return xmlMakeElement(node);
		//return null;
		/* to reinitialize the display */
		//check(node);
	}


	public String xmlMakeElement(XsdNode node) throws IOException,
			FileMakersException {
		
		if (!node.isUsed) {
			return "";
		}
		
		String xmlCode = "";

		Iterator children = getChildren(node);

		ArrayList attributeList = new ArrayList();
		ArrayList elementList = new ArrayList();
		ArrayList groupList = new ArrayList();

		/*
		 * get every childs of the node get the structureType of the userElement
		 * and use the apropriate marshaller
		 */
		while (children.hasNext()) {
			XsdNode child = (XsdNode) children.next();
			switch (((Annotated) child.getUserObject()).getStructureType()) {
			case Structure.ATTRIBUTE:
				attributeList.add(child);
				break;
			case Structure.ELEMENT: 
				if (child.isUsed) {
					elementList.add(child);
				}
				break;
			case Structure.GROUP:
				getMessageManager().sendMessage("should not be any group...." + child, MessageManagerInt.warningMessage);
				if (child.isUsed)
					groupList.add(child);
				break;
			}
		}

		indentation += "\t";

		HashMap maxOccurs = new HashMap();
		HashMap minOccurs = new HashMap();

		for (int i = 0; i < elementList.size(); i++) {
			XsdNode child = (XsdNode) elementList.get(i);
			/* initialisation if first occurence of the element */
			/** TODO: check if it works for duplicated nodes... */
			if (!maxOccurs.containsKey(child.toString())) {
				if (child.max != -1) {
					maxOccurs.put(child.toString(), new Integer(child.max));
				} else {
					maxOccurs.put(child.toString(), "UNBOUNDED");
				}
				minOccurs.put(child.toString(), new Integer(child.min));
			}

			if (associatedDuplicableFields.get(child) != null) {
				/* marshall all subelemets */
				/* make filter */
				/* how many sub elements */
//				String p1 = getPathForNode(child);
//				HashMap h = associatedDuplicableFields;
								
				String tmpPath = (String) associatedDuplicableFields.get(child);
				if (pathFilter != null && !unduplicableNodes.contains(node)) {
						String[] filters = pathFilter.split("\\.");
						String[] paths = tmpPath.split("\\.");
						String filteredPath = "";
						for (int j = 0; j < filters.length; j++) {
							paths[j] = String.valueOf(Integer.parseInt(filters[j])
									+ Integer.parseInt(paths[j]));
						}
						for (int j = 0; j < paths.length - 1; j++) {
							filteredPath += paths[j] + ".";
						}
						filteredPath += paths[paths.length - 1];
						tmpPath = filteredPath;
				}
				int nbDuplications = flatFiles
				.nbElements(tmpPath);
				
				String previousFilter = pathFilter;
				/* do not forget to apply previous filter to the new one!!! */
				String filter = "";
				
				for (int j = 0; j < ((String) associatedDuplicableFields.get(child))
						.split("\\.").length; j++) {
					if (previousFilter != null && previousFilter.split("\\.").length > j) 
						filter += previousFilter.split("\\.")[j] + ".";
					else 
						filter += "0.";
				}

				filter += "0";
				pathFilter = filter;
				for (int j = 0; j < nbDuplications; j++) {
					int lastFilterIdx = Integer.parseInt(filter.substring(filter
							.lastIndexOf(".") + 1))
							+ j;
					pathFilter = filter.substring(0, filter.lastIndexOf(".") + 1)
							+ lastFilterIdx;
					String xmlChildCode = xmlMakeElement(child);
					/* update number of nodes found */
					if (xmlChildCode != null) {
						try {
							maxOccurs.put(child.toString(), new Integer(
									((Integer) maxOccurs.get(child
											.toString())).intValue() - 1));
						} catch (ClassCastException e) {
							/*
							 * ok, max is unbounded and exception is thrown when
							 * trying to cast String to Integer
							 */
						}
						minOccurs.put(child.toString(), new Integer(
								((Integer) minOccurs.get(child.toString()))
										.intValue() - 1));
					}
					xmlCode += xmlChildCode;
				}
				pathFilter = previousFilter;
			} else if (associatedFlatFiles.contains(child)) {
				/* marshall all line */

				pushFlatFile(flatFiles.getFlatFile(associatedFlatFiles
						.indexOf((XsdNode) elementList.get(i))));
				
				getMessageManager().sendMessage("[CREATE XML] from file: "+ getCurrentFlatFile().fileURL
						.getFile(), MessageManagerInt.simpleMessage);

				observable
						.setCurrentFlatFile(getCurrentFlatFile().fileURL
								.getFile());
				observable.setElement(node.toString());
				observable.indentation++;

				boolean endOfFile = false;
				getCurrentFlatFile().restartFile();

				/* if the first line contains title, pass througth it */
				if (getCurrentFlatFile().firstLineForTitles()) {
					getCurrentFlatFile().nextLine();
				}
				int previousLineNumber = lineNumber;
				lineNumber = 0;
				
				while (!endOfFile) {
					observable.setCurrentLine(lineNumber++);
					try { /* get each line */
						if (!getCurrentFlatFile().hasLine()) {
							throw new IOException(
									"!getCurrentFlatFile().hasLine()");
						}
						String xmlChildCode = xmlMakeElement(child);

						/* update number of nodes found */
						if (xmlChildCode != null) {
							try {
								maxOccurs.put(child.toString(), new Integer(
										((Integer) maxOccurs.get(child
												.toString())).intValue() - 1));
							} catch (ClassCastException e) {
								/*
								 * ok, max is unbounded and exception is thrown when
								 * trying to cast String to Integer
								 */
							}
							minOccurs.put(child.toString(), new Integer(
									((Integer) minOccurs.get(child.toString()))
											.intValue() - 1));
						}
						xmlCode += xmlChildCode;
						getCurrentFlatFile().nextLine();
					} catch (IOException e) { /* end of the file */
						endOfFile = true;
						getCurrentFlatFile().restartFile();
					}
				}
				lineNumber = previousLineNumber;
				popFlatFile();
			} else {
				/* marshall element */
				String xmlChildCode = xmlMakeElement(child);
	
				/* update number of nodes found */
				if (xmlChildCode != null && !xmlChildCode.equals("")) {
					try {
						maxOccurs.put(child.toString(), new Integer(
								((Integer) maxOccurs.get(child
										.toString())).intValue() - 1));
					} catch (ClassCastException e) {
						/*
						 * ok, max is unbounded and exception is throws when
						 * trying to cast String to Integer
						 */
					}
					minOccurs.put(child.toString(), new Integer(
							((Integer) minOccurs.get(child.toString()))
									.intValue() - 1));
					xmlCode += xmlChildCode;
				}
			}
		}

		
		indentation = indentation.substring(1);
		
		/* check number of each element */
		boolean errors = false;
		Iterator names = minOccurs.keySet().iterator();

		Iterator mins = minOccurs.values().iterator();
		Iterator maxs = maxOccurs.values().iterator();
		while (names.hasNext()) {
			String name = (String) names.next();
			if (((Integer) mins.next()).intValue() > 0) {
				getMessageManager().sendMessage(printPath(node.getPath()) + ": a " + name + " is missing! (line : " + lineNumber + ")", MessageManagerInt.errorMessage);
				errors = true;
			}

			/* if a max is < 0, it means there are too many elements */
			try {
				if (((Integer) maxs.next()).intValue() < 0) {
					getMessageManager().sendMessage(printPath(node.getPath()) + ": a " + name + " should be removed! (line : " + lineNumber + ")", MessageManagerInt.errorMessage);
					errors = true;
				}

			} catch (ClassCastException e) {
				/*
				 * ok, max is unbounded and exception is throws when trying to
				 * cast String to Integer
				 */
			}
		}
		
		/* attributes */
		ArrayList checkedAttributes = new ArrayList();
		for (int i = 0; i < attributeList.size(); i++) {
			XsdNode attribute = (XsdNode) attributeList.get(i);
			checkedAttributes.add(attribute.getName());
			if (getValue(attribute) == null || getValue(attribute).length() == 0) {
				if (attribute.isRequired) {
//					errorManager.addMessage(node, "attibute  " + attribute + " is required for " + node,
//							ErrorManager.error);
					getMessageManager().sendMessage(printPath(node.getPath()) + " attibute  " + attribute + " is required for " + node + " (line : " + lineNumber + ")", MessageManagerInt.errorMessage);
					errors = true;
				} 
				else {
					checkedAttributes.add("");
				}
			} else {
				checkedAttributes.add(getValue(attribute));
			}
		}

		if (errors) 
			return "";

		if (xmlCode.trim().length() > 0) {
			xmlCode += "\n"+ indentation;
		}
		
		String value = getValue(node);
		if (value != null && value.trim().length() > 0) {
			xmlCode += value;
		}

		boolean isEmptyElement = (xmlCode == null || xmlCode.length() <= 0);
		
		if (!isEmptyElement) 
			return openElement(node, checkedAttributes, isEmptyElement) + xmlCode + closeElement(node, isEmptyElement);	
		else return openElement(node, checkedAttributes, isEmptyElement);
	}

//	/**
//	 * return an enumeration of all children of given node
//	 * if one of the chidren is transparent, add the child'children instead of the 
//	 * child itself
//	 * @return
//	 */
//	private Iterator getChildren(XsdNode node) {
//		Enumeration enumeration = node.children();
//		ArrayList children = new ArrayList();
//		while (enumeration.hasMoreElements()) {
//			XsdNode child = (XsdNode) enumeration.nextElement();
//			if (child.transparent) {
//				Iterator littleChildren = getChildren(child);
//				while (littleChildren.hasNext()) {
//					children.add(littleChildren.next());
//				}
//			} else
//				children.add(child);
//		}
//
//		return children.iterator();
//	}

}