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
package psidev.psi.mi.filemakers.xmlMaker;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;


import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import psidev.psi.mi.filemakers.xmlFlattener.XmlFlattener;
import psidev.psi.mi.filemakers.xmlMaker.gui.DictionaryPanel;
import psidev.psi.mi.filemakers.xmlMaker.gui.FlatFileTabbedPanel;
import psidev.psi.mi.filemakers.xmlMaker.gui.XsdTreePanelImpl;
import psidev.psi.mi.filemakers.xmlMaker.mapping.DictionaryMapping;
import psidev.psi.mi.filemakers.xmlMaker.mapping.FlatFileMapping;
import psidev.psi.mi.filemakers.xmlMaker.mapping.Mapping;
import psidev.psi.mi.filemakers.xmlMaker.mapping.TreeMapping;
import psidev.psi.mi.filemakers.xmlMaker.structure.Dictionary;
import psidev.psi.mi.filemakers.xmlMaker.structure.FlatFile;
import psidev.psi.mi.filemakers.xmlMaker.structure.XsdTreeStructImpl;
import psidev.psi.mi.filemakers.xsd.JTextPaneMessageManager;
import psidev.psi.mi.filemakers.xsd.Utils;
import psidev.psi.mi.filemakers.xsd.XsdNode;

/**
 * Main class for the PSI files maker: this class displays a graphical interface
 * that allows to load and display an XML schema as a tree, to load several flat
 * files and display them as a list of columns, to select on the tree to wich
 * nodes should be associated the flat files and to which nodes should be
 * associated the columns, to load dictionnaries and associate them to nodes for
 * which values form the flat file will be replaced by their definition, and to
 * print in an XML file. Some checkings can be made and some warnings are
 * displayed that allow to know if associations respect or not the schema.
 * 
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XmlMakerGui extends JFrame {
	
	static String mappingFileName = null;
	
	private static void displayUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -classpath dist/xmlMakerFlattener.jar -Djava.ext.dirs=libs "+XmlFlattener.class.getName()
								+" [-mapping mapping.xml] "
        		, options);
	}
	
	public void load() {
		JFileChooser fc;
		if (Utils.lastVisitedMappingDirectory != null) { 
			fc = new JFileChooser(Utils.lastVisitedMappingDirectory);
		} else fc = new JFileChooser(".");
		
		int returnVal = fc.showOpenDialog(new JFrame());
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		load(fc.getSelectedFile());
	}
	
	
	private void load(File mappingFile) {
		try {
			FileInputStream fin = new FileInputStream(mappingFile);
			
			Utils.lastVisitedDirectory = mappingFile.getPath();
			Utils.lastVisitedMappingDirectory = mappingFile.getPath();
			//Utils.lastMappingFile = mappingFile.getName();
			// Create XML encoder.
			XMLDecoder xdec = new XMLDecoder(fin);

			/* get mapping */
			Mapping mapping = (Mapping) xdec.readObject();

			/* flat files */
			flatFileTabbedPanel.flatFileContainer.flatFiles = new ArrayList();

			for (int i = 0; i < mapping.flatFiles.size(); i++) {
				FlatFileMapping ffm = (FlatFileMapping) mapping.flatFiles
						.get(i);
				FlatFile f = new FlatFile();
				if (ffm != null) {
					f.lineSeparator = ffm.lineSeparator;
					f.firstLineForTitles = ffm.fisrtLineForTitle;
					f.setSeparators(ffm.getSeparators());

					try {
						URL url = new File(ffm.getFileURL()).toURL();
						if (url != null)
							f.load(url);
					} catch (FileNotFoundException fe) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Unable to load file" + ffm.getFileURL(),
								"[PSI makers: PSI maker] load flat file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				treePanel.flatFileTabbedPanel.flatFileContainer.addFlatFile(f);
			}
			treePanel.flatFileTabbedPanel.reload();

			/* dictionaries */
			dictionnaryLists.dictionaries.dictionaries = new ArrayList();

			for (int i = 0; i < mapping.getDictionaries().size(); i++) {
				DictionaryMapping dm = (DictionaryMapping) mapping
						.getDictionaries().get(i);
				Dictionary d = new Dictionary();

				try {
					URL url = null;
					if (dm.getFileURL() != null)
						url = new File(dm.getFileURL()).toURL();
					if (url != null)
						d = new Dictionary(url, dm.getSeparator(),
								dm.caseSensitive);
					else
						d = new Dictionary();
				} catch (FileNotFoundException fe) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Unable to load file" + dm.getFileURL(),
							"[PSI makers: PSI maker] load dictionnary",
							JOptionPane.ERROR_MESSAGE);
					d = new Dictionary();
				}
				treePanel.dictionaryPanel.dictionaries.addDictionary(d);
			}
			treePanel.dictionaryPanel.reload();

			/* tree */
			TreeMapping treeMapping = mapping.getTree();

			File schema = new File(treeMapping.getSchemaURL());
			try {
				treePanel.loadSchema(schema);
				((XsdTreeStructImpl) treePanel.xsdTree)
						.loadMapping(treeMapping);

				treePanel.xsdTree.check();
				treePanel.reload();

				/* set titles for flat files */
				for (int i = 0; i < mapping.flatFiles.size(); i++) {
					try {
						flatFileTabbedPanel.tabbedPane.setTitleAt(i,
								((XsdNode) xsdTree.getAssociatedFlatFiles()
										.get(i)).toString());
					} catch (IndexOutOfBoundsException e) {
						/** TODO: manage exception */
					}
				}

			} catch (FileNotFoundException fe) {
				JOptionPane.showMessageDialog(new JFrame(), "File not found: "
						+ schema.getName(), "[PSI makers]",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Unable to load file" + ioe.toString(), "[PSI makers]",
						JOptionPane.ERROR_MESSAGE);
			}
			xdec.close();
			fin.close();
		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(new JFrame(),
					"Unable to load mapping" + mappingFile.getName(),
					"[PSI makers: PSI maker] load mapping",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(new JFrame(),
					"IO error, unable to load mapping",
					"[PSI makers: PSI maker] load mapping",
					JOptionPane.ERROR_MESSAGE);
		} catch (NoSuchElementException nsee) {
			nsee.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Unable to load file",
					"[PSI makers]", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void save() {
		try {
			JFileChooser fc;
			if (Utils.lastVisitedMappingDirectory != null) { 
				fc = new JFileChooser(Utils.lastVisitedMappingDirectory);
			} else fc = new JFileChooser(".");
		
			int returnVal = fc.showSaveDialog(new JFrame());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());

			// Create XML encoder.
			XMLEncoder xenc = new XMLEncoder(fos);

			Mapping mapping = new Mapping();
			mapping.setTree(((XsdTreeStructImpl) treePanel.xsdTree)
					.getMapping());

			/* dictionaries */
			for (int i = 0; i < treePanel.dictionaryPanel.dictionaries
					.getDictionaries().size(); i++) {
//				DictionaryMapping dm = ((Dictionary) xsdTree.dictionaries
//						.getDictionaries().get(i)).getMapping();
				mapping.dictionaries.add(((Dictionary) xsdTree.dictionaries
						.getDictionaries().get(i)).getMapping());
			}

			/* flat files */
			for (int i = 0; i < xsdTree.flatFiles.flatFiles.size(); i++) {
//				FlatFileMapping fm = (xsdTree.flatFiles.getFlatFile(i))
//						.getMapping();
				mapping.flatFiles.add((xsdTree.flatFiles.getFlatFile(i))
						.getMapping());
			}

			xenc.writeObject(mapping);

			xenc.close();
			fos.close();
		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(new JFrame(), "Unable to write file",
					"[PSI makers: PSI maker] save mapping",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			System.out.println("pb: " + ex);
			StackTraceElement[] s = ex.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i]);
			}
		}
	}

	/**
	 * 
	 * @uml.property name="treePanel"
	 * @uml.associationEnd
	 * @uml.property name="treePanel" multiplicity="(1 1)"
	 */
	public XsdTreePanelImpl treePanel;

	/**
	 * 
	 * @uml.property name="flatFileTabbedPanel"
	 * @uml.associationEnd
	 * @uml.property name="flatFileTabbedPanel" multiplicity="(1 1)"
	 */
	public FlatFileTabbedPanel flatFileTabbedPanel;

	/**
	 * 
	 * @uml.property name="dictionnaryLists"
	 * @uml.associationEnd
	 * @uml.property name="dictionnaryLists" multiplicity="(1 1)"
	 */
	public DictionaryPanel dictionnaryLists;

	/**
	 * 
	 * @uml.property name="xsdTree"
	 * @uml.associationEnd
	 * @uml.property name="xsdTree" multiplicity="(1 1)"
	 */
	public XsdTreeStructImpl xsdTree;

	public XmlMakerGui() {
		super("XML Maker");

		/* look n'feel */
		try {
//			UIManager.setLookAndFeel(new TonicLookAndFeel());
		} catch (Exception e) {
			System.out.println("Unable to load look'n feel");
		}

		getContentPane().setLayout(new BorderLayout());

		xsdTree = new XsdTreeStructImpl();
		JTextPaneMessageManager messageManager = new JTextPaneMessageManager();
		xsdTree.setMessageManager(messageManager);
		treePanel = new XsdTreePanelImpl(xsdTree, messageManager);
		

		
		
		flatFileTabbedPanel = new FlatFileTabbedPanel(xsdTree.flatFiles);
		flatFileTabbedPanel.setBorder(new TitledBorder("Flat files"));

		dictionnaryLists = new DictionaryPanel(xsdTree.dictionaries);
		dictionnaryLists.setBorder(new TitledBorder("Dictionnary"));

		Box associationsPanels = new Box(BoxLayout.Y_AXIS);

		associationsPanels.add(flatFileTabbedPanel);

		associationsPanels.add(dictionnaryLists);
		getContentPane().add(associationsPanels, BorderLayout.WEST);

		getContentPane().add(treePanel, BorderLayout.CENTER);

		treePanel.setTabFileTabbedPanel(flatFileTabbedPanel);
		treePanel.setDictionnaryPanel(dictionnaryLists);
		final CloseView fv = new CloseView();
		addWindowListener(fv);
		setJMenuBar(new XmlMakerMenu());
//		setSize(800, 600);
		this.pack();
		setVisible(true);
		
		if (mappingFileName != null) {
			load(new File(mappingFileName));
		}
		
	}

	/**
	 * close properly the frame
	 */
	public class CloseView extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			close();
		}

		public void windowClosing(WindowEvent e) {
			close();
		}

		public void close() {
			XmlMakerGui.this.setVisible(false);
			XmlMakerGui.this.dispose();
			System.exit(0);
		}
	}

	public class clearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}

	/**
	 * Save the mapping
	 * 
	 * @author arnaud
	 *  
	 */
	public class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			save();
		}
	}

	/**
	 * the menu bar
	 */
	public class XmlMakerMenu extends JMenuBar {
		public XmlMakerMenu() {
			JMenu file = new JMenu(new String("File"));
			JMenuItem save = new JMenuItem(new String("Save mapping"));
			JMenuItem load = new JMenuItem(new String("Load mapping"));
			JMenuItem clear = new JMenuItem(new String("New mapping"));
			JMenuItem exit = new JMenuItem(new String("Exit"));
			clear.addActionListener(new clearListener());
			load.addActionListener(new LoadListener());
			save.addActionListener(new SaveListener());
			exit.addActionListener(new CloseView());
			file.add(clear);
			file.add(load);
			file.add(save);
			file.add(exit);
			add(file);

			JMenu help = new JMenu(new String("Help"));
			JMenuItem documentation = new JMenuItem("Documentation");
			documentation.addActionListener(new DisplayDocumentationListener());
			JMenuItem about = new JMenuItem("About");
			about.addActionListener(new DisplayAboutListener());
			help.add(documentation);
			help.add(about);
			add(help);
		}

		public class DisplayDocumentationListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JEditorPane editorPane = new JEditorPane();
				editorPane.setEditable(false);
				try {
					editorPane.setContentType("text/html");
					editorPane.setPage("file:doc/documentation.html");

					JScrollPane areaScrollPane = new JScrollPane(editorPane);
					areaScrollPane
							.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					areaScrollPane.setPreferredSize(new Dimension(600, 650));
					JOptionPane.showMessageDialog(new JFrame(), areaScrollPane);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Documentation not found.", "Documentation",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		public class DisplayAboutListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JEditorPane editorPane = new JEditorPane();
				editorPane.setEditable(false);
				try {
					editorPane.setPage("file:doc/about.html");
					editorPane.setContentType("text/html");
					JScrollPane areaScrollPane = new JScrollPane(editorPane);
					areaScrollPane
							.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					areaScrollPane.setPreferredSize(new Dimension(600, 650));
					JOptionPane.showMessageDialog(new JFrame(), areaScrollPane);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(new JFrame(),
							"About not found.", "About...",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public static void main(String[] args) {
		// create Option objects
		Option helpOpt = new Option("help", "print this message.");

		Option mappingOpt = OptionBuilder.withArgName("mapping").hasArg()
				.withDescription(
						"the mapping file, created by the GUI application")
				.create("mapping");
		mappingOpt.setRequired(false);
		
		Options options = new Options();

		options.addOption(helpOpt);
		options.addOption(mappingOpt);
		
		// create the parser
		CommandLineParser parser = new BasicParser();
		CommandLine line = null;
		try {
			// parse the command line arguments
			line = parser.parse(options, args, true);
		} catch (ParseException exp) {
			// Oops, something went wrong

			displayUsage(options);

			System.err.println("Parsing failed.  Reason: "
					+ exp.getMessage());
			System.exit(1);
		}

		if (line.hasOption("help")) {
			displayUsage(options);
			System.exit(0);
		}
		
		// These argument are mandatory.
		mappingFileName = line.getOptionValue("mapping");
		System.out.println("mapping: " + mappingFileName);
		new XmlMakerGui();
	}

	public class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			load();
		}
	}

	public void clear() {
		flatFileTabbedPanel.flatFileContainer.flatFiles = new ArrayList();
		flatFileTabbedPanel.flatFileContainer.flatFiles.add(new FlatFile());
		treePanel.flatFileTabbedPanel.reload();

		/* dictionaries */
		dictionnaryLists.dictionaries.dictionaries = new ArrayList();
		treePanel.dictionaryPanel.reload();

		getContentPane().remove(treePanel);
		
		xsdTree = new XsdTreeStructImpl();
		JTextPaneMessageManager messageManager = new JTextPaneMessageManager();
		xsdTree.setMessageManager(messageManager);
		treePanel = new XsdTreePanelImpl(xsdTree, messageManager);

		getContentPane().add(treePanel, BorderLayout.CENTER);

		treePanel.setTabFileTabbedPanel(flatFileTabbedPanel);
		treePanel.setDictionnaryPanel(dictionnaryLists);

		treePanel.xsdTree.treeModel.reload();
		treePanel.xsdTree.emptySelectionLists();
		treePanel.reload();
	}

}