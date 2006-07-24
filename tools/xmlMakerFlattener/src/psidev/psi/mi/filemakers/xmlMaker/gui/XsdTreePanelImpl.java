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
package psidev.psi.mi.filemakers.xmlMaker.gui;

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
import java.util.Date;
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
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;


import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.Annotation;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.Documentation;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Structure;

import psidev.psi.mi.filemakers.xmlMaker.structure.XsdTreeStructImpl;
import psidev.psi.mi.filemakers.xsd.MessageManagerInt;
import psidev.psi.mi.filemakers.xsd.Utils;
import psidev.psi.mi.filemakers.xsd.XsdNode;

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
public class XsdTreePanelImpl extends psidev.psi.mi.filemakers.xsd.AbstractXsdTreePanel {

	public class GenericAssociationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			doAssociation();
		}
	}

	public void doAssociation() {
		XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
				.getLastSelectedPathComponent();

		if (selectedNode == null) {
			xsdTree.getMessageManager().sendMessage("no node seletected", MessageManagerInt.errorMessage);
//			JOptionPane
//			.showMessageDialog(
//					new JFrame(),
//					"No node selected.",
//					"[XML maker]",
//					JOptionPane.ERROR_MESSAGE);	
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
			xsdTree.getMessageManager().sendMessage("no value can be associated to this node", MessageManagerInt.errorMessage);
//			JOptionPane
//			.showMessageDialog(
//					new JFrame(),
//					"No value can be associated to this node.",
//					"[XML maker]",
//					JOptionPane.ERROR_MESSAGE);	
			return;
		}

		if (fieldAssociation.isSelected()) {
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
	public XsdTreePanelImpl(XsdTreeStructImpl xsdTree, JTextPane messagePane) {
		super(xsdTree);
//		try {
//					UIManager.setLookAndFeel(
//		 "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//				}
//				catch (Exception e) {System.out.println("beurk");}
		
//		messagesPane = new JTextPane (new DefaultStyledDocument());
		messagePane.setEditable(false);

		JScrollPane scrollpane = new JScrollPane(messagePane);
		scrollpane.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));	
		scrollpane.setMinimumSize(new Dimension(200, 150));	
		scrollpane.setPreferredSize(new Dimension(200, 150));	
		scrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setBorder(new TitledBorder("Messages"));

		add(scrollpane, BorderLayout.SOUTH);
		add(getButtonPanel(), BorderLayout.EAST);
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

//	File logoutFile;

	/**
	 * create a button panel that includes buttons for loading the schema, to
	 * associate a node to a flat file, a cell a default value or to specify
	 * that a value should be automaticaly generated, to get informations about
	 * the node, print the XML file or just have a preview of it.
	 */
	public Box getButtonPanel() {
//		associationLabel.setEditable(false);

		Box buttonsPanel = new Box(BoxLayout.Y_AXIS);

		 JPanel treeBox = new  JPanel();//
		 treeBox.setLayout(new BoxLayout(treeBox, BoxLayout.Y_AXIS));//(BoxLayout.Y_AXIS);

//		Box mappingBox = new Box(BoxLayout.Y_AXIS);
//		mappingBox.setBorder(new TitledBorder("Mapping"));

		Box associationBox = new Box(BoxLayout.Y_AXIS);
		associationBox.setBorder(new TitledBorder("Associations"));

		Box nodeBox = new Box(BoxLayout.Y_AXIS);
		nodeBox.setBorder(new TitledBorder("Node"));
		
		Box outputBox = new Box(BoxLayout.Y_AXIS);
		outputBox.setBorder(new TitledBorder("Output"));

		/* add a button for loading a XML Schema */
		JButton loadFileb = new JButton("Open");
		Utils.setDefaultSize(loadFileb);
		loadFileb.addActionListener(new LoadSchemaListener());

		JButton setIdb = new JButton("Prefix");
		Utils.setDefaultSize(setIdb);
		setIdb.addActionListener(new SetIdListener());

		/* add a button for duplicate a node (in case of lists) */
		JButton duplicateb = new JButton("Duplicate");
		Utils.setDefaultSize(duplicateb);
		duplicateb.addActionListener(new DuplicateListener());

		/* add a button for restauring original choice */
		JButton choiceb = new JButton("Restaure");
		Utils.setDefaultSize(choiceb);
		choiceb.addActionListener(new OriginalNodeListener());

		JButton infosb = new JButton("About");
		Utils.setDefaultSize(infosb);
		infosb.addActionListener(new InfosListener());

		JButton checkb = new JButton("Check");
		Utils.setDefaultSize(checkb);
		checkb.addActionListener(new CheckListener());
		
		JButton previewb = new JButton("Preview");
		Utils.setDefaultSize(previewb);
		previewb.addActionListener(new PreviewListener());

		JButton printb = new JButton("Make XML");
		Utils.setDefaultSize(printb);
		printb.addActionListener(new PrintListener());

		treeBox.add(loadFileb);
		treeBox.add(setIdb);
		treeBox.add(checkb);
		treeBox.setBorder(new TitledBorder("Schema"));

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

		JButton genericAssociationb = new JButton("Associate");
		Utils.setDefaultSize(genericAssociationb);
		genericAssociationb.addActionListener(new GenericAssociationListener());

		JButton genericCancelAssociationb = new JButton("Cancel");
		Utils.setDefaultSize(genericCancelAssociationb);
		genericCancelAssociationb
				.addActionListener(new GenericCancelAssociationListener());

//		Box associationb1Box = new Box(BoxLayout.Y_AXIS);
//		associationb1Box.setSize(10,10);
//		Box associationb2Box = new Box(BoxLayout.Y_AXIS);
//		Box associationb3Box = new Box(BoxLayout.Y_AXIS);
//		Box associationb4Box = new Box(BoxLayout.Y_AXIS);

		associationBox.add(flatFileAssociation);

		associationBox.add(duplicableFieldAssociation);

		associationBox.add(fieldAssociation);
		JButton editFieldb = new JButton("validation");
		Utils.setDefaultSize(editFieldb);		
		editFieldb.addActionListener(new EditFieldAssociationListener());
		associationBox.add(editFieldb);
		associationBox.add(dictionnaryAssociation);
		associationBox.add(defaultAssociation);
		associationBox.add(defaultAssociation);
		associationBox.add(autoGenerationAssociationButton);

//		JScrollPane scrollPane = new JScrollPane(associationLabel);
//		scrollPane
//				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		associationb2Box.add(scrollPane);
		associationBox.add(genericAssociationb);
		associationBox.add(genericCancelAssociationb);

//		associationb2Box.add(associationb4Box);
//		associationb2Box.add(associationb3Box);

//		associationBox.add(associationb2Box);
//		associationBox.add(associationb1Box);

//		Box lineBox2 = new Box(BoxLayout.Y_AXIS);
//		Box lineBox3 = new Box(BoxLayout.Y_AXIS);


		buttonsPanel.add(treeBox);
		buttonsPanel.add(associationBox);
		buttonsPanel.add(nodeBox);
		buttonsPanel.add(outputBox);
//		lineBox2.add(lineBox2);
//		buttonsPanel.add(lineBox3);
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

			associationButtons.add(closedAssociation);
			associationButtons.add(openAssociation);
			associationButtons.setSelected(closedAssociation.getModel(), true);

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
//			try {
//				editorPane.setPage(logoutFile.toURL());

				JScrollPane areaScrollPane = new JScrollPane(editorPane);
				areaScrollPane
						.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				areaScrollPane.setPreferredSize(new Dimension(600, 650));
				JOptionPane.showMessageDialog(new JFrame(), areaScrollPane);
//			} catch (IOException ioe) {
//				JOptionPane.showMessageDialog(new JFrame(),
//						"Documentation not found.", "Documentation",
//						JOptionPane.ERROR_MESSAGE);
//			}
		}
	}

	/**
	 * used to displayed in a panel an overview of problem found
	 */
	public class CheckListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
			.getLastSelectedPathComponent();
	
			boolean errors = false;
			
			if (node == null) {
				node = (XsdNode) ((XsdTreeStructImpl) xsdTree).treeModel
				.getRoot();
			}			

			xsdTree.getMessageManager().sendMessage("[CHECKING] CHECK NODE "+node, MessageManagerInt.simpleMessage);
			
			if (node == null) {
				errors = true;
				xsdTree.getMessageManager().sendMessage("no schema loaded", MessageManagerInt.errorMessage);
				return;
			} else {
				errors = !((XsdTreeStructImpl) xsdTree).check(node);
			}
			
			if (errors)
				xsdTree.getMessageManager().sendMessage("[CHECKING] failed, errors have been found", MessageManagerInt.simpleMessage);
			else 
				xsdTree.getMessageManager().sendMessage("[CHECKING] no errors found", MessageManagerInt.simpleMessage);
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
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
				return;
			}

			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setText(((XsdTreeStructImpl) xsdTree).name + "\n"
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
				xsdTree.getMessageManager().sendMessage("unable to load file", MessageManagerInt.errorMessage);
//				JOptionPane.showMessageDialog(new JFrame(),
//						"Unable to load file",
//						"[PSI makers: PSI maker] load dictionnary",
//						JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				System.out.println("pb: " + ex);
				StackTraceElement[] s = ex.getStackTrace();
				for (int i = 0; i < s.length; i++) {
					System.out.println(s[i]);
				}
			}
		}
	}

	/** used to duplicate the node selected */
	public class DuplicateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();
			if (node == null) {
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
				return;
			}
			xsdTree.duplicateNode(node);
		}
	}

//	/** used to duplicate the node selected */
//	public class DeleteListener implements ActionListener {
//		public void actionPerformed(ActionEvent e) {
//			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
//					.getLastSelectedPathComponent();
//			if (node == null) {
//				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
//				return;
//			}
//			((XsdTreeStructImpl) xsdTree).expendChoices.remove(node
//					.getPath2String());
//			((XsdTreeStructImpl) xsdTree).expendChoices.add(null);
//			xsdTree.duplicateNode(node);
//		}
//	}

	/**
	 * used to replace the node by its original value, if a choice has been
	 * done.
	 */
	public class OriginalNodeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();
			if (node == null) {
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
				return;
			}

			//			if (node.originalParent == null) {
			if (!node.transparent) {
				xsdTree.getMessageManager().sendMessage("No choice has been done for this node.", MessageManagerInt.errorMessage);
				return;
			}

			XsdNode parent = (XsdNode) node.getParent();

			int position = parent.getIndex(node);

			xsdTree.undoChoice(node);

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
				text += " value: " + value;
			}

			if (e.getClickCount() == 2) {
				doAssociation();
			}

			if (selectedNode != null && text.trim().length() >0) {
				xsdTree.getMessageManager().sendMessage("["+selectedNode.getName()+"] " +text.trim(), MessageManagerInt.simpleMessage);
			}
		}
	}

	public void associateFlatFile(XsdNode node, int flatFileIndex) {
		if (flatFileTabbedPanel.getFlatFileByIndex(0).fileURL == null) {
			xsdTree.getMessageManager().sendMessage("no flat file has been loaded in selected tab yet", MessageManagerInt.errorMessage);
//			JOptionPane
//			.showMessageDialog(
//					new JFrame(),
//					"No flat file has been loaded in selected tab yet.",
//					"associating a file",
//					JOptionPane.ERROR_MESSAGE);	
			return;
		}
		
		
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
			xsdTree.getMessageManager().sendMessage("No dictonnary selected", MessageManagerInt.errorMessage);
			return;
		}

		if (dictionaryPanel.getExampleList().length == 0) { // no selection
			xsdTree.getMessageManager().sendMessage("This dictionnary does not contain any value,"
					+ " maybe the separator has not been set properly.", MessageManagerInt.errorMessage);
			return;
		}

		AssociateDictionnaryListPanel adp = new AssociateDictionnaryListPanel();
		int confirm = JOptionPane.showConfirmDialog(null, adp,
				"[XML maker] load dictionnary", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (confirm != JOptionPane.OK_OPTION)
			return;

		if (node == null) {
			xsdTree.getMessageManager().sendMessage("No node selected", MessageManagerInt.errorMessage);
			return;
		}

		((XsdTreeStructImpl) xsdTree).associateDictionnary(node, dictionnary,
				adp.getColumn(), adp.closedAssociation.isSelected());
	}

	public void associateField(XsdNode node) {
		String path = flatFileTabbedPanel.getSelectedPath();
		if (!path.matches("([0-9]+\\.)*[0-9]+")) {
			xsdTree.getMessageManager().sendMessage("No field selected " + path, MessageManagerInt.errorMessage);
			return;
		}

		if (node == null) {
			xsdTree.getMessageManager().sendMessage("No node selected", MessageManagerInt.errorMessage);
			return;
		}

		((XsdTreeStructImpl) xsdTree).associateField(node, path, false);
	}

	public class GenericCancelAssociationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

			if (selectedNode == null) {
				xsdTree.getMessageManager().sendMessage("No node selected", MessageManagerInt.errorMessage);
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
				xsdTree.getMessageManager().sendMessage("No schema loaded", MessageManagerInt.errorMessage);
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

				out.flush();
				out.close();

			} catch (FileNotFoundException fe) {
				xsdTree.getMessageManager().sendMessage("unable to write file", MessageManagerInt.errorMessage);
//				JOptionPane.showMessageDialog(new JFrame(),
//						"unable to write file", "XML maker",
//						JOptionPane.ERROR_MESSAGE);
			} catch (IOException ex) {
				xsdTree.getMessageManager().sendMessage("unable to write file", MessageManagerInt.errorMessage);
//				JOptionPane.showMessageDialog(new JFrame(),
//						"unable to write file", "XML maker",
//						JOptionPane.ERROR_MESSAGE);
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
					.setCellRenderer(new XsdTreeRenderer());
		} catch (Exception e) {
			System.out.println(xsdTree + ", "
					+ ((XsdTreeStructImpl) xsdTree).tree);
		}
	}

	public class XsdTreeRenderer extends DefaultTreeCellRenderer {
		ImageIcon iconAttribute;

		ImageIcon iconElement;

		public XsdTreeRenderer() {
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
				try {
				setText(getText()
						+ " ("
						+ ((AttributeDecl) node.getUserObject())
								.getSimpleType().getName() + ")      ");
				} catch (java.lang.NullPointerException npe) {
					/* no type defined, assume it's text */ 
					setText(getText()
							+ " ("
							+ "no type" + ")      ");
				}
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
			
			
			XsdNode node2check = node;
			/* in case of transparent node: look at the first child:
			 * transparent node are only the one used for making a choice, and have always only one child;
			 */
			if (node.transparent && node.getChildCount() > 0 )
				node2check = (XsdNode) node.getChildAt(0);
			
			if (node2check.transparent)
				setForeground(Color.LIGHT_GRAY);
			
			if (node2check.isUsed)
				setForeground(Color.BLACK);

			/* show error */
			if (!node2check.isCheckedOk && node.isRequired)
				setForeground(Color.RED);

			if (((XsdTreeStructImpl) xsdTree).isAffected(node2check)
					|| ((XsdTreeStructImpl) xsdTree).hasDefaultValue(node)
					|| ((XsdTreeStructImpl) xsdTree).associatedAutogeneration
							.contains(node))
				setForeground(Color.BLUE);

			/* show nodes associated to flat files */
			if (((XsdTreeStructImpl) xsdTree).associatedFlatFiles
					.contains(node2check))
				setForeground(Color.GREEN);



			return this;
		}
	}

//	public void reload() {
//		super.reload();
//	}

	/**
	 * print a xml output for the whole file
	 */
	public class PrintListener implements ActionListener {
		PrintWriter logoutPrintWriter;

		public void actionPerformed(ActionEvent e) {

			if (((XsdTreeStructImpl) xsdTree).rootNode == null) {
				xsdTree.getMessageManager().sendMessage("No schema loaded", MessageManagerInt.errorMessage);
				return;
			}

//			TreeNode[] path = ((XsdTreeStructImpl) xsdTree).rootNode.getPath();
			try {
				String defaultDirectory = Utils.lastVisitedDirectory;
				if (Utils.lastVisitedOutputDirectory != null)
					defaultDirectory = Utils.lastVisitedOutputDirectory;

				JFileChooser fileChooser = new JFileChooser(defaultDirectory);

				int confirm = fileChooser.showSaveDialog(new JFrame());

				if (confirm != JOptionPane.OK_OPTION)
					return;

				Utils.lastVisitedDirectory = fileChooser.getSelectedFile()
						.getPath();
				Utils.lastVisitedOutputDirectory = fileChooser
						.getSelectedFile().getPath();

				File out = fileChooser.getSelectedFile();

				MarshallingObserver observer = new MarshallingObserver();
				observer
						.setObservable(((XsdTreeStructImpl) xsdTree).observable);
				((XsdTreeStructImpl) xsdTree).observable.addObserver(observer);
				
				Date DateCurrent=new Date(System.currentTimeMillis());
				
				xsdTree.getMessageManager().sendMessage("[CREATE XML] start writting XML document: "+ DateCurrent.toGMTString(), MessageManagerInt.simpleMessage);
				((XsdTreeStructImpl) xsdTree).print2(out);
				DateCurrent=new Date(System.currentTimeMillis());
				xsdTree.getMessageManager().sendMessage("[CREATE XML] finished writting XML document: "+ DateCurrent.toGMTString(), MessageManagerInt.simpleMessage);
				
			} catch (FileNotFoundException fe) {
				xsdTree.getMessageManager().sendMessage("unable to write file", MessageManagerInt.errorMessage);
			} catch (IOException ex) {
				xsdTree.getMessageManager().sendMessage("unable to write file", MessageManagerInt.errorMessage);
			}
		}

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
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
				return;
			}
			if (!((XsdTreeStructImpl) xsdTree).associatedFields
					.containsKey(node)) {
				xsdTree.getMessageManager().sendMessage("this node is not associated to a field", MessageManagerInt.errorMessage);
				return;
			}

			/* keep old value to suggest it in the panel */
			boolean unduplicableNode = ((XsdTreeStructImpl) xsdTree).unduplicableNodes
					.contains(node);

			String regexp = ((XsdTreeStructImpl) xsdTree).getRegexp(node);

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
					xsdTree.getMessageManager().sendMessage("unvalid regular expression", MessageManagerInt.errorMessage);
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