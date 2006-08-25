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
package psidev.psi.mi.filemakers.xmlFlattener.structure;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.Structure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import psidev.psi.mi.filemakers.xmlFlattener.mapping.TreeMapping;
import psidev.psi.mi.filemakers.xsd.AbstractXsdTreeStruct;
import psidev.psi.mi.filemakers.xsd.Utils;
import psidev.psi.mi.filemakers.xsd.XsdNode;

/**
 * 
 * This class overides the abstract class AbstractXslTreeStruct to provide a
 * tree representation of a XML schema, with management of transformation of an
 * XML file to a flat file.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XsdTreeStructImpl extends AbstractXsdTreeStruct {

	public static final int NUMERIC_NUMEROTATION = 0;

	public static final int HIGH_ALPHABETIC_NUMEROTATION = 1;

	public static final int LOW_ALPHABETIC_NUMEROTATION = 2;

	public static final int NO_NUMEROTATION = 3;

	public int numerotation_type = NUMERIC_NUMEROTATION;

	int curElementsCount = 0;

	/**
	 * the XML document to parse and transform into a flat file
	 */
	public Document document = null;

	/**
	 * 
	 * @uml.property name="documentURL"
	 */
	public URL documentURL = null;

	/**
	 * the separator for the flat file
	 * 
	 * @uml.property name="separator"
	 */
	public String separator = "|";

	/**
	 * the node associated to a line of the flat file. if null, the printer will
	 * look for the deeper node that is an ancestor of every selection.
	 * 
	 * @uml.property name="lineNode"
	 */
	public XsdNode lineNode = null;

	/**
	 * true if the user has choosed a node that contains what he wants to see on
	 * a line of the flat file
	 */
	public boolean lineNodeIsSelected = false;

	/**
	 * the elements of ths XML document associated to a line of the flat file.
	 * if null, the printer will look for the deeper node that is an ancestor of
	 * every selection.
	 */
	public ArrayList lineElements = null;

	/**
	 * type of marshalling: for creating a line with columns titles
	 */
	public final static int TITLE = 0;

	/**
	 * type of marshalling: for not creating a line with columns titles
	 */
	public final static int FULL = 1;

	/**
	 * create a new instance of XslTree The nodes will not be automaticaly
	 * duplicated even if the schema specify that more than one element of this
	 * type is mandatory
	 */
	public XsdTreeStructImpl() {
		super(false, false);
	}

	/**
	 * this map contains regular expression used to filter XML node if a node do
	 * not validate the regexp, itself or its parent element (in case of
	 * attribute) will be ignored
	 */
	public HashMap elementFilters = new HashMap();

	/**
	 * set the separator for the flat file
	 * 
	 * @param s
	 *            the separator
	 * 
	 * @uml.property name="separator"
	 */
	public void setSeparator(String s) {
		separator = s;
	}

	/**
	 * check for errors on this node (lack of associations...) and return an
	 * array of understandable Strings describing the errors
	 * 
	 * @param node
	 *            the node to check
	 * @return an array of Strings describing the errors found
	 */
	public boolean check(XsdNode node) {
		return true;
	}

	/**
	 * this method should reinitialize every variable making reference to the
	 * actual tree, such as any <code>List</code> used to make associations to
	 * externals objects.
	 * 
	 * set selection as a new <code>ArrayList</code>
	 */
	public void emptySelectionLists() {
		ArrayList selectionsCopy = new ArrayList();
		selectionsCopy.addAll(selections);
		Iterator it = selectionsCopy.iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			unselectNode(node);
		}
		lineNode = null;
		elementFilters = new HashMap();
	}


	HashMap referencedElements = new HashMap(); 

	/**
	 * Open a frame to choose an XML document and load it.
	 *  
	 */
	public void loadDocument(URL url) throws FileNotFoundException,
			NullPointerException, MalformedURLException, IOException,
			SAXException {
		maxCounts = new HashMap();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);

		factory.setAttribute(SCHEMA_LANGUAGE, XML_SCHEMA);
		factory.setAttribute(SCHEMA_SOURCE, schemaFile);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			System.out.println(url.toString());
			builder.setErrorHandler(xmlErrorHandler);
			document = builder.parse(url.toString());
			this.documentURL = url;

			/* get all references */
			System.out.println("get keys/keyRefs") ;
			buidKeyMaps();
			for (Iterator it = refType2referedType.keySet().iterator(); it.hasNext();) {
				String refer = (String) it.next();
				String refered = (String) refType2referedType.get(refer);
				System.out.println("found reftype: "+refer+ " refers "+refered);
			}
			System.out.println("done") ;

			System.out.println("document parsed ... get elements");
			setLineNode(lineNode);

			Utils.lastVisitedDirectory = url.getPath();
			Utils.lastVisitedDocumentDirectory = url.getPath();
		} catch (ParserConfigurationException e) {
			/** TODO: manage excepton */
		}
	}
	
	private HashMap xsKeyNodes = new HashMap();

	private void getKeyNodes(String keyName, String keySelector, String keyField) {
		
		if (document == null)
			return;
		
		String[] path = keySelector.split("/");
		
		/* the node that contains all keys */
		/* find the parent node */
		Node nodeContainer = getContainer(document, path, 0);
		
		if (nodeContainer == null)
			return;
		System.out.println("found list of refered node: "+nodeContainer.getNodeName());
		
		for (int i = 0; i< nodeContainer.getChildNodes().getLength(); i++) {
			Node child = nodeContainer.getChildNodes().item(i);
			String name = child.getNodeName();
			/* get refId name */
			String idFieldName = getReferedIdFieldName(name); 
			if (child.hasAttributes()) {
				for (int j = 0; j < child.getAttributes().getLength(); j++ ) {
					if (child.getAttributes().item(j).getNodeName().equals(idFieldName)) {
						String ref = child.getAttributes().item(j).getTextContent();
						xsKeyNodes.put(keyName+"#"+ref, child);//keyName
						System.out.println("add: "+keyName+"#"+ref);
					}
				}
			}
			
		}
		
		
	}
	
	private Node getContainer(Node node, String[] path, int startIdx) {
		if (startIdx == path.length - 1)
			return node;
		if (!node.hasChildNodes()) {
			System.out.println("no more nodes");
			return null;
		}
		
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			if (node.getChildNodes().item(j).getNodeName().equals(path[startIdx])) {
				return getContainer(node.getChildNodes().item(j), path,
						++startIdx);
			}
		}

//		System.out.println("finish");
		return null;
	}
	
	private String getReferedIdFieldName(String key) {
		return "id";	
	}
	
	
	private void buidKeyMaps() {
		
		System.out.println("get keys");
		for (Iterator it = keyz.iterator(); it.hasNext();) {
			Node node = (Node) it.next();
			String keyName = null;
			String keySelector = null;
			String keyField = null;
			
			if (node.hasAttributes()) {
				for (int i = 0; i < node.getAttributes().getLength(); i++) {
					if (node.getAttributes().item(i).getNodeName().equals(
							"name")) {
						keyName = node.getAttributes().item(i).getTextContent();

					}
				}
			}
			
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				Node child = node.getChildNodes().item(i);

				if (child.getNodeName().equals("xs:selector")) {
					for (int j = 0; j < child.getAttributes().getLength(); j++) {
						if (child.getAttributes().item(j).getNodeName().equals(
								"xpath")) {
							keySelector = getXpath(child.getParentNode().getParentNode()) +"/"+child.getAttributes().item(j)
									.getTextContent();
						}
					}
				} else if (child.getNodeName().equals("xs:field")) {
					for (int j = 0; j < child.getAttributes().getLength(); j++) {
						if (child.getAttributes().item(j).getNodeName().equals(
								"xpath")) {
							keyField = child.getAttributes().item(j)
									.getTextContent().replace("@", "");
						}
					}
				}
			}
			getKeyNodes(keyName, keySelector, keyField);

		}
		
		System.out.println("get keyRefs");
		for (Iterator it = keyRefs.iterator(); it.hasNext();) {
			Node node = (Node) it.next();
			String keyRefName = null;
			String keyRefRefer = null;
			String keyRefSelector = null;
			String keyRefField = null;
			
			if (node.hasAttributes()) {
				for (int i = 0; i < node.getAttributes().getLength(); i++) {
					if (node.getAttributes().item(i).getNodeName().equals(
							"name")) {
						keyRefName = node.getAttributes().item(i)
								.getTextContent();
					} else if (node.getAttributes().item(i).getNodeName()
							.equals("refer")) {
						keyRefRefer = node.getAttributes().item(i)
								.getTextContent();
					}
				}
			}
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				Node child = node.getChildNodes().item(i);
				if (child.getNodeName().equals("xs:selector")) {
					for (int j = 0; j < child.getAttributes().getLength(); j++) {
						if (child.getAttributes().item(j).getNodeName().equals(
								"xpath")) {
							keyRefSelector = child.getAttributes().item(j)
									.getTextContent();
						}
					}
				} else if (child.getNodeName().equals("xs:field")) {
					for (int j = 0; j < child.getAttributes().getLength(); j++) {
						if (child.getAttributes().item(j).getNodeName().equals(
								"xpath")) {
							keyRefField = child.getAttributes().item(j)
									.getTextContent();
						}
					}
				}
			}
			refType2referedType.put(getSchemaXpath(node.getParentNode())+"/"+keyRefSelector,keyRefRefer );
	}
	}


	
	/**
	 * 
	 * @uml.property name="lineNode"
	 */
	public void setLineNode(XsdNode lineNode) {
		this.lineNode = lineNode;
		maxCounts = new HashMap();

		lineElements = getNodes(lineNode.getPath());
		treeModel.reload(lineNode);

		lineNodeIsSelected = true;
	}

	public String getInfos(XsdNode node) {
		String infos = super.getInfos(node);
		infos += "selected: " + selections.contains(node) + "\n";
		return infos;
	}

	/**
	 * this <code>HashMap</code> keep the maximum amount of a node type found
	 * in the file for a type of node. The key is the String association of the
	 * name of the parent and the name of the node
	 */
	public HashMap maxCounts = new HashMap();

	/**
	 * follow a path in the document to find corresponding element
	 * 
	 * @return the node in the XML document that found by following the path
	 *  
	 */
	public ArrayList getNodes(TreeNode[] path) {
		if (document == null)
			return null;

		Node value = document.getDocumentElement();
		ArrayList list = getXmlElements(path, value, 0);
		curElementsCount = list.size();
		System.out.println(curElementsCount + " elements found for selection.");
		return list;

	}

	public ArrayList getXmlElements(TreeNode[] path, Node xmlNode, int pathIndex) {
		ArrayList list = new ArrayList();
		
		if (pathIndex < path.length - 1) {
			NodeList children = xmlNode.getChildNodes();

			for (int j = 0; j < children.getLength(); j++) {
				if (((XsdNode) path[pathIndex + 1]).toString().compareTo(
						children.item(j).getNodeName()) == 0) {
					list.addAll(getXmlElements(path, children.item(j),
							pathIndex + 1));
				}
			}

			return list;
		} else {
			list.add(xmlNode);
			return list;
		}
	}

	/**
	 * 
	 * @param element
	 *            an element of the XML document
	 * @return value of this element if it exists, an empty String else
	 */
	public String getElementValue(Element element) {
		try {
			NodeList children = element.getChildNodes();
			String value = "";
			for (int i = 0; i < children.getLength(); i++) {
//				System.out.println("go "+i+children.item(i).getNodeValue()+" _ "+children.item(i).getNodeType()+"/"+Node.ATTRIBUTE_NODE);
//				if (children.item(i).getNodeType() == Node.ATTRIBUTE_NODE) {
//					System.out.println("attribute");
//				
//					if (elementFilters.containsKey(children.item(i))) {
//						try {
//							System.out.println("FSGFHGJJJFg");
//							String value2 = children.item(i).getNodeValue();
//							/** TODO: done for managing filter */
//							if (!value.matches((String) elementFilters
//									.get(children.item(i)))) {
//								return "";
//							}
//						} catch (NullPointerException e) {
//							return "";
//						}
//					}
//					
//				}
				
				if (children.item(i).getNodeName() == "#text")
					value = children.item(i).getNodeValue();
			}
			
			
			return value;
		} catch (NullPointerException e) {
			/** element is null */
			return "";
		}
		

		
		
	}

	/**
	 * look in the XML schema for the deepest node that is an ancester of every
	 * nodes selected @ return the deepest node in the XML schema that is an
	 * ancester of every nodes selected
	 */
	public void setXmlRoot() {
		if (lineNodeIsSelected)
			return;

		if (selections.size() == 0) {
			lineNode = (XsdNode) treeModel.getRoot();
			lineElements = getNodes(lineNode.getPath());
			return;
		}

		XsdNode value = rootNode;
		XsdNode tmp = null;
		XsdNode select = null;
		XsdNode lastDuplicable = null;
		// go down while no more than one child is used and current node
		// is not selected

		// keep last node duplicable

		Enumeration children = value.children();
		int nb = 0;
		while (nb <= 1 && !selections.contains(tmp)) {

			if (children.hasMoreElements()) {
				XsdNode child;
				child = (XsdNode) children.nextElement();
				if (child.isUsed) {
					tmp = child;
					nb++;
				}
				if (selections.contains(child)) {
					select = child;
				}
			} else {
				nb = 0;
				if (tmp.isDuplicable())
					lastDuplicable = tmp;
				value = tmp;
				children = value.children();
			}
		}
		if (nb <= 1)
			value = select;
		System.out.println("[PSI makers: flattener] root selected: "
				+ value.toString());

		lineNode = lastDuplicable;

		lineElements = getNodes(lineNode.getPath());

	}

	/**
	 * true if currently printed element is the first of a ligne. Used to know
	 * if a separator is needed when prining the flat file.
	 */
	public boolean firstElement = true;

	/**
	 * create a flat file separated by specified separator containing every
	 * element contained in the XML document and selected on the tree, with a
	 * first line that contains the tiltles of columns
	 * 
	 * @param out
	 *            the <code>writer</code> where to print the file
	 * @throws IOException
	 */
	public void write(Writer out) throws IOException {
		/*
		 * get the first interresting node, ie the deepest one that is an
		 * ancestor of every selected node, in the schema and corresponding
		 * nodes in the the document
		 */
		setXmlRoot();
		firstElement = true;
		/* marshall once for title */
		out.write(getTitle(lineNode) + "\n");
		out.flush();
		//        writeNode(lineNode, (Element) lineElements.get(0), TITLE, out);
		//		out.write("\n");
		firstElement = true;
		/* marshall each element */
		for (int i = 0; i < lineElements.size(); i++) {
			firstElement = true;
			writeNode(lineNode, (Element) lineElements.get(i), out, false);
			out.write("\n");
			out.flush();
		}
	}

	/**
	 * get the maximum amount of element of a type as child of another type of
	 * element in the XML document. It is used to know how many columns have to
	 * be created in the flat file, as even empty ones have to be printed.
	 */
	public int getMaxCount(XsdNode node) {
		XsdNode originalNode = node;
		/* if no document loaded */
		if (lineElements == null) {
			return 0;
		}
		/* max count already computed */
		if (maxCounts.containsKey(node)) {
			return ((Integer) maxCounts.get(node)).intValue();
		}
		int count = 0;
		int max = 0;

		/** for attributes get number of parent element */
		if (((Annotated) node.getUserObject()).getStructureType() == Structure.ATTRIBUTE) {
			node = (XsdNode) node.getParent();
		}
		for (int i = 0; i < lineElements.size(); i++) {
			count = getMaxCount((Node) lineElements.get(i), lineNode, node,
					node.pathFromAncestorEnumeration(lineNode));
			if (count > max)
				max = count;
		}

		/* the fields are kept even if no element have been found */
		if (max < node.min) {
			max = node.min;
		}
		if (max == 0) {
			max = 1;
		}

		/* keep the result */
		maxCounts.put(originalNode, new Integer(max));

		return max;
	}

	/**
	 * get the maximum amount of element of a type as child of another type of
	 * element in the XML document. It is used to know how many columns have to
	 * be created in the flat file, as even empty ones have to be printed.
	 * 
	 * @param element
	 *            the element in the XML document
	 * @param parent
	 *            the parent of the node on the tree
	 * @param target
	 *            the node on the tree
	 * @param path
	 *            the path to access to the node
	 */
	public int getMaxCount(Node element, XsdNode parent, XsdNode target,
			Enumeration path) {

		if (target == parent) {
			if (elementFilters.containsKey(target)) {
				try {
					String value = ((Element) element).getAttributeNode(
							target.toString()).getNodeValue();
					/** TODO: done for managing filter */
					if (!value.matches((String) elementFilters.get(target)))
						return 0;
				} catch (NullPointerException e) {
					return 0;
				}
			}
			return 1;
		}

		int currentMax = 0;
		int totalCount = 0;
		int max = 0;
		//        int count = 0;

		path.nextElement();
		XsdNode nextNode = (XsdNode) path.nextElement();

		/* get all childrens and refs */
		NodeList childrens = element.getChildNodes();

		for (int indexChildrens = 0; indexChildrens < childrens.getLength(); indexChildrens++) {
			Node child = childrens.item(indexChildrens);

			/**
			 * check for the name: a single node in the tree could have numerous
			 * corresponding elements in the XML document, that could be either
			 * the node itself or a reference.
			 */
			if (child.getNodeType() == Structure.ATTRIBUTE) {
				Enumeration xsdChildrens = target.children();
				while (xsdChildrens.hasMoreElements()) {
					XsdNode xsdChild = (XsdNode) xsdChildrens.nextElement();

					if (elementFilters.containsKey(xsdChild)) {
						try {
							String value = ((Element) element)
									.getAttributeNode(child.toString())
									.getNodeValue();
							/** TODO: done for managing filter */
							//if (value != null && value.length() > 0) {
							if (!value.matches((String) elementFilters
									.get(child)))
								return 0;
							//}
						} catch (NullPointerException e) {
							return 0;
						}
					}
				}
			}

			/* direct childrens */
			if (child.getNodeName().compareTo(nextNode.toString()) == 0) {
				//                count++;
				currentMax = getMaxCount(child, nextNode, target, target
						.pathFromAncestorEnumeration(nextNode));
				totalCount += currentMax;
				if (((XsdNode) target.getParent()).toString().compareTo(
						parent.toString()) == 0) {
					max += currentMax;
				} else {
					if (currentMax > max)
						max = currentMax;
				}
			}

			/* references */
//			else if (isRefType(child.getNodeName())) {
//				Element ref = getElementById(((Element) child) //document.
//						.getAttribute(refAttribute));
//				if (ref != null
//						&& ref.getNodeName().compareTo(nextNode.toString()) == 0) {
//					//                    count++;
//					currentMax = getMaxCount(ref, nextNode, target, target
//							.pathFromAncestorEnumeration(nextNode));
//					totalCount += currentMax;
//					if (((XsdNode) target.getParent()).toString().compareTo(
//							parent.toString()) == 0) {
//						max += currentMax;
//					} else if (currentMax > max) {
//						max = currentMax;
//					}
//				}
//			}

			/* key ref */
			else if (isXsRefPath(child)) {
				Element ref = this.getElementByKeyRef(child); //((Element) child) //document.
//						.getAttribute(refAttribute), child.getNodeName());
				if (ref != null
						&& ref.getNodeName().compareTo(nextNode.toString()) == 0) {
					//                    count++;
					currentMax = getMaxCount(ref, nextNode, target, target
							.pathFromAncestorEnumeration(nextNode));
					totalCount += currentMax;
					if (((XsdNode) target.getParent()).toString().compareTo(
							parent.toString()) == 0) {
						max += currentMax;
					} else if (currentMax > max) {
						max = currentMax;
					}
				}
			}
			/* ID */
			else if (isRefType(child.getNodeName())) {
				Element ref = getElementById(((Element) child) //document.
						.getAttribute(refAttribute));
				if (ref != null
						&& ref.getNodeName().compareTo(nextNode.toString()) == 0) {
					//                    count++;
					currentMax = getMaxCount(ref, nextNode, target, target
							.pathFromAncestorEnumeration(nextNode));
					totalCount += currentMax;
					if (((XsdNode) target.getParent()).toString().compareTo(
							parent.toString()) == 0) {
						max += currentMax;
					} else if (currentMax > max) {
						max = currentMax;
					}
				}
			}			
			
			
			
		}
		return max;
	}

	/**
	 * add to the flat file the content of a node
	 * 
	 * @param xsdNode
	 *            the node in the tree to parse
	 * @param xmlElement
	 *            the element in tghe XML document
	 * @param marshallingType
	 *            title or full parsing
	 * @param out
	 * @throws IOException
	 */
	public boolean writeNode(XsdNode xsdNode, Node xmlElement, Writer out,
			boolean empty) throws IOException {

		/** first check if the element do not have to be filtered */
		if (!empty) {
			Enumeration children = xsdNode.children();
			while (children.hasMoreElements()) {
				XsdNode child = (XsdNode) children.nextElement();

				if (((Annotated) child.getUserObject()).getStructureType() == Structure.ATTRIBUTE) {
					if (elementFilters.containsKey(child)) {
						try {
							String value = ((Element) xmlElement)
									.getAttributeNode(child.toString())
									.getNodeValue();
							/** TODO: done for managing filter */
							 Pattern p = Pattern.compile((String) elementFilters
										.get(child));
							 Matcher m = p.matcher(value);
							 boolean b = m.matches();
							
							 if (!b) {
								return false;
							}
						} catch (NullPointerException e) {
							return false;
						}
					}
				}
			}
		}

		if (!xsdNode.isUsed) {
			return false;
		}

		if (selections.contains(xsdNode)) {
			if (xmlElement != null || empty) {
				String value = getElementValue((Element) xmlElement);
				/** TODO: done for managing filter */
				/** if empty marshalling, we do not care about filters */
				if (elementFilters.containsKey(xsdNode) && !empty &&  elementFilters.get(xsdNode) != null &&  ((String)elementFilters.get(xsdNode)).length() > 0) {
					if (value.matches((String) elementFilters.get(xsdNode))) {
						if (firstElement)
							firstElement = false;
						else
							out.write(separator);
						out.write(getElementValue((Element) xmlElement));
					}
				} else {
					if (firstElement)
						firstElement = false;
					else
						out.write(separator);
					out.write(getElementValue((Element) xmlElement));
				}
			}
		}

		//Enumeration
		Enumeration children = xsdNode.children();
		while (children.hasMoreElements()) {
			XsdNode child = (XsdNode) children.nextElement();
			if (child.isUsed) {
				switch (((Annotated) child.getUserObject()).getStructureType()) {
				case Structure.ELEMENT:
					/** number of element found */
					int cpt = 0;
					/** number of elements really marshalled, ie not filtered */
					int nbElementFound = 0;
					/* create a NodeList with all childs with tagname */
					if (xmlElement != null) {
						NodeList allElements = xmlElement.getChildNodes();
						ArrayList elements = new ArrayList();
						/**
						 * number of element found: could be lower than
						 * elements's length due to filters
						 */
						for (int i = 0; i < allElements.getLength(); i++) {
							if (allElements.item(i).getNodeName().compareTo(
									child.toString()) == 0) {
								elements.add(allElements.item(i));
							}


							/* get refence by xs:key */
							else if (isXsRefPath(allElements.item(i)
									)) {
								Element ref = //document.
								this.getElementByKeyRef(allElements.item(i));
//										((Element) allElements
//												.item(i))
//												.getAttribute(refAttribute),allElements.item(i)
//												.getNodeName());
								if (ref != null
										&& ref.getNodeName().compareTo(
												child.toString()) == 0) {
									elements.add(ref);
								}
							}
							
							/* get references by XML id */
							else if (isRefType(allElements.item(i)
									.getNodeName())) {
								Element ref = //document.
								getElementById(((Element) allElements
												.item(i))
												.getAttribute(refAttribute));
								if (ref != null
										&& ref.getNodeName().compareTo(
												child.toString()) == 0) {
									elements.add(ref);
								}
							}
							
						}
						while (cpt < elements.size()) {
							boolean notEmptyElement = writeNode(child,
									(Node) elements.get(cpt), out, false);
							cpt++;
							if (notEmptyElement)
								nbElementFound++;
						}
					}
					int maxCount = getMaxCount(child);
					while (nbElementFound < maxCount) {
						writeNode(child, null, out, true);
						nbElementFound++;
					}
					break;
				case Structure.ATTRIBUTE:
					if (firstElement)
						firstElement = false;
					else
						out.write(separator);

					if (xmlElement != null) {
						try {
							out.write(((Element) xmlElement).getAttributeNode(
									child.toString()).getNodeValue());
						} catch (NullPointerException ne) {
							/* main cause: the cell does not exist */
						}
					}
					break;
				default:
					System.out
							.println("[PSI makers: flattener] ERROR: the node is neither an attribute nor an element");
				}
			}
		}
		return true;
	}

	/**
	 * Such as write but return a String instead of writing in a file. Only for
	 * the marshalling type title.
	 * 
	 * @param node
	 * @param element
	 * @param marshallingType
	 * @return
	 */
	public String getTitle(XsdNode node) {
		String out = "";

		/** TODO: new */
		getMaxCount(node);

		if (!node.isUsed) {
			return out;
		}

		if (selections.contains(node)) {
			if (firstElement)
				firstElement = false;
			else
				out += separator;
			out += node.getName() + nextNumber(node);
		}

		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			XsdNode child = (XsdNode) children.nextElement();

			if (child.isUsed) {
				switch (((Annotated) child.getUserObject()).getStructureType()) {
				case Structure.ELEMENT:
					int cpt = 0;
					/* create a NodeList with all childs with tagname */
					int maxCount = getMaxCount(child);
					while (cpt < maxCount) {
						out += getTitle(child);
						cpt++;
					}
					break;
				case Structure.ATTRIBUTE:
					if (firstElement)
						firstElement = false;
					else
						out += separator;
					out += child.getName() + nextNumber(child);
					break;
				default:
					System.out
							.println("[PSI makers: flattener] ERROR: the node is neither an attribute nor an element");
				}
			}
		}
		return out;
	}

	public ArrayList selections = new ArrayList();

	public void addName(XsdNode node, String name) {
		associatedNames.put(node, name);
		node.setName(name);
		treeModel.reload(node);
	}

	public void addFilter(XsdNode node, String regexp) {
		elementFilters.remove(node);
		if (regexp != null && !regexp.trim().equals(""))
			elementFilters.put(node, regexp.trim());
	}

	
	public void selectNode(XsdNode node) {
		selections.add(node);
		node.use();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);

		setXmlRoot();
	}

	public void unselectNode(XsdNode node) {
		selections.remove(node);
		node.unuse();
		check((XsdNode) treeModel.getRoot());
		treeModel.reload(node);

		setXmlRoot();
	}

	public TreeMapping getMapping() {
		TreeMapping mapping = new TreeMapping();

		mapping.numerotation_type = this.numerotation_type;
		System.out.println("saving mapping");
		if (this.documentURL != null)
			mapping.setDocumentURL(Utils.relativizeURL(this.documentURL)
					.getPath());
		if (this.getSchemaURL() != null)
			mapping.setSchemaURL(Utils.relativizeURL(this.getSchemaURL())
					.getPath());

		if (this.lineNode != null)
			mapping.setLineNode(getPathForNode(this.lineNode));
		mapping.setSeparator(this.separator);

		mapping.setExpendChoices(this.expendChoices);

		ArrayList selections = new ArrayList();
		for (int i = 0; i < this.selections.size(); i++) {
			selections.add(getPathForNode((XsdNode) this.selections.get(i)));
		}
		mapping.setSelections(selections);

		HashMap associatedNames = new HashMap();
		Iterator it = this.associatedNames.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			//            String field = (String) this.associatedNames.get(node);
			associatedNames.put(getPathForNode(node), this.associatedNames
					.get(node));
		}
		mapping.setAssociatedNames(associatedNames);

		HashMap elementFilters = new HashMap();
		it = this.elementFilters.keySet().iterator();
		while (it.hasNext()) {
			XsdNode node = (XsdNode) it.next();
			//            String field = (String) this.elementFilters.get(node);
			elementFilters.put(getPathForNode(node), this.elementFilters
					.get(node));
		}
		mapping.setElementFilters(elementFilters);

		return mapping;
	}

	/**
	 * add to the flat file the content of a node
	 * 
	 * @param node
	 *            the node in the tree to parse
	 * @param element
	 *            the element in tghe XML document
	 * @param marshallingType
	 *            title or full parsing
	 * @param out
	 * @throws IOException
	 */
	public String marshallNode(XsdNode node, Node element) throws IOException {
		String marshalling = "";
		if (!node.isUsed) {
			return marshalling;
		}

		if (selections.contains(node)) {
			if (firstElement)
				firstElement = false;
			else
				marshalling += separator;

			
			
			Enumeration children = node.children();
			boolean filtered = false;
			while (children.hasMoreElements()) {
				XsdNode child = (XsdNode) children.nextElement();
			
				if (((Annotated) child.getUserObject()).getStructureType() == Structure.ATTRIBUTE) {
					if (elementFilters.containsKey(child)) {
						try {
							String value = ((Element) element)
									.getAttributeNode(child.toString())
									.getNodeValue();
							/** TODO: done for managing filter */
							if (!value.matches((String) elementFilters
									.get(child))) {
								filtered = true;
							}
						} catch (NullPointerException e) {
			
						}
					}
				}
			}
			
			
			if (element != null && filtered == false) {
				marshalling += getElementValue((Element) element);
			}
		}

		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			XsdNode child = (XsdNode) children.nextElement();
			if (child.isUsed) {
				switch (((Annotated) child.getUserObject()).getStructureType()) {
				case Structure.ELEMENT:
					int cpt = 0;
					/* create a NodeList with all childs with tagname */
					if (element != null) {
						NodeList allElements = element.getChildNodes();
						ArrayList elements = new ArrayList();
						for (int i = 0; i < allElements.getLength(); i++) {
							if (allElements.item(i).getNodeName().compareTo(
									child.toString()) == 0) {
								elements.add(allElements.item(i));
							}

							/* get references */
							else if (isXsRefPath(allElements.item(i)
									)) {
								Element ref = //document.
								this.getElementByKeyRef((Element) allElements
												.item(i));
								if (ref != null
										&& ref.getNodeName().compareTo(
												child.toString()) == 0) {
									elements.add(ref);
								}
							}
							
							/* get references */
							else if (isRefType(allElements.item(i)
									.getNodeName())) {
								Element ref = //document.
								getElementById(((Element) allElements
												.item(i))
												.getAttribute(refAttribute));
								if (ref != null
										&& ref.getNodeName().compareTo(
												child.toString()) == 0) {
									elements.add(ref);
								}
							}
						}
						while (cpt < elements.size()) {
							marshalling += marshallNode(child, (Node) elements
									.get(cpt));
							cpt++;
						}
					}
					int maxCount = getMaxCount(child);
					while (cpt < maxCount) {
						marshalling += marshallNode(child, null);
						cpt++;
					}
					break;
				case Structure.ATTRIBUTE:
					if (firstElement)
						firstElement = false;
					else
						marshalling += separator;

					if (element != null) {
						try {
							marshalling += ((Element) element)
									.getAttributeNode(child.toString())
									.getNodeValue();
						} catch (NullPointerException ne) {
							/* main cause: the cell does not exist */
						}
					}
					break;
				default:
					System.out
							.println("[PSI makers: flattener] ERROR: the node is neither an attribute nor an element");
				}
			}
		}
		return marshalling;
	}

	public void loadMapping(TreeMapping mapping) throws IOException,
			SAXException {
		if (mapping.documentURL != null)
			this.setDocumentURL(new File(mapping.documentURL).toURL());
		if (mapping.getSchemaURL() != null)
			this.setSchemaURL(new File(mapping.getSchemaURL()).toURL());

		File schema = new File(Utils.absolutizeURL(schemaURL).getPath());
		loadSchema(schema);

		this.setExpendChoices(mapping.expendChoices);

		int i = 0;
		while (i < expendChoices.size()) {
			String path = (String) expendChoices.get(i);
			i++;
//			int index = path.indexOf(".");
//			String subpath = path;
//			redoChoice(path, null);
			super.extendPath(super.getNodeByPath(path));
		}

		if (mapping.getLineNode() != null)
			this.setLineNode(getNodeByPath(mapping.getLineNode()));
		if (documentURL != null)
			this.loadDocument(documentURL);

		this.setSeparator(mapping.separator);

		for (i = 0; i < mapping.selections.size(); i++) {
			XsdNode node = getNodeByPath((String) mapping.selections.get(i));
			selectNode(node);
		}

		Iterator it = mapping.associatedNames.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			String field = (String) mapping.associatedNames.get(path);
			XsdNode node = getNodeByPath(path);
			addName(node, (String) mapping.associatedNames.get(path));
		}

		it = mapping.elementFilters.keySet().iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			String field = (String) mapping.elementFilters.get(path);
			XsdNode node = getNodeByPath(path);
			this.elementFilters.put(node, mapping.elementFilters.get(path));
		}

		this.numerotation_type = mapping.numerotation_type;

	}

	/**
	 * @return Returns the documentURI.
	 * 
	 * @uml.property name="documentURL"
	 */
	public URL getDocumentURL() {
		return documentURL;
	}

	/**
	 * @param documentURI
	 *            The documentURI to set.
	 * 
	 * @uml.property name="documentURL"
	 */
	public void setDocumentURL(URL documentURL) {
		this.documentURL = documentURL;
	}

	/**
	 * @param schemaURI
	 *            The schemaURI to set.
	 */
	public void setSchemaURI(URL schemaURL) {
		this.schemaURL = schemaURL;
	}

	/**
	 * @return Returns the lineNode.
	 */
	public XsdNode getLineNode() {
		return lineNode;
	}

	/**
	 * keep current values for referenced fields
	 * 
	 * @uml.property name="associatedValues"
	 */
	public HashMap associatedNames = new HashMap();

	/**
	 * @return Returns the curElementCount.
	 */
	public int getCurElementCount() {
		return curElementsCount;
	}

	private String nextNumber(XsdNode node) {
		String alphabetHigh = "_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String alphabetLow = "_abcdefghijklmnopqrstuvwxyz";
		int count = getMaxCount(node, lineNode) + 1;
		if (count > 1) {
			int num = node.nextNumber();
			num = (num) % count;
			if (numerotation_type == HIGH_ALPHABETIC_NUMEROTATION
					&& num < alphabetHigh.length())
				return "" + alphabetHigh.charAt(num);
			if (numerotation_type == LOW_ALPHABETIC_NUMEROTATION
					&& num < alphabetHigh.length())
				return "" + alphabetLow.charAt(num);

			if (numerotation_type == NUMERIC_NUMEROTATION)
				return "" + num;
			return "" + num;
		}
		return "";
	}

	/**
	 * set count for each node to 0. the count is used for the display of the
	 * title line, and this function will be used before updating the preview or
	 * before printing the file.
	 */
	public void resetCount() {
		resetCount(lineNode);
	}

	private void resetCount(XsdNode node) {
		node.cpt = 0;
		int nbChildren = node.getChildCount();
		for (int i = 0; i < nbChildren; i++) {
			resetCount((XsdNode) node.getChildAt(i));
		}
	}

	public int getMaxCount(XsdNode node, XsdNode parent) {
		if (node == parent) {
			return getMaxCount(node);
		}
		Enumeration e = node.pathFromAncestorEnumeration(parent);
		e.nextElement();
		XsdNode nextNode = (XsdNode) e.nextElement();
		return getMaxCount(parent) * getMaxCount(node, nextNode);
	}
	
	/**
	 * get Element refered by this id, according to the XML id specification
	 * @param id
	 * @return
	 */
	private Element getElementById(String id) {
		System.out.println("get id : "+id);
		Element ref = document.getElementById(id);
		return ref;
	}
	
	
	/**
	 * get element refered, according to the key/keyRef xs specification
	 * @param node
	 * @return
	 */
	private Element getElementByKeyRef(Node node) {
		Element ref ;
			
		String refType = getDocumentXpath(node);
		System.out.println("search ref for : "+refType);
		/* get ref attribute */
		String refId = node.getTextContent();
		if (refId == null || refId.equals("")){ 
			for (int i = 0; i< node.getAttributes().getLength(); i++) {
//				System.out.println(node.getAttributes().item(i).getNodeName()+" :: "+node.getAttributes().item(i).getTextContent());
				if (node.getAttributes().item(i).getNodeName().equals(refAttribute)){
						refId = node.getAttributes().item(i).getNodeValue();
				}
			}
		}
		
		String referedType = (String) refType2referedType.get(refType)	;	
		
		ref = (Element) xsKeyNodes.get(referedType+"#"+refId);		
		return ref;
	}
	
	
	private String getXpath(Node node) {
		String xpath = "";
		if (node.getParentNode() != null) {
			xpath = getXpath(node.getParentNode());
		}
		String name = getName(node);
		
		if (name != null && xpath != null && xpath.equals("") == false)
			xpath += "/";
		
		if (name != null)
			xpath += name;
		return xpath;
	}
	
	/**
	 * the name of an element in the schema is contained in the attribute 'name'
	 * @param node
	 * @return
	 */
	private String getName(Node node) {
		if (node.hasAttributes() == false) 
			return null;
		
		for (int i = 0; i<node.getAttributes().getLength(); i++) {
			if (node.getAttributes().item(i).getNodeName().equals("name")) 
				return node.getAttributes().item(i).getNodeValue();
		}
		return null;
	}

	private void getKeys(Node node) {
		try {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				if (node.getChildNodes().item(i).getNodeName()
						.indexOf("keyref") > 0) {
					keyRefs.add(node.getChildNodes().item(i));
				} else if (node.getChildNodes().item(i).getNodeName().indexOf(
						"key") > 0) {
					keyz.add(node.getChildNodes().item(i));
				}
				getKeys(node.getChildNodes().item(i));
			}
	
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	
}