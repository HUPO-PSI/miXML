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
package mint.filemakers.xmlMaker.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.XMLDecoder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import mint.filemakers.xmlMaker.structure.XsdTreeStructImpl;
import mint.filemakers.xsd.Utils;
import mint.filemakers.xsd.XsdNode;

import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.Annotation;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.Documentation;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Structure;

/**
 * 
 * This class overides the abstract class AbstractXslTree to provide a tree
 * representation of a XML schema, with management of marshalling of several
 * flat files to a xml file that respects the schema
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XsdTreePanelImpl extends mint.filemakers.xsd.AbstractXsdTreePanel {

	public void displayMessage(String errorMessage) {
		super.displayMessage(errorMessage, "[XML maker]");
	}

	public class GenericAssociationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			doAssociation();
		}
	}

	public void doAssociation() {
		XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
				.getLastSelectedPathComponent();

		if (selectedNode == null) {
			displayMessage("No node selected", "[XML maker]");
			return;
		}

		if (flatFileAssociation.isSelected()) {
			associateFlatFile(selectedNode, flatFileTabbedPanel
					.getSelectedIndex());
			return;
		}

		if (duplicableFieldAssociation.isSelected()) {
			String path = flatFileTabbedPanel.getSelectedPath();
			((XsdTreeStructImpl) xsdTree).associateDuplicableField(
					selectedNode, path);
			return;
		}

		if (!((XsdTreeStructImpl) xsdTree)
				.canHaveValue((XsdNode) ((XsdTreeStructImpl) xsdTree).tree
						.getLastSelectedPathComponent())) {
			displayMessage("No value can be associated to this node",
					"[XML maker]");
			return;
		}

		if (fieldAssociation.isSelected()) {
			//                String path = flatFileTabbedPanel.getSelectedPath();
			//                ((XsdTreeStructImpl) xsdTree)
			//                        .associateField(selectedNode, path);
			associateField(selectedNode);
		} else if (dictionnaryAssociation.isSelected())
			associateDictionnary(selectedNode);
		else if (defaultAssociation.isSelected())
			associateDefaultValue(selectedNode);
		else if (autoGenerationAssociationButton.isSelected())
			((XsdTreeStructImpl) xsdTree)
					.associateAutoGenerateValue(selectedNode);
	}

	/** use as default value when the user is asked for a default value */
	private String lastValueEntered = "";

	/**
	 * 
	 * @uml.property name="flatFileTabbedPanel"
	 */
	public FlatFileTabbedPanel flatFileTabbedPanel;

	/**
	 * panel for dictionnaries
	 * 
	 * @uml.property name="dictionaryPanel"
	 */
	public DictionaryPanel dictionaryPanel;

	/**
	 * @return Returns the associationButtons.
	 *  
	 */
	public ButtonGroup getAssociationButtons() {
		return associationButtons;
	}

	/**
	 * @param associationButtons
	 *            The associationButtons to set.
	 *  
	 */
	public void setAssociationButtons(ButtonGroup associationButtons) {
		this.associationButtons = associationButtons;
	}

	/**
	 * @return Returns the autoGenerationAssociationButton.
	 * 
	 * @uml.property name="autoGenerationAssociationButton"
	 */
	public JRadioButton getAutoGenerationAssociationButton() {
		return autoGenerationAssociationButton;
	}

	/**
	 * @param autoGenerationAssociationButton
	 *            The autoGenerationAssociationButton to set.
	 * 
	 * @uml.property name="autoGenerationAssociationButton"
	 */
	public void setAutoGenerationAssociationButton(
			JRadioButton autoGenerationAssociationButton) {
		this.autoGenerationAssociationButton = autoGenerationAssociationButton;
	}

	/**
	 * @return Returns the defaultAssociation.
	 * 
	 * @uml.property name="defaultAssociation"
	 */
	public JRadioButton getDefaultAssociation() {
		return defaultAssociation;
	}

	/**
	 * @param defaultAssociation
	 *            The defaultAssociation to set.
	 * 
	 * @uml.property name="defaultAssociation"
	 */
	public void setDefaultAssociation(JRadioButton defaultAssociation) {
		this.defaultAssociation = defaultAssociation;
	}

	/**
	 * @return Returns the dictionaryPanel.
	 * 
	 * @uml.property name="dictionaryPanel"
	 */
	public DictionaryPanel getDictionaryPanel() {
		return dictionaryPanel;
	}

	/**
	 * @param dictionaryPanel
	 *            The dictionaryPanel to set.
	 * 
	 * @uml.property name="dictionaryPanel"
	 */
	public void setDictionaryPanel(DictionaryPanel dictionaryPanel) {
		this.dictionaryPanel = dictionaryPanel;
	}

	/**
	 * @return Returns the dictionnaryAssociation.
	 * 
	 * @uml.property name="dictionnaryAssociation"
	 */
	public JRadioButton getDictionnaryAssociation() {
		return dictionnaryAssociation;
	}

	/**
	 * @param dictionnaryAssociation
	 *            The dictionnaryAssociation to set.
	 * 
	 * @uml.property name="dictionnaryAssociation"
	 */
	public void setDictionnaryAssociation(JRadioButton dictionnaryAssociation) {
		this.dictionnaryAssociation = dictionnaryAssociation;
	}

	/**
	 * @return Returns the fieldAssociation.
	 * 
	 * @uml.property name="fieldAssociation"
	 */
	public JRadioButton getFieldAssociation() {
		return fieldAssociation;
	}

	/**
	 * @param fieldAssociation
	 *            The fieldAssociation to set.
	 * 
	 * @uml.property name="fieldAssociation"
	 */
	public void setFieldAssociation(JRadioButton fieldAssociation) {
		this.fieldAssociation = fieldAssociation;
	}

	/**
	 * @return Returns the flatFileAssociation.
	 * 
	 * @uml.property name="flatFileAssociation"
	 */
	public JRadioButton getFlatFileAssociation() {
		return flatFileAssociation;
	}

	/**
	 * @param flatFileAssociation
	 *            The flatFileAssociation to set.
	 * 
	 * @uml.property name="flatFileAssociation"
	 */
	public void setFlatFileAssociation(JRadioButton flatFileAssociation) {
		this.flatFileAssociation = flatFileAssociation;
	}

	/**
	 * @return Returns the flatFileTabbedPanel.
	 * 
	 * @uml.property name="flatFileTabbedPanel"
	 */
	public FlatFileTabbedPanel getFlatFileTabbedPanel() {
		return flatFileTabbedPanel;
	}

	/**
	 * @param flatFileTabbedPanel
	 *            The flatFileTabbedPanel to set.
	 * 
	 * @uml.property name="flatFileTabbedPanel"
	 */
	public void setFlatFileTabbedPanel(FlatFileTabbedPanel flatFileTabbedPanel) {
		this.flatFileTabbedPanel = flatFileTabbedPanel;
	}

	/**
	 * create a new instance of XslTree The nodes will be automaticaly
	 * duplicated if the schema specify that more than one element of this type
	 * are mandatory
	 */
	public XsdTreePanelImpl(XsdTreeStructImpl xsdTree) {
		super(xsdTree, BorderLayout.SOUTH);
		//		try {
		//			UIManager.setLookAndFeel(
		// "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		//		}
		//		catch (Exception e) {System.out.println("beurk");}
		MouseListener mouseListener = new TreeMouseAdapter();
		xsdTree.tree.addMouseListener(mouseListener);
	}

	/**
	 * 
	 * @uml.property name="associationButtons"
	 * @uml.associationEnd
	 * @uml.property name="associationButtons" multiplicity="(0 1)"
	 */
	public ButtonGroup associationButtons;

	public JLabel validationRegexpLbl; // = new JTextArea("regexp");

	public JCheckBox unduplicableCB;

	/**
	 * 
	 * @uml.property name="fieldAssociation"
	 * @uml.associationEnd
	 * @uml.property name="fieldAssociation" multiplicity="(0 1)"
	 */
	public JRadioButton fieldAssociation;

	public JRadioButton duplicableFieldAssociation;

	/**
	 * 
	 * @uml.property name="dictionnaryAssociation"
	 * @uml.associationEnd
	 * @uml.property name="dictionnaryAssociation" multiplicity="(0 1)"
	 */
	public JRadioButton dictionnaryAssociation;

	/**
	 * 
	 * @uml.property name="defaultAssociation"
	 * @uml.associationEnd
	 * @uml.property name="defaultAssociation" multiplicity="(0 1)"
	 */
	public JRadioButton defaultAssociation;

	/**
	 * 
	 * @uml.property name="autoGenerationAssociationButton"
	 * @uml.associationEnd
	 * @uml.property name="autoGenerationAssociationButton" multiplicity="(0 1)"
	 */
	public JRadioButton autoGenerationAssociationButton;

	/**
	 * 
	 * @uml.property name="flatFileAssociation"
	 * @uml.associationEnd
	 * @uml.property name="flatFileAssociation" multiplicity="(0 1)"
	 */
	public JRadioButton flatFileAssociation;

	File logoutFile;

	/**
	 * create a button panel that includes buttons for loading the schema, to
	 * associate a node to a flat file, a cell a default value or to specify
	 * that a value should be automaticaly generated, to get informations about
	 * the node, print the XML file or just have a preview of it.
	 */
	public Box getButtonPanel() {
		associationLabel.setEditable(false);

		Box buttonsPanel = new Box(BoxLayout.X_AXIS);

		Box treeBox = new Box(BoxLayout.Y_AXIS);
		treeBox.setBorder(new TitledBorder("Schema"));

		Box mappingBox = new Box(BoxLayout.Y_AXIS);
		mappingBox.setBorder(new TitledBorder("Mapping"));

		Box nodeBox = new Box(BoxLayout.Y_AXIS);
		nodeBox.setBorder(new TitledBorder("Node"));

		Box associationBox = new Box(BoxLayout.X_AXIS);
		associationBox.setBorder(new TitledBorder("Associations"));

		Box outputBox = new Box(BoxLayout.X_AXIS);
		outputBox.setBorder(new TitledBorder("Output"));

		/* add a button for loading a XML Schema */
		JButton loadFileb = new JButton("Open");
		loadFileb.setMaximumSize(buttonsDimension);
		loadFileb.addActionListener(new LoadSchemaListener());

		JButton setIdb = new JButton("Prefix");
		setIdb.setMaximumSize(buttonsDimension);
		setIdb.addActionListener(new SetIdListener());

		/* add a button for duplicate a node (in case of lists) */
		JButton duplicateb = new JButton("Duplicate");
		duplicateb.setMaximumSize(buttonsDimension);
		duplicateb.addActionListener(new DuplicateListener());

		/* add a button for restauring original choice */
		JButton choiceb = new JButton("Restaure");
		choiceb.setMaximumSize(buttonsDimension);
		choiceb.addActionListener(new OriginalNodeListener());

		JButton infosb = new JButton("About");
		infosb.setMaximumSize(buttonsDimension);
		infosb.addActionListener(new InfosListener());

		JButton checkb = new JButton("Check");
		checkb.setMaximumSize(buttonsDimension);
		checkb.addActionListener(new CheckListener());

		JButton previewb = new JButton("Preview");
		previewb.setMaximumSize(buttonsDimension);
		previewb.addActionListener(new PreviewListener());

		JButton printb = new JButton("Make XML");
		printb.setMaximumSize(buttonsDimension);
		printb.addActionListener(new PrintListener());

		treeBox.add(loadFileb);
		treeBox.add(setIdb);
		treeBox.add(checkb);

		nodeBox.add(duplicateb);
		nodeBox.add(choiceb);
		nodeBox.add(infosb);

		outputBox.add(previewb);
		outputBox.add(printb);

		associationButtons = new ButtonGroup();

		fieldAssociation = new JRadioButton("to field");
		duplicableFieldAssociation = new JRadioButton("to duplicable field");
		dictionnaryAssociation = new JRadioButton("to dictionnary");
		defaultAssociation = new JRadioButton("to default value");
		autoGenerationAssociationButton = new JRadioButton("to automatic value");
		flatFileAssociation = new JRadioButton("to flat file");

		associationButtons.add(flatFileAssociation);
		associationButtons.add(duplicableFieldAssociation);
		associationButtons.add(fieldAssociation);
		associationButtons.add(dictionnaryAssociation);
		associationButtons.add(defaultAssociation);
		associationButtons.add(autoGenerationAssociationButton);
		associationButtons.setSelected(flatFileAssociation.getModel(), true);

		//        JButton validationRegexpsb = new JButton("validate on regular
		// expression:");
		//        validationRegexpsb.setMaximumSize(buttonsDimension);
		//        validationRegexpsb.addActionListener(new
		// validationRegexpsListener());

		JButton genericAssociationb = new JButton("Associate");
		genericAssociationb.setMaximumSize(buttonsDimension);
		genericAssociationb.addActionListener(new GenericAssociationListener());

		JButton genericCancelAssociationb = new JButton("Cancel");
		genericCancelAssociationb.setMaximumSize(buttonsDimension);
		genericCancelAssociationb
				.addActionListener(new GenericCancelAssociationListener());

		Box associationb1Box = new Box(BoxLayout.Y_AXIS);
		Box associationb2Box = new Box(BoxLayout.Y_AXIS);
		Box associationb3Box = new Box(BoxLayout.X_AXIS);
		Box associationb4Box = new Box(BoxLayout.X_AXIS);

		associationb1Box.add(flatFileAssociation);

		associationb1Box.add(duplicableFieldAssociation);

		associationb1Box.add(fieldAssociation);
		JButton editFieldb = new JButton("validation");
		editFieldb.addActionListener(new EditFieldAssociationListener());
		associationb1Box.add(editFieldb);
		associationb1Box.add(dictionnaryAssociation);
		associationb1Box.add(defaultAssociation);
		associationb1Box.add(defaultAssociation);
		associationb1Box.add(autoGenerationAssociationButton);

		JScrollPane scrollPane = new JScrollPane(associationLabel);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		associationb2Box.add(scrollPane);
		associationb3Box.add(genericAssociationb);
		associationb3Box.add(genericCancelAssociationb);

		associationb2Box.add(associationb4Box);
		associationb2Box.add(associationb3Box);

		associationBox.add(associationb2Box);
		associationBox.add(associationb1Box);

		Box lineBox2 = new Box(BoxLayout.X_AXIS);
		Box lineBox3 = new Box(BoxLayout.Y_AXIS);

		lineBox2.add(treeBox);
		lineBox2.add(nodeBox);
		lineBox3.add(lineBox2);
		lineBox3.add(outputBox);
		buttonsPanel.add(associationBox);
		buttonsPanel.add(lineBox3);

		return buttonsPanel;
	}

	/**
	 * associate this Panel to a list of dictionnaries
	 * 
	 * @param d
	 *            a DictionnaryPanel
	 */
	public void setDictionnaryPanel(DictionaryPanel d) {
		dictionaryPanel = d;
	}

	/**
	 * associate this Panel to a FlatFileTabbedPanel
	 * 
	 * @param d
	 *            a FlatFileTabbedPanel
	 */
	public void setTabFileTabbedPanel(FlatFileTabbedPanel panel) {
		flatFileTabbedPanel = panel;
		((XsdTreeStructImpl) xsdTree).flatFiles = panel.flatFileContainer;
	}

	public class AssociateDictionnaryListPanel extends JPanel {

		/**
		 * 
		 * @uml.property name="column"
		 */
		int column;

		JList list;

		JRadioButton closedAssociation = new JRadioButton(
				"closed association: null if value is not found in the dictionary");

		JRadioButton openAssociation = new JRadioButton(
				"open association: keep values not found in the dictionary");

		JRadioButton refusedAssociation = new JRadioButton(
				"reverse association: refuse values found in the dictionary");

		public ButtonGroup associationButtons = new ButtonGroup();

		/**
		 * 
		 * @uml.property name="column"
		 */
		public int getColumn() {
			return column;
		}

		public AssociateDictionnaryListPanel() {
			super();
			setLayout(new BorderLayout());

			list = new JList(dictionaryPanel.getExampleList());
			JScrollPane scrollList = new JScrollPane(list);
			list.addListSelectionListener(new SetColumnlistener());
			add(
					new JLabel(
							"Select the field that contains the definition and press OK:"),
					BorderLayout.NORTH);
			add(scrollList, BorderLayout.CENTER);

			Box box = new Box(BoxLayout.Y_AXIS);
			box.add(closedAssociation);
			box.add(openAssociation);
			//            box.add(refusedAssociation);

			associationButtons.add(closedAssociation);
			associationButtons.add(openAssociation);
			associationButtons.setSelected(closedAssociation.getModel(), true);
			//            associationButtons.add(refusedAssociation);

			add(box, BorderLayout.SOUTH);
		}

		public class SetColumnlistener implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				column = list.getSelectedIndex();
			}
		}
	}

	public class DisplayMessagesListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			try {
				editorPane.setPage(logoutFile.toURL());

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

	/**
	 * used to displayed in a panel an overview of problem found
	 */
	public class CheckListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).treeModel
					.getRoot();
			String errors;

			if (node == null) {
				errors = "No schema loaded!";
			} else {
				ArrayList paths = ((XsdTreeStructImpl) xsdTree).check(node);
				errors = paths.size() + " errors found: \n";
				for (int i = 0; i < paths.size(); i++) {
					errors += paths.get(i) + "\n";
				}
			}

			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setText(errors);

			JScrollPane areaScrollPane = new JScrollPane(editorPane);
			areaScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			areaScrollPane.setPreferredSize(new Dimension(500, 500));
			JFrame message = new JFrame();
			message.setResizable(true);
			//			JOptionPane.showMessageDialog(message, areaScrollPane);
			JFrame frame = new JFrame();
			frame.setSize(400, 300);
			frame.getContentPane().add(areaScrollPane);
			frame.show();

		}
	}

	/**
	 * used to display in a new panel informations about the node selected
	 */
	public class InfosListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

			if (node == null) {
				displayMessage("No node selected", "[XML maker]");
				return;
			}

			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setText(((XsdTreeStructImpl) xsdTree).name + "\n\n"
					+ ((XsdTreeStructImpl) xsdTree).getInfos(node));

			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			JFrame frame = new JFrame();
			frame.setSize(400, 300);
			frame.getContentPane().add(scrollPane);
			frame.show();
		}
	}

	/** used for loading the schema */
	public class LoadSchemaListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			loadSchema();
		}
	}

	public class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser fc = new JFileChooser(".");

				int returnVal = fc.showOpenDialog(new JFrame());
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}

				FileInputStream fis = new FileInputStream(fc.getSelectedFile());
				XMLDecoder xdec = new XMLDecoder(fis);
				xdec.close();
				fis.close();
				((XsdTreeStructImpl) xsdTree).check();
			} catch (FileNotFoundException fe) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Unable to load file",
						"[PSI makers: PSI maker] load dictionnary",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				System.out.println("pb: " + ex);
				StackTraceElement[] s = ex.getStackTrace();
				for (int i = 0; i < s.length; i++) {
					System.out.println(s[i]);
				}
			}
			//			loadSchema();
		}
	}

	/** used to duplicate the node selected */
	public class DuplicateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();
			if (node == null) {
				displayMessage("no node selected", "[XML maker]");
				return;
			}
			((XsdTreeStructImpl) xsdTree).expendChoices.add(node
					.getPath2String());
			((XsdTreeStructImpl) xsdTree).expendChoices.add(null);
			duplicateNode(node);
		}
	}

	/** used to duplicate the node selected */
	public class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();
			if (node == null) {
				displayMessage("no node selected", "[XML maker]");
				return;
			}
			((XsdTreeStructImpl) xsdTree).expendChoices.remove(node
					.getPath2String());
			((XsdTreeStructImpl) xsdTree).expendChoices.add(null);
			duplicateNode(node);
		}
	}

	/**
	 * used to replace the node by its original value, if a choice has been
	 * done.
	 */
	public class OriginalNodeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();
			if (node == null) {
				displayMessage("no node selected", "[XML maker]");
				return;
			}

			//			if (node.originalParent == null) {
			if (!node.transparent) {
				displayMessage("No choice has been done for this node.",
						"[XML maker]");
				return;
			}

			XsdNode parent = (XsdNode) node.getParent();

			int position = parent.getIndex(node);

			//			parent.insert(node.originalParent, position);
			//			parent.remove(position + 1);
			xsdTree.undoChoice(node);

			//			node.originalParent.isExtended = false;
			((XsdTreeStructImpl) xsdTree).treeModel.reload(parent);
		}
	}

	public class TreeMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();
			if (selectedNode == null)
				return;
			String value;
			String text = ((XsdTreeStructImpl) xsdTree)
					.getAssociationInfo(selectedNode);
			if ((value = ((XsdTreeStructImpl) xsdTree).getValue(selectedNode)) != "") {
				text += "\nvalue: " + value;
			}

			if (e.getClickCount() == 2) {
				doAssociation();
			}

			if (selectedNode != null) {
				associationLabel.setText(text);
			}
		}
	}

	public void associateFlatFile(XsdNode node, int flatFileIndex) {
		XsdNode previousAssociation = null;

		int previousFlatfileAssociated = ((XsdTreeStructImpl) xsdTree).associatedFlatFiles
				.indexOf(node);

		try {
			((XsdTreeStructImpl) xsdTree).associatedFlatFiles.set(
					flatFileTabbedPanel.getSelectedIndex(), null);
		} catch (Exception e) {
			/* ok, no association yet */
		}

		/* delete name from ex-associated flatfile */
		if (previousFlatfileAssociated > -1)
			flatFileTabbedPanel.tabbedPane.setTitleAt(
					previousFlatfileAssociated, "");

		((XsdTreeStructImpl) xsdTree).associateFlatFile(node, flatFileIndex);
		flatFileTabbedPanel.tabbedPane.setTitleAt(
				flatFileTabbedPanel.tabbedPane.getSelectedIndex(), node
						.toString());
	}

	public void associateDefaultValue(XsdNode node) {
		String value;
		if (((XsdTreeStructImpl) xsdTree).hasDefaultValue(node)) {
			value = (String) JOptionPane.showInputDialog(new JFrame(),
					"Enter a default value, \n",
					(String) ((XsdTreeStructImpl) xsdTree).associatedValues
							.get(node));
		} else {
			value = (String) JOptionPane.showInputDialog(new JFrame(),
					"Enter a default value, \n", lastValueEntered);
		}

		if (value != null) {
			if (((Annotated) (node.getUserObject())).getStructureType() != Structure.ELEMENT
					&& ((Annotated) (node.getUserObject())).getStructureType() != Structure.ATTRIBUTE) {
				JOptionPane
						.showMessageDialog(
								new JFrame(),
								"a value can only be associated with an node of type element or attribute",
								"associating a value",
								JOptionPane.ERROR_MESSAGE);
				return;
			}
			lastValueEntered = value;
			((XsdTreeStructImpl) xsdTree).associateDefaultValue(node, value);
		}
	}

	/**
	 * associate a dictionnary to the node selected. Each time a value will be
	 * requested for this node, it will be changed for its replacement value in
	 * target list if it exists
	 * 
	 * @param value
	 *            the value
	 */
	public void associateDictionnary(XsdNode node) {

		int dictionnary = dictionaryPanel.getSelectedDictionnary();

		if (dictionnary == -1) { // no selection
			displayMessage("No dictonnary selected", "[XML maker]");
			return;
		}

		if (dictionaryPanel.getExampleList().length == 0) { // no selection
			displayMessage("This dictionnary does not contain any value,"
					+ " maybe the separator has not been set properly.",
					"[XML maker]");
			return;
		}

		AssociateDictionnaryListPanel adp = new AssociateDictionnaryListPanel();
		int confirm = JOptionPane.showConfirmDialog(null, adp,
				"[XML maker] load dictionnary", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (confirm != JOptionPane.OK_OPTION)
			return;

		if (node == null) {
			displayMessage("No node selected", "[XML maker]");
			return;
		}

		((XsdTreeStructImpl) xsdTree).associateDictionnary(node, dictionnary,
				adp.getColumn(), adp.closedAssociation.isSelected());
	}

	public void associateField(XsdNode node) {
		String path = flatFileTabbedPanel.getSelectedPath();
		if (!path.matches("([0-9]\\.)*[0-9]+")) {
			displayMessage("No field selected", "[XML maker]");
			return;
		}
		//		AssociateFieldPanel afp = new AssociateFieldPanel();
		//		int confirm = JOptionPane.showConfirmDialog(null, afp,
		//				"[XML maker] field association", JOptionPane.OK_CANCEL_OPTION,
		//				JOptionPane.QUESTION_MESSAGE);
		//
		//		if (confirm != JOptionPane.OK_OPTION)
		//			return;

		if (node == null) {
			displayMessage("No node selected", "[XML maker]");
			return;
		}

		//		if (afp.regexp.getText().trim().length() > 0) {
		//			try {
		//				Pattern.compile(afp.regexp.getText().trim());
		//			} catch (java.util.regex.PatternSyntaxException pse) {
		//				displayMessage("unvalid regular expression", "[XML Maker]");
		//				return;
		//			}
		//		}

		//		((XsdTreeStructImpl) xsdTree).associateField(node, path,
		//				afp.unduplicableAssociation.isSelected());

		((XsdTreeStructImpl) xsdTree).associateField(node, path, false);

		//		if (afp.regexp.getText().trim().length() > 0)
		//			((XsdTreeStructImpl) xsdTree).associateValidationRegexp(node,
		//					afp.regexp.getText().trim());

	}

	public class GenericCancelAssociationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

			if (selectedNode == null) {
				displayMessage("No node selected", "[XML maker]");
				return;
			}

			if (fieldAssociation.isSelected())
				((XsdTreeStructImpl) xsdTree)
						.cancelAssociateField(selectedNode);
			else if (dictionnaryAssociation.isSelected())
				((XsdTreeStructImpl) xsdTree)
						.cancelAssociateDictionnary(selectedNode);
			else if (defaultAssociation.isSelected())
				((XsdTreeStructImpl) xsdTree).cancelDefaultValue(selectedNode);
			else if (autoGenerationAssociationButton.isSelected())
				((XsdTreeStructImpl) xsdTree).cancelAutogenerate(selectedNode);
			else if (flatFileAssociation.isSelected()) {
				((XsdTreeStructImpl) xsdTree)
						.cancelAssociateFlatFile(selectedNode);

			}
			//                JOptionPane.showMessageDialog(new JFrame(),
			//                        "you cannot change association to a flat file",
			//                        "[XML maker]", JOptionPane.ERROR_MESSAGE);
			else if (duplicableFieldAssociation.isSelected()) {
				((XsdTreeStructImpl) xsdTree)
						.cancelDuplicableField(selectedNode);
				return;
			}
		}
	}

	/** used to display the preview */
	public class PreviewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (((XsdTreeStructImpl) xsdTree).rootNode == null) {
				displayMessage("No schema loaded", "[XML maker]");
				return;
			}

			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			try {
				editorPane
						.setText(((XsdTreeStructImpl) xsdTree)
								.previewNode((XsdNode) ((XsdTreeStructImpl) xsdTree).tree
										.getLastSelectedPathComponent()));
			} catch (java.lang.NullPointerException noNodeExeption) {
				/* no node selected */
				editorPane.setText("No preview available.");
			}
			JFrame frame = new JFrame();
			frame.setSize(400, 300);
			frame.getContentPane().add(new JScrollPane(editorPane));
			frame.show();
		}
	}

	/**
	 * print a xml output for the whole file
	 */
	public class PrintErrorsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser fileChooser = new JFileChooser(".");
				int confirm = fileChooser.showSaveDialog(new JFrame());

				if (confirm != JOptionPane.OK_OPTION)
					return;

				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(fileChooser.getSelectedFile())));

				//				out.write(messagesTextArea.getText());

				out.flush();
				out.close();

			} catch (FileNotFoundException fe) {
				JOptionPane.showMessageDialog(new JFrame(),
						"unable to write file", "XML maker",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(new JFrame(),
						"unable to write file", "XML maker",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/** set an id that will be used as prefix for autogenerated values */
	public class SetIdListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String s = (String) JOptionPane.showInputDialog(new JFrame(),
					"Enter a default value, \n",
					((XsdTreeStructImpl) xsdTree).id);

			if (s != null)
				((XsdTreeStructImpl) xsdTree).id = s;
		}
	}

	public void setCellRenderer() {
		try {
			((XsdTreeStructImpl) xsdTree).tree
					.setCellRenderer(new XslTreeRenderer());
		} catch (Exception e) {
			System.out.println(xsdTree + ", "
					+ ((XsdTreeStructImpl) xsdTree).tree);
		}
	}

	public class XslTreeRenderer extends DefaultTreeCellRenderer {
		ImageIcon iconAttribute;

		ImageIcon iconElement;

		public XslTreeRenderer() {
			iconAttribute = new ImageIcon("images/ic-att.gif");
			iconElement = new ImageIcon("images/ic-elt.gif");
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			XsdNode node = (XsdNode) value;
			/* set icon and tooltip */
			switch (((Annotated) node.getUserObject()).getStructureType()) {
			case Structure.GROUP:
				setIcon(null);
				break;
			case Structure.ATTRIBUTE:
				setIcon(iconAttribute);
				setToolTipText("default value: "
						+ ((AttributeDecl) node.getUserObject())
								.getDefaultValue());
				break;
			case Structure.ELEMENT:
				setIcon(iconElement);
				try {
					setToolTipText(((Documentation) ((Annotation) ((ElementDecl) node
							.getUserObject()).getAnnotations().nextElement())
							.getDocumentation().nextElement()).getContent());
				} catch (Exception e) {
					setToolTipText("no documentation");
				}

				break;
			}

			switch (((Annotated) node.getUserObject()).getStructureType()) {
			case Structure.ATTRIBUTE:
				setText(getText()
						+ " ("
						+ ((AttributeDecl) node.getUserObject())
								.getSimpleType().getName() + ")      ");
				break;
			case Structure.ELEMENT:
				String type = null;
				try {
					type = ((ElementDecl) node.getUserObject()).getType()
							.getName();
				} catch (NullPointerException npe) {
					/* no type defined */
					System.out
							.println("WARNING: no type declaration for element "
									+ node.toString());
				}
				int max = node.max;
				String text = getText();
				try {
					text += " ["
							+ ((ElementDecl) node.getUserObject()).getType()
									.getBaseType().getName() + "]";
				} catch (Exception e) {
					/* no base type */
				}
				text += " (";
				if (type != null)
					text += type + ", ";
				text += "max: ";
				if (max == -1)
					text += "unbounded";
				else
					text += max;
				text += ")      ";
				setText(text);
			}

			if (((XsdTreeStructImpl) xsdTree).associatedDuplicableFields
					.containsKey(node)) {
				setText(getText().substring(0, getText().length() - 5) + "*");
			}

			if (((XsdTreeStructImpl) xsdTree).unduplicableNodes.contains(node)) {
				setText(getText().substring(0, getText().length() - 5) + "+");
			}

			if (((XsdTreeStructImpl) xsdTree).associatedOpenDictionary
					.containsKey(node)
					|| ((XsdTreeStructImpl) xsdTree).associatedClosedDictionary
							.containsKey(node)) {
				setText(getText().substring(0, getText().length() - 5)
						+ "(dictionnary)");
			}

			setForeground(Color.LIGHT_GRAY);

			if (node.isUsed)
				setForeground(Color.BLACK);

			/* show error */
			if (!node.isCheckedOk && node.isRequired)
				setForeground(Color.RED);

			if (((XsdTreeStructImpl) xsdTree).isAffected(node)
					|| ((XsdTreeStructImpl) xsdTree).hasDefaultValue(node)
					|| ((XsdTreeStructImpl) xsdTree).associatedAutogeneration
							.contains(node))
				setForeground(Color.BLUE);

			/* show nodes associated to flat files */
			if (((XsdTreeStructImpl) xsdTree).associatedFlatFiles
					.contains(node))
				setForeground(Color.GREEN);

			if (node.transparent)
				setForeground(Color.LIGHT_GRAY);

			return this;
		}
	}

	public void reload() {
		super.reload();
	}

	/**
	 * print a xml output for the whole file
	 */
	public class PrintListener implements ActionListener {
		//        JTextField logoutFileName = new JTextField("log.out");
		//
		//        JFileChooser logFileChooser = new JFileChooser(".");
		//
		PrintWriter logoutPrintWriter;

		public void actionPerformed(ActionEvent e) {

			if (((XsdTreeStructImpl) xsdTree).rootNode == null) {
				displayMessage("No schema loaded", "[XML maker]");
				return;
			}

			TreeNode[] path = ((XsdTreeStructImpl) xsdTree).rootNode.getPath();
			try {
				String defaultDirectory = Utils.lastVisitedDirectory;
				if (Utils.lastVisitedOutputDirectory != null)
					defaultDirectory = Utils.lastVisitedOutputDirectory;

				//                logFileChooser.setCurrentDirectory(new
				// File(defaultDirectory));
				//
				JFileChooser fileChooser = new JFileChooser(defaultDirectory);
				//                JLabel logFileLabel = new JLabel("log file:");
				//                JButton logoutButton = new JButton("log file");
				//                logoutButton.addActionListener(new LogoutListener());
				//                Box logPanel = new Box(BoxLayout.Y_AXIS);
				//                logPanel.add(logFileLabel);
				//                logPanel.add(logoutFileName);
				//                logPanel.add(logoutButton);
				//                fileChooser.setAccessory(logPanel);

				int confirm = fileChooser.showSaveDialog(new JFrame());

				if (confirm != JOptionPane.OK_OPTION)
					return;

				Utils.lastVisitedDirectory = fileChooser.getSelectedFile()
						.getPath();
				Utils.lastVisitedOutputDirectory = fileChooser
						.getSelectedFile().getPath();

				File out = fileChooser.getSelectedFile();

				//                if ((logoutFile = logFileChooser.getSelectedFile()) == null)
				// {
				//                    logoutFile = new File("log.out");
				//                }
				//                
				logoutFile = new File(out.getName() + ".log");

				//                logoutPrintWriter = new PrintWriter(new BufferedWriter(
				//                        new FileWriter(logoutFile)));
				//                logoutFileName.setText(logoutFile.getName());
				//
				//              logoutPrintWriter = new PrintWriter(new BufferedWriter(
				//              new FileWriter(logoutFile)));
				//              logoutFileName.setText(logoutFile.getName());

				logoutPrintWriter = new PrintWriter(new BufferedWriter(
						new FileWriter(logoutFile)));
				//                logoutFileName.setText(logoutFile.getName());

				//              logoutPrintWriter = new PrintWriter(new BufferedWriter(
				//              new FileWriter(logoutFile)));
				//              logoutFileName.setText(logoutFile.getName());

				MarshallingObserver observer = new MarshallingObserver();
				observer
						.setObservable(((XsdTreeStructImpl) xsdTree).observable);
				((XsdTreeStructImpl) xsdTree).observable.addObserver(observer);

				//                new Thread(observer).start();
				//                javax.swing.SwingUtilities.invokeLater(observer);
				observer.start();
				((XsdTreeStructImpl) xsdTree).print(out, logoutFile);

				//                JPanel panel = new JPanel();
				//                JButton displayMessages = new JButton(
				//                        "Click here to display error messages");
				//                displayMessages
				//                        .addActionListener(new DisplayMessagesListener());
				//                panel.add(new JLabel("Marshalling done."));
				//                panel.add(displayMessages);
				//
				//                JOptionPane.showMessageDialog(new JFrame(), panel,
				//                        "[XML makers] creating the xml document",
				//                        JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException fe) {
				JOptionPane.showMessageDialog(new JFrame(),
						"unable to write file", "XML maker",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(new JFrame(),
						"unable to write file", "XML maker",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		//        public class LogoutListener implements ActionListener {
		//            public void actionPerformed(ActionEvent e) {
		//                int confirm = logFileChooser.showSaveDialog(new JFrame());
		//                if (confirm != JOptionPane.OK_OPTION)
		//                    return;
		//                try {
		//                    /* log file */
		//                    File logoutFile = logFileChooser.getSelectedFile();
		//                    logoutPrintWriter = new PrintWriter(new BufferedWriter(
		//                            new FileWriter(logoutFile)));
		//                    logoutFileName.setText(logoutFile.getName());
		//                } catch (FileNotFoundException fe) {
		//                    JOptionPane.showMessageDialog(new JFrame(),
		//                            "unable to write log file", "XML maker",
		//                            JOptionPane.ERROR_MESSAGE);
		//                } catch (IOException ex) {
		//                    JOptionPane.showMessageDialog(new JFrame(),
		//                            "unable to write log file", "XML maker",
		//                            JOptionPane.ERROR_MESSAGE);
		//                }
		//            }
		//        }
	}

	public class AssociateFieldPanel extends JPanel {
		String filter = "";

		JTextField regexp = new JTextField("");

		JLabel regexpLbl = new JLabel("validate value on regexp: ");

		JCheckBox unduplicableAssociation = new JCheckBox(
				"do not duplicate the node (keep value of first line)");

		public AssociateFieldPanel() {
			super();

			setLayout(new BorderLayout());
			Box box = new Box(BoxLayout.Y_AXIS);
			box.add(unduplicableAssociation);
			box.add(regexpLbl);
			box.add(regexp);

			add(box, BorderLayout.SOUTH);
		}

		public AssociateFieldPanel(String regexp,
				boolean unduplicableAssociation) {
			super();
			System.out.println("ee" + regexp);
			setLayout(new BorderLayout());
			if (regexp != null) {
				this.filter = regexp;
				this.regexp = new JTextField(regexp);
			}
			this.unduplicableAssociation.setSelected(unduplicableAssociation);

			Box box = new Box(BoxLayout.Y_AXIS);
			box.add(this.unduplicableAssociation);
			box.add(this.regexpLbl);
			box.add(this.regexp);

			add(box, BorderLayout.SOUTH);
		}
	}

	/**
	 * used to display in a new panel informations about the node selected
	 */
	public class EditFieldAssociationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

			if (node == null) {
				displayMessage("No node selected", "[XML maker]");
				return;
			}
			if (!((XsdTreeStructImpl) xsdTree).associatedFields
					.containsKey(node)) {
				displayMessage("this node is not associated to a field",
						"[XML maker]");
				return;
			}

			/* keep old value to suggest it in the panel */
			boolean unduplicableNode = ((XsdTreeStructImpl) xsdTree).unduplicableNodes
					.contains(node);

			String regexp = ((XsdTreeStructImpl) xsdTree).getRegexp(node);
			//	        if (((XsdTreeStructImpl)
			// xsdTree).validationRegexps.containsKey(node)) {
			//	        	regexp = (String) ((XsdTreeStructImpl)
			// xsdTree).validationRegexps.get(node);
			//	        	System.out.println("rr"+regexp);
			//	        }

			AssociateFieldPanel afp = new AssociateFieldPanel(regexp,
					unduplicableNode);
			int confirm = JOptionPane.showConfirmDialog(null, afp,
					"[XML maker] field association",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (confirm != JOptionPane.OK_OPTION)
				return;

			if (afp.regexp.getText().trim().length() > 0) {
				try {
					Pattern.compile(afp.regexp.getText().trim());
				} catch (java.util.regex.PatternSyntaxException pse) {
					displayMessage("unvalid regular expression", "[XML Maker]");
					return;
				}
			}

			((XsdTreeStructImpl) xsdTree).unduplicableNodes.remove(node);
			if (afp.unduplicableAssociation.isSelected())
				((XsdTreeStructImpl) xsdTree).unduplicableNodes.add(node);

			((XsdTreeStructImpl) xsdTree).validationRegexps.remove(node);
			if (afp.regexp.getText().trim().length() > 0)
				((XsdTreeStructImpl) xsdTree).associateValidationRegexp(node,
						afp.regexp.getText().trim());

		}
	}

}