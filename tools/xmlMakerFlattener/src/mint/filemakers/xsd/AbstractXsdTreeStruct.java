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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.XMLType;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.xml.sax.InputSource;

/**
 * This Class creates and manages a tree representation of a XML schema
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public abstract class AbstractXsdTreeStruct extends Observable {

	/**
	 * XML attributes
	 */
	public static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	/**
	 * XML attributes
	 */
	public static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	/**
	 * XML attributes
	 */
	public static String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	public void loadSchema(File file) throws FileNotFoundException, IOException {
		emptySelectionLists();
		schemaFile = file;
		schemaURL = schemaFile.toURL();
		FileReader xsd = new FileReader(schemaURL.getFile());
		SchemaReader reader = new SchemaReader(new InputSource(xsd));
		schema = reader.read();
		createTree();
		Utils.lastVisitedDirectory = schemaURL.getPath();
		Utils.lastVisitedSchemaDirectory = schemaURL.getPath();
	}

	/**
	 * DefaultTreeSelectionModel tree
	 * 
	 * @uml.property name="tree"
	 */
	public JTree tree;

	/**
	 * 
	 * @uml.property name="xmlErrorHandler"
	 */
	public XmlErrorHandler xmlErrorHandler = new XmlErrorHandler();

	/**
	 * the root node of the tree
	 * 
	 * @uml.property name="rootNode"
	 */
	public XsdNode rootNode;

	/**
	 * 
	 * @uml.property name="treeModel"
	 */
	public DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

	/**
	 * if this variable is setted to true (by the constructor) while expanding a
	 * node, the tree will automaticaly create the minimum amount of nodes
	 * required according by the schema
	 * 
	 * @uml.property name="autoDuplicate"
	 */
	public boolean autoDuplicate;

	/**
	 * this hashmap keep trace of choices made by user when expanding the tree.
	 * It is usefull for example in case of saving/loading . It associate a path
	 * (String) to a name.
	 * 
	 * @uml.property name="expendChoices"
	 * @uml.associationEnd
	 * @uml.property name="expendChoices" multiplicity="(0 -1)"
	 *               elementType="java.lang.String"
	 */
	public ArrayList expendChoices = new ArrayList();

	/**
	 * if this variable is setted to true (by the constructor) while expanding a
	 * node that discribe a choice, the tree will give to the user the
	 * possibility to choose what element to expand. Else all possibility is
	 * displayed
	 * 
	 * @uml.property name="manageChoices"
	 */
	public boolean manageChoices;

	/**
	 * the schema
	 * 
	 * @uml.property name="schema"
	 */
	public Schema schema;

	/**
	 * Returns an instance of <code>AbstractXslTree</code>
	 * 
	 * @param autoduplicate
	 *            indicates that new nodes will be automaticly created according
	 *            to the minimum defined in the schema (minOccurs)
	 */
	public AbstractXsdTreeStruct(boolean autoduplicate, boolean manageChoices) {
		this.autoDuplicate = autoduplicate;
		this.manageChoices = manageChoices;
		this.tree = new JTree(treeModel);
	}

	public AbstractXsdTreeStruct() {
		this.autoDuplicate = true;
		this.manageChoices = true;
		tree = new JTree(treeModel);
	}

	/**
	 * the xsl file describing the schema
	 * 
	 * @uml.property name="schemaFile"
	 */
	public File schemaFile;

	/**
	 * 
	 * @uml.property name="schemaFileName"
	 */
	public URL schemaURL;

	/**
	 * this method should reinitialize every variable makin reference to the
	 * actual tree, such as any <code>List</code> used to make associations to
	 * externals objects.
	 */
	public abstract void emptySelectionLists();

	/**
	 * create the tree according to the schema loaded. The root node is
	 * displayed
	 *  
	 */
	public void createTree() {
		Enumeration elts = schema.getElementDecls();
		ElementDecl elt = (ElementDecl) elts.nextElement();
		XsdNode node = new XsdNode(elt);
		/* rootNode is mandatory */
		rootNode = node;
		rootNode.use();
		treeModel = new DefaultTreeModel(rootNode);
		tree.setModel(treeModel);
	}

	/**
	 * get the number of children with given name for a node used for example to
	 * know if the maximum number of node of that type is already reach or to
	 * create the minimum number of node of that type required according to the
	 * schema
	 */
	public int getChildrenCount(XsdNode node, String childrenName) {
		int count = 0;
		Enumeration childrens = node.children();
		while (childrens.hasMoreElements()) {
			if (((XsdNode) childrens.nextElement()).toString() == childrenName)
				count++;
		}
		return count;
	}

	/**
	 * an enumeration of all children add recursively to this enumeration the
	 * childrens of childrens if child is a choice
	 * 
	 * @result a list of <code>String</code>
	 */
	public ArrayList getChoices(Group g) {
		ArrayList choices = new ArrayList();
		Enumeration childrens = g.enumerate();
		while (childrens.hasMoreElements()) {
			Annotated child = (Annotated) childrens.nextElement();
			choices.add(child);
		}
		return choices;
	}

	/**
	 * type for references used for going deeper in case of normalized document
	 */
	public static String refType = "refType";

	public static String refAttribute = "ref";

	/**
	 * List of the types found in the schema that descrobe a reference to an
	 * element
	 *  
	 */
	public static ArrayList refTypeList = new ArrayList();

	/**
	 * retrun true if an element of this type is a reference to another element.
	 */
	public boolean isRefType(String nodeName) {
		return refTypeList.contains(nodeName);
	}

	/**
	 * describes the node with informations such as its name or its XML type.
	 * Other informations should probably have to be added when extending this
	 * class
	 * 
	 * @param node
	 *            the node on which informations are required
	 * @return a string describing informations about the node
	 */
	public String getInfos(XsdNode node) {
		String infos = "";
		/* name */
		infos += "name: " + node.toString() + "\n";
		/* XML type */
		infos += "Xml type: ";
		switch (((Annotated) node.getUserObject()).getStructureType()) {
		case Structure.ATTRIBUTE:
			infos += "attribute\n";
			infos += "This node is ";
			if (!((AttributeDecl) node.getUserObject()).isRequired())
				infos += "not ";
			infos += "required\n";
			break;
		case Structure.ELEMENT:
			infos += "element\n";
			infos += "minimum occurences: " + node.min + "\n";
			infos += "maximum occurences: ";
			if (node.max >= 0)
				infos += node.max + "\n";
			else
				infos += "unbounded\n\n";
			infos += "This node is ";
			if (((ElementDecl) node.getUserObject()).getMinOccurs() == 0)
				infos += "not ";
			infos += "required\n";
			break;
		default:
			infos += "not XML\n";
		}
		return infos;
	}

	/**
	 * return a understandable <code>String</code> describing the path of a
	 * node, on type: "grandparentNode.ParentNode.Node"
	 * 
	 * @param path
	 *            the path to describe
	 * @return an understandable <code>String</code> to describe the node
	 */
	public String printPath(TreeNode[] path) {
		String value = "";
		for (int i = 0; i < path.length; i++) {
			if (((Annotated) ((XsdNode) path[i]).getUserObject())
					.getStructureType() != Structure.GROUP)
				value += "[" + ((XsdNode) path[i]).toString() + "]";
		}
		return value;
	}

	/**
	 * a node ca have a value if it is an attribute or if it has a simple
	 * content
	 * 
	 * @param node
	 *            a node
	 * @return true if the node can have a value
	 */
	public boolean canHaveValue(XsdNode node) {
		if (((Annotated) node.getUserObject()).getStructureType() == Structure.ATTRIBUTE)
			return true;
		if (!((ElementDecl) node.getUserObject()).getType().isComplexType())
			return true;
		if (((ElementDecl) node.getUserObject()).getType().getBaseType() != null) {
			if (!((ElementDecl) node.getUserObject()).getType().getBaseType()
					.isComplexType())
				return true;
		}
		return false;
	}

	/**
	 * check for errors on this node (lack of associations...) a return an array
	 * of understandable Strings describing the errors
	 * 
	 * @param node
	 *            the node to check
	 * @return an array of Strings describing the errors found
	 */
	public abstract ArrayList check(XsdNode node);

	public ArrayList check() {
		return check(rootNode);
	}

	/**
	 * follow indexes in the tree and return the node found
	 * 
	 * @param indexes
	 *            a string describing the indexes, e.g. 1.1.2.1
	 */
	public XsdNode getNodeByPath(String indexes) {
		String nextIndexes = indexes;
		XsdNode currentNode = null; //rootNode;
		int index = 0;
		while (nextIndexes.indexOf(".") >= 0) {
			index = Integer.parseInt(nextIndexes.substring(0, nextIndexes
					.indexOf(".")));
			nextIndexes = nextIndexes.substring(nextIndexes.indexOf(".") + 1);
			if (currentNode == null) {
				currentNode = rootNode;
			} else {
				/** TODO : check if it really can be commented */
				if (!currentNode.isExtended) {
					extendPath(currentNode);
				}
				currentNode = (XsdNode) currentNode.getChildAt(index);
			}
			if (!currentNode.isExtended)
				extendPath(currentNode);
		}
		index = Integer.parseInt(nextIndexes);
		/* no more "." in the path just a last index */
		if (currentNode == null)
			return rootNode;

		if (!currentNode.isExtended) {
			extendPath(currentNode);
		}
		currentNode = (XsdNode) currentNode.getChildAt(index);
		return currentNode;
	}

	/**
	 * return a String describing the indexes to follow to go from root node to
	 * target node
	 * 
	 * @param node
	 * @return
	 */
	public String getPathForNode(XsdNode node) {
		TreeNode nodesPath[] = node.getPath();
		String path = "0";
		for (int i = 0; i < nodesPath.length - 1; i++) {
			path += "." + nodesPath[i].getIndex(nodesPath[i + 1]);
		}
		return path;
	}

	public void reload() {
		treeModel.reload();
	}

	/**
	 * @return Returns the refAttribute.
	 * 
	 * @uml.property name="refAttribute"
	 */
	public static String getRefAttribute() {
		return refAttribute;
	}

	/**
	 * @param refAttribute
	 *            The refAttribute to set.
	 */
	public static void setRefAttribute(String refAttribute) {
		AbstractXsdTreeStruct.refAttribute = refAttribute;
	}

	/**
	 * @return Returns the refType.
	 * 
	 * @uml.property name="refType"
	 */
	public static String getRefType() {
		return refType;
	}

	/**
	 * @param refType
	 *            The refType to set.
	 */
	public static void setRefType(String refType) {
		AbstractXsdTreeStruct.refType = refType;
	}

	/**
	 * @return Returns the refTypeList.
	 * 
	 * @uml.property name="refTypeList"
	 */
	public static ArrayList getRefTypeList() {
		return refTypeList;
	}

	/**
	 * @param refTypeList
	 *            The refTypeList to set.
	 */
	public static void setRefTypeList(ArrayList refTypeList) {
		AbstractXsdTreeStruct.refTypeList = refTypeList;
	}

	/**
	 * @return Returns the autoDuplicate.
	 *  
	 */
	public boolean isAutoDuplicate() {
		return autoDuplicate;
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
	 * @return Returns the manageChoices.
	 *  
	 */
	public boolean isManageChoices() {
		return manageChoices;
	}

	/**
	 * @return Returns the rootNode.
	 * 
	 * @uml.property name="rootNode"
	 */
	public XsdNode getRootNode() {
		return rootNode;
	}

	/**
	 * @param rootNode
	 *            The rootNode to set.
	 * 
	 * @uml.property name="rootNode"
	 */
	public void setRootNode(XsdNode rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * @return Returns the schema.
	 * 
	 * @uml.property name="schema"
	 */
	public Schema getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *            The schema to set.
	 * 
	 * @uml.property name="schema"
	 */
	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	/**
	 * @return Returns the schemaFile.
	 * 
	 * @uml.property name="schemaFile"
	 */
	public File getSchemaFile() {
		return schemaFile;
	}

	/**
	 * @param schemaFile
	 *            The schemaFile to set.
	 * 
	 * @uml.property name="schemaFile"
	 */
	public void setSchemaFile(File schemaFile) {
		this.schemaFile = schemaFile;
	}

	/**
	 * @return Returns the tree.
	 * 
	 * @uml.property name="tree"
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * @param tree
	 *            The tree to set.
	 * 
	 * @uml.property name="tree"
	 */
	public void setTree(JTree tree) {
		this.tree = tree;
	}

	/**
	 * @return Returns the treeModel.
	 * 
	 * @uml.property name="treeModel"
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	/**
	 * @param treeModel
	 *            The treeModel to set.
	 * 
	 * @uml.property name="treeModel"
	 */
	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	/**
	 * @param autoDuplicate
	 *            The autoDuplicate to set.
	 * 
	 * @uml.property name="autoDuplicate"
	 */
	public void setAutoDuplicate(boolean autoDuplicate) {
		this.autoDuplicate = autoDuplicate;
	}

	/**
	 * @param manageChoices
	 *            The manageChoices to set.
	 * 
	 * @uml.property name="manageChoices"
	 */
	public void setManageChoices(boolean manageChoices) {
		this.manageChoices = manageChoices;
	}

	public void redoChoice(String path, String choice) {
		XsdNode currentNode = rootNode;
		/* never a choice on rootNode */
		String nextIndexes = path.substring(path.indexOf(".") + 1);
		int index = 0;

		/* for each element on the path */
		while (nextIndexes.length() > 0) {
			/* if choice do it */
			Annotated annotated = (Annotated) (currentNode.getUserObject());
			if (annotated.getStructureType() == Structure.GROUP) {
				/* if it's a complex type, look inside */

				Group g = (Group) annotated;
				try {
					if (g.getOrder().getType() == Order.CHOICE && manageChoices) {
						XsdNode parent = (XsdNode) currentNode.getParent();
						int position = parent.getIndex(currentNode);
						ArrayList choices = getChoices(g);
						ArrayList possibilities = new ArrayList();
						for (int i = 0; i < choices.size(); i++) {
							try {
								possibilities
										.add(((ElementDecl) choices.get(i))
												.getName());
							} catch (ClassCastException e) {
								/* a group: give an overview */
								possibilities
										.add(XsdNode
												.choiceToString((Group) choices
														.get(i)));
							}
						}
						//						String choice = (String) expendChoices.get(path);
						((XsdNode) currentNode.getParent()).remove(parent
								.getIndex(currentNode));
						XsdNode newNode;
						newNode = new XsdNode((Annotated) choices
								.get(possibilities.indexOf(choice)));
						newNode.isRequired = currentNode.isRequired;
						newNode.min = currentNode.min;
						newNode.max = currentNode.max;
						newNode.originalParent = currentNode;
						parent.insert(newNode, position);
						currentNode = newNode;
					} else {
						/* if not extended, do it */
						if (!currentNode.isExtended) {
							extendPath(currentNode);
						} else {
							index = Integer.parseInt(nextIndexes.substring(0,
									nextIndexes.indexOf(".")));
							nextIndexes = nextIndexes.substring(nextIndexes
									.indexOf(".") + 1);
							currentNode = (XsdNode) currentNode
									.getChildAt(index);
						}
					}
				} catch (StringIndexOutOfBoundsException e) {
					return;
				}
			} else {
				/* if not extended, do it */
				if (!currentNode.isExtended) {
					extendPath(currentNode);
				} else {
					if (nextIndexes.indexOf(".") >= 0) {
						index = Integer.parseInt(nextIndexes.substring(0,
								nextIndexes.indexOf(".")));
						nextIndexes = nextIndexes.substring(nextIndexes
								.indexOf(".") + 1);
						currentNode = (XsdNode) currentNode.getChildAt(index);
					} else {

						index = Integer.parseInt(nextIndexes);
						nextIndexes = "-1";
						try {
							currentNode = (XsdNode) currentNode
									.getChildAt(index);
						} catch (ArrayIndexOutOfBoundsException e) {
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * @return Returns the schemaURI.
	 */
	public URL getSchemaURL() {
		return schemaURL;
	}

	/**
	 * @param schemaURI
	 *            The schemaURI to set.
	 */
	public void setSchemaURL(URL schemaURI) {
		this.schemaURL = schemaURI;
	}

	/**
	 * @return Returns the sCHEMA_LANGUAGE.
	 */
	public static String getSCHEMA_LANGUAGE() {
		return SCHEMA_LANGUAGE;
	}

	/**
	 * @param schema_language
	 *            The sCHEMA_LANGUAGE to set.
	 */
	public static void setSCHEMA_LANGUAGE(String schema_language) {
		SCHEMA_LANGUAGE = schema_language;
	}

	/**
	 * @return Returns the sCHEMA_SOURCE.
	 */
	public static String getSCHEMA_SOURCE() {
		return SCHEMA_SOURCE;
	}

	/**
	 * @param schema_source
	 *            The sCHEMA_SOURCE to set.
	 */
	public static void setSCHEMA_SOURCE(String schema_source) {
		SCHEMA_SOURCE = schema_source;
	}

	/**
	 * @return Returns the xML_SCHEMA.
	 */
	public static String getXML_SCHEMA() {
		return XML_SCHEMA;
	}

	/**
	 * @param xml_schema
	 *            The xML_SCHEMA to set.
	 */
	public static void setXML_SCHEMA(String xml_schema) {
		XML_SCHEMA = xml_schema;
	}

	/**
	 * @return Returns the xmlErrorHandler.
	 * 
	 * @uml.property name="xmlErrorHandler"
	 */
	public XmlErrorHandler getXmlErrorHandler() {
		return xmlErrorHandler;
	}

	/**
	 * @param xmlErrorHandler
	 *            The xmlErrorHandler to set.
	 * 
	 * @uml.property name="xmlErrorHandler"
	 */
	public void setXmlErrorHandler(XmlErrorHandler xmlErrorHandler) {
		this.xmlErrorHandler = xmlErrorHandler;
	}

	/**
	 * check elements and attributes for the structure contained in this node
	 * create nodes and add'em to the tree
	 */
	public void extendPath(XsdNode node) {
		Annotated annotated = (Annotated) (node.getUserObject());
		String path = getPathForNode(node);
		switch (annotated.getStructureType()) {
		case Structure.ATTRIBUTE:
			break;

		case Structure.GROUP:
			/* if it's a complex type, look inside */
			Group g = (Group) annotated;

			XsdNode parent = (XsdNode) node.getParent();

			/* position is important when adding new node */
			int position = parent.getIndex(node);

			/*
			 * if a sequence: add all childs if a choice, ask user
			 */
			if (g.getOrder().getType() == Order.CHOICE && manageChoices) {
				((XsdNode) node.getParent()).remove(parent.getIndex(node));

				XsdNode newNode;

				String choice = (String) expendChoices.get(expendChoices
						.indexOf(path) + 1);

				ArrayList choices = getChoices(g);
				ArrayList possibilities = new ArrayList();
				for (int i = 0; i < choices.size(); i++) {
					try {
						possibilities.add(((ElementDecl) choices.get(i))
								.getName());
					} catch (ClassCastException e) {
						/* a group: give an overview */
						possibilities.add(XsdNode
								.choiceToString((Group) choices.get(i)));
					}
				}

				newNode = new XsdNode((Annotated) choices.get(possibilities
						.indexOf(choice)));

				expendChoices.add(path);
				expendChoices.add(choice);

				newNode.isRequired = node.isRequired;
				newNode.min = node.min;
				newNode.max = node.max;
				newNode.originalParent = node;
				parent.insert(newNode, position);

				if (((Annotated) newNode.getUserObject()).getStructureType() != Structure.GROUP)
					extendPath(newNode);
				else if (((Group) newNode.getUserObject()).getOrder().getType() != Order.CHOICE)
					extendPath(newNode);

			} else { /* sequence */
				int parentIndex = parent.getIndex(node);
				((XsdNode) node.getParent()).remove(parent.getIndex(node));

				Enumeration elts = g.enumerate();
				boolean firstElement = true;
				while (elts.hasMoreElements()) {

					Annotated element = (Annotated) elts.nextElement();

					XsdNode newNode = new XsdNode(element);
					if (firstElement) {
						parent.insert(newNode, parentIndex);
						firstElement = false;
					} else
						parent.add(newNode);

					if (((Annotated) newNode.getUserObject())
							.getStructureType() != Structure.GROUP)
						extendPath(newNode);
					else if (((Group) newNode.getUserObject()).getOrder()
							.getType() != Order.CHOICE)
						extendPath(newNode);

					if (autoDuplicate) {
						if (element.getStructureType() == Structure.ELEMENT) {
							for (int i = 1; i < ((ElementDecl) element)
									.getMinOccurs(); i++) {

								newNode = new XsdNode(element);
								parent.add(newNode);

								if (((Annotated) newNode.getUserObject())
										.getStructureType() != Structure.GROUP)
									extendPath(newNode);
								else if (((Group) newNode.getUserObject())
										.getOrder().getType() != Order.CHOICE)
									extendPath(newNode);
							}
						}
					}
				}
			}
			check((XsdNode) treeModel.getRoot());
			treeModel.reload(parent);
			break;
		case Structure.ELEMENT:
			/* if it's a complex type, look inside */
			XMLType type = ((ElementDecl) annotated).getType();
			/* no type : send message */
			if (type == null) {
				System.out.println("WARNING: no type declaration for element "
						+ node.toString());
				return;
			}
			if (type.isSimpleType())
				return;
			Enumeration attributes = ((ComplexType) type).getAttributeDecls();
			while (attributes.hasMoreElements()) {
				node.add(new XsdNode((Annotated) attributes.nextElement()));
			}

			Enumeration elements = ((ComplexType) type).enumerate();
			while (elements.hasMoreElements()) {
				Particle ptc = (Particle) elements.nextElement();
				XsdNode child = new XsdNode((Annotated) ptc);
				node.add(child);
				if (ptc.getStructureType() != Structure.GROUP)
					extendPath(child);
				else if (((Group) child.getUserObject()).getOrder().getType() != Order.CHOICE)
					extendPath(child);
			}

			/* do not forget the base type */
			try {
				attributes = ((ComplexType) type.getBaseType())
						.getAttributeDecls();
				while (attributes.hasMoreElements()) {
					node.add(new XsdNode((Annotated) attributes.nextElement()));
				}
				elements = ((ComplexType) type.getBaseType()).enumerate();
				while (elements.hasMoreElements()) {
					Particle ptc = (Particle) elements.nextElement();
					XsdNode child = new XsdNode((Annotated) ptc);
					node.add(child);
					if (ptc.getStructureType() != Structure.GROUP)
						extendPath(child);
					else if (((Group) child.getUserObject()).getOrder()
							.getType() != Order.CHOICE)
						extendPath(child);
				}
			} catch (Exception e) {
				/* no base type */
			}

			check((XsdNode) treeModel.getRoot());
			treeModel.reload(node);
			break;
		}
		node.isExtended = true;
	} // extendPath

}