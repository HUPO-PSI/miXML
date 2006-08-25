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
package psidev.psi.mi.filemakers.xmlFlattener.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;


import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.Annotation;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.Documentation;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Structure;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import psidev.psi.mi.filemakers.xmlFlattener.structure.XsdTreeStructImpl;
import psidev.psi.mi.filemakers.xsd.MessageManagerInt;
import psidev.psi.mi.filemakers.xsd.Utils;
import psidev.psi.mi.filemakers.xsd.XsdNode;

/**
 * 
 * This class overides the abstract class AbstractXslTreePanel to provide a
 * graphical interface for the tree representation of a XML schema, with
 * management of transformation of an XML file to a flat file.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XsdTreePanelImpl extends psidev.psi.mi.filemakers.xsd.AbstractXsdTreePanel {

	public String exampleLine;

	public class associateNameListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

			if (selectedNode == null) {
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
				return;
			}

			associateName(selectedNode);
		}
	}

	public class NumerotationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (highAlphabeticb.isSelected())
				((XsdTreeStructImpl) xsdTree).numerotation_type = XsdTreeStructImpl.HIGH_ALPHABETIC_NUMEROTATION;
			if (lowAlphabeticb.isSelected())
				((XsdTreeStructImpl) xsdTree).numerotation_type = XsdTreeStructImpl.LOW_ALPHABETIC_NUMEROTATION;
			if (numericb.isSelected())
				((XsdTreeStructImpl) xsdTree).numerotation_type = XsdTreeStructImpl.NUMERIC_NUMEROTATION;
			if (noneb.isSelected())
				((XsdTreeStructImpl) xsdTree).numerotation_type = XsdTreeStructImpl.NO_NUMEROTATION;

			updatePreview();
		}
	}

	public ButtonGroup numerotationButtons;

	public JRadioButton numericb;

	public JRadioButton highAlphabeticb;

	public JRadioButton lowAlphabeticb;

	public JRadioButton noneb;

	public JCheckBox displayExample;

	/**
	 * display filter for selected node
	 *  
	 */
	public JLabel filter;// = new JTextField("no filter");

	/**
	 * create a new instance of XslTree The nodes will not be automaticaly
	 * duplicated even if the schema specify that more than one element of this
	 * type is mandatory
	 */
	public XsdTreePanelImpl(XsdTreeStructImpl xsdTree, JTextPane messagePane) {
		super(xsdTree, BorderLayout.EAST);

		MouseListener mouseListener = new TreeMouseAdapter();
		xsdTree.tree.addMouseListener(mouseListener);

		JScrollPane scrollpane = new JScrollPane(messagePane);
		scrollpane.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));	
		scrollpane.setMinimumSize(new Dimension(200, 150));	
		scrollpane.setPreferredSize(new Dimension(200, 150));
		scrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setBorder(new TitledBorder("Preview"));

		add(scrollpane, BorderLayout.SOUTH);
	}

	/**
	 * create a button panel that includes buttons for loading the schema and
	 * the document, to select and unselect a node, get informations about the
	 * node, choose a separator for the flat file and print the flat file
	 */
	public Box getButtonPanel() {
		Box buttonsPanel = new Box(BoxLayout.Y_AXIS);
		buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		Box treeBox = new Box(BoxLayout.Y_AXIS);
		treeBox.setBorder(new TitledBorder("Schema"));

		Box associationBox = new Box(BoxLayout.Y_AXIS);
		associationBox.setBorder(new TitledBorder("Association"));

		Box outputBox = new Box(BoxLayout.Y_AXIS);
		outputBox.setBorder(new TitledBorder("Output"));

		Box numerotationBox = new Box(BoxLayout.X_AXIS);
		numerotationBox.setBorder(new TitledBorder("Numerotation"));
		/* add a button for loading a XML Schema */
		JButton loadFileb = new JButton("Open schema");
		Utils.setDefaultSize(loadFileb);
		loadFileb.addActionListener(new LoadSchemaListener());

		/*
		 * add a button for associating the content of a node to a line in the
		 * flat file
		 */
		JButton selectLineNodeb = new JButton("Main node");
		Utils.setDefaultSize(selectLineNodeb);
		selectLineNodeb.addActionListener(new SelectLineNodeListener());

		JButton selectNodeb = new JButton("Select");
		Utils.setDefaultSize(selectNodeb);
		selectNodeb.addActionListener(new SelectNodeListener());

		JButton unselectNodeb = new JButton("Unselect");
		Utils.setDefaultSize(unselectNodeb);
		unselectNodeb.addActionListener(new UnselectNodeListener());

		JButton nameb = new JButton("Name");
		Utils.setDefaultSize(nameb);
		nameb.addActionListener(new associateNameListener());

		JButton filterb = new JButton("Filter");
		Utils.setDefaultSize(filterb);
		filterb.addActionListener(new AssociateFilterListener());

		JButton infosb = new JButton("About");
		Utils.setDefaultSize(infosb);
		infosb.addActionListener(new InfosListener());

		associationBox.add(selectNodeb);
		associationBox.add(unselectNodeb);
		associationBox.add(nameb);
		associationBox.add(filterb);
		filter = new JLabel("no filter");
		associationBox.add(filter);

		treeBox.add(loadFileb);

		associationBox.add(infosb);

		JButton loadXmlFileb = new JButton("Open document (XML)");
		Utils.setDefaultSize(loadXmlFileb);
		loadXmlFileb.addActionListener(new LoadDocumentListener());

		JButton setSeparatorb = new JButton("Separator");
		Utils.setDefaultSize(setSeparatorb);
		setSeparatorb.addActionListener(new SetSeparatorListener());

		JButton printTabFileb = new JButton("Print");
		Utils.setDefaultSize(printTabFileb);
		printTabFileb.addActionListener(new PrintFlatFileListener());

		treeBox.add(loadXmlFileb);
		treeBox.add(selectLineNodeb);

		outputBox.add(setSeparatorb);
		outputBox.add(printTabFileb);

		numerotationButtons = new ButtonGroup();

		numericb = new JRadioButton("1");
		highAlphabeticb = new JRadioButton("A");
		lowAlphabeticb = new JRadioButton("a");
		noneb = new JRadioButton("none");

		numericb.addActionListener(new NumerotationListener());
		highAlphabeticb.addActionListener(new NumerotationListener());
		lowAlphabeticb.addActionListener(new NumerotationListener());
		numerotationBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		numerotationButtons.add(numericb);
		numerotationButtons.add(highAlphabeticb);
		numerotationButtons.add(lowAlphabeticb);
		numerotationButtons.add(noneb);

		switch (((XsdTreeStructImpl) xsdTree).numerotation_type) {
		case XsdTreeStructImpl.HIGH_ALPHABETIC_NUMEROTATION:
			highAlphabeticb.setSelected(true);
			break;
		case XsdTreeStructImpl.LOW_ALPHABETIC_NUMEROTATION:
			lowAlphabeticb.setSelected(true);
			break;
		case XsdTreeStructImpl.NUMERIC_NUMEROTATION:
			numericb.setSelected(true);
			break;
		default:
			noneb.setSelected(true);
			break;
		}

		numerotationBox.add(numericb);
		numerotationBox.add(highAlphabeticb);
		numerotationBox.add(lowAlphabeticb);
		numerotationBox.add(noneb);

		buttonsPanel.add(treeBox);
		buttonsPanel.add(associationBox);
		buttonsPanel.add(outputBox);
		buttonsPanel.add(numerotationBox);

		displayExample = new JCheckBox("preview");
		buttonsPanel.add(displayExample);
		displayExample.addActionListener(new PreviewListener());
		
		return buttonsPanel;
	}

	/**
	 * Set the cell renderer with the local renderer
	 */
	public void setCellRenderer() {
		xsdTree.tree.setCellRenderer(new XslTreeRenderer());
	}

	public void setTreeSelectionListener() {
		xsdTree.tree.addTreeSelectionListener(new XsdTreeSelectionListener());
	}

	/**
	 * Used to parse the XML document using XML schema
	 * 
	 * @author Arnaud Ceol
	 *  
	 */
	public class LoadDocumentListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (xsdTree.schema == null) {
				xsdTree.getMessageManager().sendMessage("please load a XML schema first", MessageManagerInt.errorMessage);
				//                xsdTree.getMessageManager().sendMessage("Please load a XML schema first",
				//                        "[PSI makers: flattener] loading document");
				return;
			}
			loadDocument();

			/* display errors in Xml document */
			//			JEditorPane editorPane = new JEditorPane();
			//			editorPane.setEditable(false);
			Iterator it = xsdTree.xmlErrorHandler.errors.iterator();
			//            String message = "\n";
			while (it.hasNext()) {
				xsdTree.getMessageManager().sendMessage((String) it.next(), MessageManagerInt.errorMessage);
				//                titlesPreviewPane.setText(titlesPreviewPane.getText()
				//                        + it.next() + "\n");
			}

			updatePreview();
		}
	}

	/**
	 * Open a frame to choose an XML document and load it.
	 *  
	 */
	public void loadDocument() {
		try {
			String defaultDirectory = Utils.lastVisitedDirectory;
			if (Utils.lastVisitedDocumentDirectory != null)
				defaultDirectory = Utils.lastVisitedDocumentDirectory;

			JFileChooser fileChooser = new JFileChooser(defaultDirectory);
			int returnVal = fileChooser.showOpenDialog(new JFrame());

			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			URL fileURL = fileChooser.getSelectedFile().toURL();

			((XsdTreeStructImpl) xsdTree).loadDocument(fileURL);
			xsdTree.getMessageManager().sendMessage("XML document " + fileURL + " loaded.", MessageManagerInt.simpleMessage);
		} catch (IOException urie) {

		} catch (SAXException saxe) {

		}
		exampleLine = null;
		//        updatePreview();
	}

	public class PrintFlatFileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (xsdTree.rootNode == null) {
				xsdTree.getMessageManager().sendMessage("no schema loaded", MessageManagerInt.errorMessage);
				return;
			}

			if (((XsdTreeStructImpl) xsdTree).document == null) {
				xsdTree.getMessageManager().sendMessage("no document loaded", MessageManagerInt.errorMessage);
				return;
			}

			TreeNode[] path = xsdTree.rootNode.getPath();
			try {
				String defaultDirectory = Utils.lastVisitedDirectory;
				if (Utils.lastVisitedOutputDirectory != null)
					defaultDirectory = Utils.lastVisitedOutputDirectory;

				JFileChooser fileChooser = new JFileChooser(defaultDirectory);
				int returnVal = fileChooser.showSaveDialog(new JFrame());

				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}

				Utils.lastVisitedDirectory = fileChooser.getSelectedFile()
						.getPath();
				Utils.lastVisitedOutputDirectory = fileChooser
						.getSelectedFile().getPath();

				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(fileChooser.getSelectedFile())));

				((XsdTreeStructImpl) xsdTree).resetCount();
				((XsdTreeStructImpl) xsdTree).write(out);
				out.flush();
				out.close();
			} catch (IOException ex) {
//				JOptionPane.showMessageDialog(new JFrame(),
//						"unable to write file", "PSI-Tab makers: Tab Maker",
//						JOptionPane.ERROR_MESSAGE);
				xsdTree.getMessageManager().sendMessage("unable to write file", MessageManagerInt.errorMessage);
			}
		}
	}

	public class CheckListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
//			ArrayList paths = xsdTree.check((XsdNode) xsdTree.treeModel
//					.getRoot());
//			String errors = paths.size() + " errors found: \n";
//			for (int i = 0; i < paths.size(); i++) {
//				errors += paths.get(i) + "\n";
//			}
//
//			JOptionPane.showMessageDialog(new JFrame(), errors,
//					"[PSI makers: flattener] checking associations",
//					JOptionPane.ERROR_MESSAGE);
		}
	}

	public class SetSeparatorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String s = (String) JOptionPane.showInputDialog(new JFrame(),
					"Field separator, \n");

			if (s != null)
				((XsdTreeStructImpl) xsdTree).setSeparator(s);
		}
	}

	/**
	 * used to display informations about the node selected
	 * 
	 * @author arnaud
	 */
	public class InfosListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) xsdTree.tree
					.getLastSelectedPathComponent();

			if (node == null) {
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
				return;
			}

			xsdTree.getMessageManager().sendMessage(xsdTree.getInfos(node), MessageManagerInt.simpleMessage);
//			JEditorPane editorPane = new JEditorPane();
//			editorPane.setEditable(false);
//			editorPane.setText(xsdTree.getInfos(node));
//
//			JScrollPane scrollPane = new JScrollPane(editorPane);
//			scrollPane
//					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//
//			JOptionPane.showMessageDialog(new JFrame(), scrollPane);
		}
	}


	public class PreviewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			updatePreview();
		}
	}
	
	
	/**
	 * used for loading a Xml schema
	 */
	public class LoadSchemaListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			loadSchema();
			if (xsdTree.schema != null)
				((XsdTreeStructImpl) xsdTree).setXmlRoot();
		}
	}

	/**
	 * use to select the node that will describe a line of the flat file
	 */
	public class SelectLineNodeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			/** TODO: commented : no more dcument necessary for selection */
			//            if (((XsdTreeStructImpl) xsdTree).document == null) {
			//                xsdTree.getMessageManager().sendMessage("Please load a XML document first",
			//                        "[PSI makers: flattener] node selection for a line");
			//                return;
			//            }
			try {
				xsdTree.emptySelectionLists();
				((XsdTreeStructImpl) xsdTree).lineNode = (XsdNode) xsdTree.tree
						.getLastSelectedPathComponent();

				((XsdTreeStructImpl) xsdTree)
						.setLineNode(((XsdTreeStructImpl) xsdTree).lineNode);
			} catch (NullPointerException npe) {
				xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
			}
			exampleLine = null;
			updatePreview();
		}
	}

	public class TreeMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			XsdNode selectedNode = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

//			String value;
			if (((XsdTreeStructImpl) xsdTree).elementFilters
					.containsKey(selectedNode)) {
				filter
						.setText((String) ((XsdTreeStructImpl) xsdTree).elementFilters
								.get(selectedNode));
			} else {
				filter.setText("no filter");
			}
			if (e.getClickCount() == 2) {
				XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
				.getLastSelectedPathComponent();
				if (((XsdTreeStructImpl) xsdTree).selections.contains(node)) {
					unselectNode();
				} else {
					selectNode();
				}
				updatePreview();
			}
		}
	}

	/**
	 * the renderer for the tree
	 */
	public class XslTreeRenderer extends DefaultTreeCellRenderer {
		ImageIcon iconAttribute;

		ImageIcon iconElement;

		Font affected;

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
			if (((XsdTreeStructImpl) xsdTree).elementFilters.containsKey(node))
				setText(node.getName() + " *");
			else
				setText(node.getName());

			setForeground(Color.GRAY);
			
			if (node.isUsed)
				setForeground(Color.BLACK);

			if (((XsdTreeStructImpl) xsdTree).selections.contains(node))
				setForeground(Color.RED);

			if (node == ((XsdTreeStructImpl) xsdTree).lineNode)
				setForeground(Color.BLUE);

			if (node == ((XsdTreeStructImpl) xsdTree).lineNode)
				setForeground(Color.GREEN);

			return this;
		}
	}

	public class SelectNodeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			selectNode();
		}
	}

	public class UnselectNodeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			unselectNode();
		}
	}

	public void selectNode() {
		XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
				.getLastSelectedPathComponent();

		if (node == null) {
			xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
			return;
		}

		if (!((XsdTreeStructImpl) xsdTree)
				.canHaveValue((XsdNode) ((XsdTreeStructImpl) xsdTree).tree
						.getLastSelectedPathComponent())) {
			xsdTree.getMessageManager().sendMessage(
					"no value should  have been associated to this node", MessageManagerInt.errorMessage);
			return;
		}

		((XsdTreeStructImpl) xsdTree).selectNode(node);
		exampleLine = null;
		updatePreview();

	}

	public void updatePreview() {
		((XsdTreeStructImpl) xsdTree).firstElement = true;
		if (((XsdTreeStructImpl) xsdTree).document != null
				&& ((XsdTreeStructImpl) xsdTree).lineNode != null) {
			String preview = "";
			if (((XsdTreeStructImpl) xsdTree).getCurElementCount() > 0)
				preview += ((XsdTreeStructImpl) xsdTree).getCurElementCount()
						+ " elements found.\n";
			try {
				((XsdTreeStructImpl) xsdTree).resetCount();
				preview += ((XsdTreeStructImpl) xsdTree)
						.getTitle(((XsdTreeStructImpl) xsdTree).lineNode)
						+ "\n";
			} catch (OutOfMemoryError oome) {
				preview = "not enougth memory to perform preview\n";
			}
			if (displayExample.isSelected()) {
				try {
					((XsdTreeStructImpl) xsdTree).firstElement = true;
					if (((XsdTreeStructImpl) xsdTree).document != null
							&& ((XsdTreeStructImpl) xsdTree).lineElements
									.size() > 0) {
						if (exampleLine == null) {
							exampleLine = ((XsdTreeStructImpl) xsdTree)
									.marshallNode(
											((XsdTreeStructImpl) xsdTree).lineNode,
											(Element) ((XsdTreeStructImpl) xsdTree).lineElements
													.get(0));
						}
						preview += exampleLine;
					}
				} catch (IOException ioe) {
					preview += "no document loaded\n";
				} catch (OutOfMemoryError oome) {
					preview += "not enougth memory to perform preview\n";
				}
			}
			xsdTree.getMessageManager().sendMessage(preview, MessageManagerInt.simpleMessage);
		} 
		else if (displayExample.isSelected()){
			xsdTree.getMessageManager().sendMessage("no document loaded", MessageManagerInt.errorMessage);
		}
	}

	public void unselectNode() {
		XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
				.getLastSelectedPathComponent();

		if (node == null) {
			xsdTree.getMessageManager().sendMessage("no node selected", MessageManagerInt.errorMessage);
			return;
		}

		((XsdTreeStructImpl) xsdTree).unselectNode(node);
		exampleLine = null;
		updatePreview();
	}

	public void associateName(XsdNode node) {
		String value = (String) JOptionPane.showInputDialog(new JFrame(),
				"Enter the name, \n", node.getName());
		((XsdTreeStructImpl) xsdTree).addName(node, value);
		updatePreview();

		((XsdTreeStructImpl) xsdTree).treeModel.reload(node);
	}

	public void associateFilter(XsdNode node) {
		String oldValue = (String) (((XsdTreeStructImpl) xsdTree).elementFilters
				.get(node));
		if (oldValue == null)
			oldValue = "";
		String value = (String) JOptionPane.showInputDialog(new JFrame(),
				"Enter the name, \n", oldValue);

		((XsdTreeStructImpl) xsdTree).addFilter(node, value);
		exampleLine = null;
		updatePreview();
		((XsdTreeStructImpl) xsdTree).treeModel.reload(node);
	}


	public class AssociateFilterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			XsdNode node = (XsdNode) ((XsdTreeStructImpl) xsdTree).tree
					.getLastSelectedPathComponent();

			associateFilter(node);
			updatePreview();
		}
	}

}