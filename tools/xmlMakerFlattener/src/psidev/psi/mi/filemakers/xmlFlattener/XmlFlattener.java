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
package psidev.psi.mi.filemakers.xmlFlattener;


import java.beans.XMLDecoder;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;

import psidev.psi.mi.filemakers.xmlFlattener.mapping.TreeMapping;
import psidev.psi.mi.filemakers.xmlFlattener.structure.XsdTreeStructImpl;
import psidev.psi.mi.filemakers.xsd.SimpleMessageManager;


/**
 * Executable class for the flattener, without graphical user interface
 * 
 * 
 * Main class for the flattener: load an XML schema as a tree, load a XML
 * document, get selected nodes from the mapping file and write a flat file.
 * 
 * Available parameters: -mapping: the mapping file, created by the GUI
 * application -xmlDocument: the XML document location, either a local file or
 * an URL -o: name of the flat file to write
 * 
 * example: java -Xms500M -Xmx500M -classpath
 * classes/:libs/xercesImpl.jar:libs/castor-0.9.5-xml.jar:libs/xml-apis.jar:libs/xmlParserAPIs.jar
 * mint.filemakers.xmlFlattener.XmlFlattenerGui -mapping mapping.xml
 * -xmlDocumant data/MIF.xml -o interactors.txt
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XmlFlattener {

	public XmlFlattener() {
		xsdTree = new XsdTreeStructImpl();		
		xsdTree.setMessageManager(new SimpleMessageManager());
	}

	public static void main(String[] args) throws Exception {
		try {
			System.setProperty("java.awt.headless", "true");
			XmlFlattener f = new XmlFlattener();
			String mappingFileName = "";
			String schema = null;
			String flatFile = null;
			String xmlDocument = null;
			String logFile = "log.out";

			int i = 0;
			while (i < args.length) {
				if (args[i].compareTo("-mapping") == 0) {
					mappingFileName = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-xmlDocument") == 0) {
					xmlDocument = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-schema") == 0) {
					schema = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-o") == 0) {
					flatFile = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-log") == 0) {
					logFile = args[i + 1];
					i = i + 2;
				} else
					i = i + 1;
			}

			System.out.println("mapping = " + mappingFileName + ", output = "
					+ flatFile);

			if (mappingFileName == null || mappingFileName.length() == 0) {
				System.out
						.println("usage: java -classpath classes/ -Djava.ext.dirs=libs "+XmlFlattener.class.getName()
								+" -mapping mapping.xml  [-xmlDocument document.xml] [-o outputfile]");
				System.out.println("Available parameters:");
				System.out
						.println("-mapping: the mapping file, created by the GUI application");
				System.out
						.println("-xmlDocument: the XML document location, either a local file or an URL");
				System.out.println("-o: name of the flat file to write");
				return;
			}

			FileInputStream fin = new FileInputStream(mappingFileName);
			// Create XML decoder.
			XMLDecoder xdec = new XMLDecoder(fin);
			TreeMapping treeMapping = (TreeMapping) xdec.readObject();

			if (xmlDocument != null) {
				xmlDocument = xmlDocument.replaceAll("'", "");
				treeMapping.setDocumentURL(xmlDocument);
				System.out.println("xmlDocument: " + xmlDocument);
			}

			if (schema != null) {
				treeMapping.setSchemaURL(schema.replaceAll("'", ""));
			}
			f.xsdTree.loadMapping(treeMapping);

			System.out.println("Xml Parsing messages:");
			Iterator it = f.xsdTree.xmlErrorHandler.errors.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}

			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(flatFile)));

			f.xsdTree.write(writer);
			writer.flush();
			writer.close();
			System.out.println("Flat file successfully created");
		} catch (Exception e) {
			System.out
					.println("usage: java -classpath classes/ -Djava.ext.dirs=libs mint.filemakers.xmlFlattener.XmlMakermint.filemakers.xmlFlattener.XmlFlattenerGui -mapping mapping.xml  [-xmlDocumant document.xml] [-o outputfile]");
			System.out.println("Available parameters:");
			System.out
					.println("-mapping: the mapping file, created by the GUI application");
			System.out
					.println("-xmlDocument: the XML document location, either a local file or an URL");
			System.out.println("-o: name of the flat file to write");
			throw (e);
		}
	}

	/**
	 * @uml.property name="xsdTree"
	 * @uml.associationEnd
	 * @uml.property name="xsdTree" multiplicity="(1 1)"
	 */
	public XsdTreeStructImpl xsdTree;

}