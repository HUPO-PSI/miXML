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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;


import org.xml.sax.SAXException;

import psidev.psi.mi.filemakers.xmlFlattener.gui.XsdTreePanelImpl;
import psidev.psi.mi.filemakers.xmlFlattener.mapping.TreeMapping;
import psidev.psi.mi.filemakers.xmlFlattener.structure.XsdTreeStructImpl;
import psidev.psi.mi.filemakers.xsd.JTextPaneMessageManager;
import psidev.psi.mi.filemakers.xsd.MessageManagerInt;
import psidev.psi.mi.filemakers.xsd.Utils;
//import com.digitprop.tonic.TonicLookAndFeel;

/**
 * Main class for the flattener: this class displays a graphical interface that
 * allows to load and display an XML schema as a tree, to load a XML document,
 * to select on the tree the nodes to be which value has to be printed in a flat
 * file, and to print in a flat file all data from the document corresponding to
 * selected nodes:
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XmlFlattenerGui extends JFrame {

	/**
	 * 
	 * @uml.property name="treePanel"
	 * @uml.associationEnd
	 * @uml.property name="treePanel" multiplicity="(1 1)"
	 */
	public XsdTreePanelImpl treePanel;

	public XmlFlattenerGui() {
		super("XML flattener");

		/* look n'feel */
		try {
//			UIManager.setLookAndFeel(new TonicLookAndFeel());
		} catch (Exception e) {
			System.out.println("Unable to load look'n feel");
		}

		XsdTreeStructImpl xsdTree = new XsdTreeStructImpl();
		getContentPane().setLayout(new BorderLayout());

		JTextPaneMessageManager messageManager = new JTextPaneMessageManager();
		xsdTree.setMessageManager(messageManager);
		
		treePanel = new XsdTreePanelImpl(xsdTree, messageManager);
		
		treePanel.setBorder(new TitledBorder("Schema"));
	
		
		getContentPane().add(treePanel, BorderLayout.CENTER);

		final CloseView fv = new CloseView();
		addWindowListener(fv);
		setJMenuBar(new XmlFlattenerMenu());
		setSize(800, 600);
		setVisible(true);
	}

	/**
	 * the menu bar
	 */
	public class PSIMakerMenu extends JMenuBar {
		public PSIMakerMenu() {
			JMenu file = new JMenu(new String("File"));
			JMenuItem exit = new JMenuItem(new String("Exit"));
			exit.addActionListener(new CloseView());
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
			XmlFlattenerGui.this.setVisible(false);
			XmlFlattenerGui.this.dispose();
			System.exit(0);
		}
	}

	public class DisplayDocumentationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			try {
				editorPane.setPage("file:doc/documentation.html");
				editorPane.setContentType("text/html");
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
				JOptionPane.showMessageDialog(new JFrame(), "About not found.",
						"About...", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void main(String[] args) {
		XmlFlattenerGui f = new XmlFlattenerGui();
	}

	/**
	 * the menu bar
	 */
	public class XmlFlattenerMenu extends JMenuBar {
		public XmlFlattenerMenu() {
			JMenu file = new JMenu(new String("File"));
			JMenuItem clear = new JMenuItem(new String("Clear mapping"));
			JMenuItem save = new JMenuItem(new String("Save mapping"));
			JMenuItem load = new JMenuItem(new String("Load mapping"));
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

	public void clear() {
		treePanel.xsdTree.emptySelectionLists();
		treePanel.reload();
		treePanel.updatePreview();
	}

	public void load() {
		try {
			String directory = Utils.lastVisitedMappingDirectory;
			if (directory == null)
				directory = Utils.lastVisitedDirectory;

			JFileChooser fc = new JFileChooser(directory);

			int returnVal = fc.showOpenDialog(new JFrame());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			Utils.lastVisitedDirectory = fc.getSelectedFile().getPath();
			Utils.lastVisitedMappingDirectory = fc.getSelectedFile().getPath();

			FileInputStream fin = new FileInputStream(fc.getSelectedFile());

			// Create XML encoder.
			XMLDecoder xdec = new XMLDecoder(fin);

			/* get mapping */
			TreeMapping treeMapping = (TreeMapping) xdec.readObject();

			/* tree */

			((XsdTreeStructImpl) treePanel.xsdTree).loadMapping(treeMapping);
			treePanel.updatePreview();

			treePanel.setTreeSelectionListener();
			treePanel.setCellRenderer();
			treePanel.xsdTree.check();
			treePanel.reload();
			xdec.close();
			fin.close();
		} catch (FileNotFoundException fe) {
			treePanel.xsdTree.getMessageManager().sendMessage("unable to load mapping file (file not found)", MessageManagerInt.errorMessage);
			System.out.println("unable to load mapping file");
			StackTraceElement[] s = fe.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i]);
			}
		} catch (IOException ex) {
			treePanel.xsdTree.getMessageManager().sendMessage("unable to load mapping file (unable to read the file, IO exception)", MessageManagerInt.errorMessage);
			System.out.println("unable to load mapping file");
			StackTraceElement[] s = ex.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i]);
			}
		} catch (SAXException ex) {
			treePanel.xsdTree.getMessageManager().sendMessage("unable to load mapping file (problem for parsing the XML file)", MessageManagerInt.errorMessage);
			System.out.println("xml pb: " + ex);
			StackTraceElement[] s = ex.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i]);
			}
		} catch (NoSuchElementException ex) {
			treePanel.xsdTree.getMessageManager().sendMessage("unable to load mapping file (an element is missing in the mapping file, maybe this file is too old and not compatible anymore)", MessageManagerInt.errorMessage);
			System.out.println("xml pb: " + ex);
			StackTraceElement[] s = ex.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i]);
			}
		} catch (ClassCastException ex) {
			treePanel.xsdTree.getMessageManager().sendMessage("unable to load mapping file (it doesn't seem to be a mapping file)", MessageManagerInt.errorMessage);
			System.out.println("xml pb: " + ex);
			StackTraceElement[] s = ex.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				System.out.println(s[i]);
			}
		}
	}

	public void save() {
		try {
			JFileChooser fc = new JFileChooser(".");

			int returnVal = fc.showSaveDialog(new JFrame());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());

			// Create XML encoder.
			XMLEncoder xenc = new XMLEncoder(fos);

			xenc.writeObject(((XsdTreeStructImpl) treePanel.xsdTree)
					.getMapping());
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

	public class clearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}

	public class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			load();
		}
	}

	public class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			save();
		}
	}

}