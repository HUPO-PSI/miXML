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
package mint.filemakers.xmlMaker;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import mint.filemakers.xmlMaker.mapping.DictionaryMapping;
import mint.filemakers.xmlMaker.mapping.FlatFileMapping;
import mint.filemakers.xmlMaker.mapping.Mapping;
import mint.filemakers.xmlMaker.mapping.TreeMapping;
import mint.filemakers.xmlMaker.structure.Dictionary;
import mint.filemakers.xmlMaker.structure.FlatFile;
import mint.filemakers.xmlMaker.structure.XsdTreeStructImpl;
import mint.filemakers.xsd.FileMakersException;
import mint.filemakers.xsd.Utils;

/**
 * 
 * Executable class for the maker, without graphical user interface
 * 
 * Main class for the maker: load an XML schema as a tree, load the flat files,
 * the dictionaries get selected nodes from the mapping file and write an XML
 * document.
 * 
 * Available parameters: -mapping: the mapping file, created by the GUI
 * application -o: name of the XML document to write -log: name of the log file
 * -flatfiles: names of the flat files in the right order, separated by comma
 * -dictionaries: names of the dictionary files in the right order, separated by
 * comma example: java -Xms500M -Xmx500M -classpath
 * classes/:libs/xercesImpl.jar:libs/castor-0.9.5-xml.jar:libs/xml-apis.jar:libs/xmlParserAPIs.jar
 * mint.filemakers.xmlFlattener.XmlMaker -mapping mapping.xml -flatfiles
 * data/interactions.txt,data/interactors.txt,data/experiments.txt -dictionaries
 * data/organisms.txt -o db-psi.xml -log db-psi.log
 * 
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XmlMaker {
	public void load(Mapping mapping) throws MalformedURLException,
			FileMakersException {
		xsdTree.flatFiles.flatFiles = new ArrayList();

		/* flat files */
		for (int i = 0; i < mapping.flatFiles.size(); i++) {
			FlatFileMapping ffm = (FlatFileMapping) mapping.flatFiles.get(i);
			FlatFile f = new FlatFile();
			f.lineSeparator = ffm.lineSeparator;
			f.firstLineForTitles = ffm.fisrtLineForTitle;
			f.setSeparators(ffm.getSeparators());
			try {
				URL url = Utils.absolutizeURL(ffm.getFileURL());
				f.load(url);
			} catch (IOException ioe) {
				System.out.println("ERROR: unable to load flat file "
						+ f.fileURL.getFile());
				throw new FileMakersException("unable to load flat file "
						+ ffm.getFileURL());
			}
			xsdTree.flatFiles.addFlatFile(f);
		}

		/* dictionaries */
		xsdTree.dictionaries.dictionaries = new ArrayList();

		for (int i = 0; i < mapping.dictionaries.size(); i++) {
			DictionaryMapping dm = (DictionaryMapping) mapping.dictionaries
					.get(i);
			try {
				URL url = Utils.absolutizeURL(dm.getFileURL());
				Dictionary d1 = new Dictionary(url, dm.getSeparator(),
						dm.caseSensitive);
				xsdTree.dictionaries.dictionaries.add(d1);
			} catch (IOException ioe) {
				System.out.println("ERROR: unable to load dictionary file "
						+ dm.getFileURL());

				throw new FileMakersException("unable to load dictionary file "
						+ dm.getFileURL());
			}
		}

		/* tree */
		TreeMapping treeMapping = mapping.getTree();

		File schema = new File(treeMapping.getSchemaURL());
		try {
			xsdTree.loadSchema(schema);
		} catch (IOException ioe) {
			System.out.println("ERROR: unable to load schema "
					+ treeMapping.getSchemaURL());
			throw new FileMakersException("unable to load schema "
					+ treeMapping.getSchemaURL());
		}
		((XsdTreeStructImpl) xsdTree).loadMapping(treeMapping);
		xsdTree.check();
	}

	/**
	 * 
	 * @uml.property name="xsdTree"
	 * @uml.associationEnd
	 * @uml.property name="xsdTree" multiplicity="(1 1)"
	 */
	public XsdTreeStructImpl xsdTree;

	public XmlMaker() {
		xsdTree = new XsdTreeStructImpl();
	}

	public static void main(String[] args) throws Exception {
		try {
			System.setProperty("java.awt.headless", "true");
			XmlMaker f = new XmlMaker();
			String mappingFileName = "";
			String flatFiles = null;
			String dictionaries = null;
			String schema = null;
			String xmlFile = null;
			String logFile = "log.out";

			int i = 0;
			while (i < args.length) {
				if (args[i].compareTo("-mapping") == 0) {
					mappingFileName = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-flatfiles") == 0) {
					flatFiles = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-dictionaries") == 0) {
					dictionaries = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-schema") == 0) {
					schema = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-o") == 0) {
					xmlFile = args[i + 1];
					i = i + 2;
				} else if (args[i].compareTo("-log") == 0) {
					logFile = args[i + 1];
					i = i + 2;
				} else
					i = i + 1;
			}

			System.out.println("mapping = " + mappingFileName + ", output = "
					+ xmlFile);

			if (mappingFileName == null || mappingFileName.length() == 0) {
				System.out
						.println("usage: java -classpath classes/ -Djava.ext.dirs=libs mint.filemakers.xmlFlattener.XmlMaker -mapping mapping.xml [-flatfiles flatfile1[,flatfile2]] [-dictionaries [dictionary1[,dictionary2]] [-o xmlDocument] [-log logfile]");
				System.out.println("Available parameters:");
				System.out
						.println("-mapping: the mapping file, created by the GUI application");
				System.out
						.println("-o: name of the XML document to write -log: name of the log file");
				System.out
						.println("-flatfiles: names of the flat files in the right order, separated by comma");
				System.out
						.println("-dictionaries: names of the dictionary files in the right order, separated by comma");
				return;
			}

			FileInputStream fin = new FileInputStream(mappingFileName);
			// Create XML decoder.
			XMLDecoder xdec = new XMLDecoder(fin);
			Mapping mapping = (Mapping) xdec.readObject();

			if (flatFiles != null) {
				String[] files = flatFiles.replaceAll("'", "").split(",");
				for (int j = 0; j < files.length; j++) {
					((FlatFileMapping) mapping.getFlatFiles().get(j)).fileURL = files[j];
					System.out.println("flat file " + j + ": " + files[j]);
				}
			}

			if (dictionaries != null) {
				String[] files = dictionaries.replaceAll("'", "").split(",");
				for (int j = 0; j < files.length; j++) {
					((DictionaryMapping) mapping.getDictionaries().get(j))
							.setFileURL(files[j]);
					System.out.println("dictionary " + j + ": " + files[j]);
				}
			}

			if (schema != null) {
				mapping.getTree().setSchemaURL(schema.replaceAll("'", ""));
			}
			try {
				f.load(mapping);
			} catch (FileMakersException fme) {
				System.out
						.println("exit from program: unable to load the mapping");
				return;
			}
			f.xsdTree.print(new File(xmlFile), new File(logFile));

			System.out.println("done");
			return;
		} catch (Exception e) {
			System.out
					.println("usage: java -classpath classes/ -Djava.ext.dirs=libs mint.filemakers.xmlFlattener.XmlMaker -mapping mapping.xml [-flatfiles flatfile1[,flatfile2]] [-dictionaries [dictionary1[,dictionary2]] [-o xmlDocument] [-log logfile]");
			System.out.println("Available parameters:");
			System.out
					.println("-mapping: the mapping file, created by the GUI application");
			System.out
					.println("-o: name of the XML document to write -log: name of the log file");
			System.out
					.println("-flatfiles: names of the flat files in the right order, separated by comma");
			System.out
					.println("-dictionaries: names of the dictionary files in the right order, separated by comma");
			throw (e);
		}
	}

}