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
package psidev.psi.mi.filemakers.xsd;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.Annotation;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.Documentation;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.Structure;

/**
 * This Class creates and manages a Panel for displaying and managing the
 * XsdTreeStruct
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */

public abstract class AbstractXsdTreePanel extends JPanel {
	public AbstractXsdTreeStruct xsdTree;

	public void loadSchema(File file) throws FileNotFoundException, IOException {
		xsdTree.loadSchema(file);

		ToolTipManager.sharedInstance().registerComponent(xsdTree.tree);
		setCellRenderer();
		xsdTree.tree.addTreeSelectionListener(new XsdTreeSelectionListener());
//		xsdTree.tree.setShowsRootHandles(true);
	}

	public JScrollPane scrollPane;

	/**
	 * Returns an instance of <code>AbstractXslTree</code>
	 * 
	 * @param autoduplicate
	 *            indicates that new nodes will be automaticly created according
	 *            to the minimum defined in the schema (minOccurs)
	 */
	public AbstractXsdTreePanel(AbstractXsdTreeStruct xsdTree, String position) {
		super(new BorderLayout());

		this.xsdTree = xsdTree;
		xsdTree.tree = new JTree(xsdTree.treeModel);
		scrollPane = new JScrollPane(xsdTree.tree);
		
		add(scrollPane, BorderLayout.CENTER);
		add(getButtonPanel(), position);
	}

	/**
	 * Returns an instance of <code>AbstractXslTree</code>
	 * 
	 * @param autoduplicate
	 *            indicates that new nodes will be automaticly created according
	 *            to the minimum defined in the schema (minOccurs)
	 */
	public AbstractXsdTreePanel(AbstractXsdTreeStruct xsdTree) {
		super(new BorderLayout());

		this.xsdTree = xsdTree;
		xsdTree.tree = new JTree(xsdTree.treeModel);
		
		scrollPane = new JScrollPane(xsdTree.tree);
		
		scrollPane.setPreferredSize(new Dimension(300,300));
		add(scrollPane, BorderLayout.CENTER);
	}	
	
	
	
	/**
	 * create a panel for buttons: her have to be added every buttons such as
	 * the ones that allows to load a schema
	 *  
	 */
	public abstract Box getButtonPanel();

	/**
	 * open a file chooser allowing to choose the file containing the schema,
	 * read it, create the tree, and finaly reinitialize everything using a
	 * previous tree. Those reinitialisations have to be defined in the
	 * <code>emptySelectionLists</code> method
	 */
	public void loadSchema() {
		//		try {

		String defaultDirectory = Utils.lastVisitedDirectory;
		if (Utils.lastVisitedSchemaDirectory != null)
			defaultDirectory = Utils.lastVisitedSchemaDirectory;

		JFileChooser fileChooser = new JFileChooser(defaultDirectory);
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			loadSchema(fileChooser.getSelectedFile());
			xsdTree.getMessageManager().sendMessage("XML schema "
					+ fileChooser.getSelectedFile().getName()
					+ " successfully loaded.", MessageManagerInt.simpleMessage);
		} catch (FileNotFoundException fe) {
			xsdTree.getMessageManager().sendMessage("File " + fileChooser.getSelectedFile().getName()
					+ " not fount", MessageManagerInt.errorMessage);
		} catch (IOException ioe) {
			xsdTree.getMessageManager().sendMessage("Unable to load file "
					+ fileChooser.getSelectedFile().getName(), MessageManagerInt.errorMessage);
		}
	}



	/**
	 * when a node is clicked by the mouse, deploy it using th
	 * <code>extendPath</code> method
	 */
	public class XsdTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			XsdNode node = (XsdNode) xsdTree.tree
					.getLastSelectedPathComponent();
			if (node == null)
				return;

			if (node.isExtended)
				return;
			try {
				xsdTree.extendPath(node);
			} catch (Exception aoobe) {
				/* a choice has to be done */
				Annotated annotated = (Annotated) (node.getUserObject());
				String path = xsdTree.getPathForNode(node);
//				if (annotated.getStructureType() == Structure.GROUP) {
					/* if it's a complex type, look inside */
					Group g = (Group) annotated;
				
				
				ArrayList choices = xsdTree.getChoices(g);
				ArrayList possibilities = new ArrayList();

				for (int i = 0; i < choices.size(); i++) {
					try {
						possibilities.add(((ElementDecl) choices.get(i))
								.getName());
					} catch (ClassCastException cce) {
						/* a group: give an overview */
						possibilities.add(XsdNode
								.choiceToString((Group) choices.get(i)));
					}
				}

				String choice = (String) JOptionPane.showInputDialog(
						new JFrame(),
						"what type of element do you want to add?",
						"[PSI makers] choice", JOptionPane.QUESTION_MESSAGE,
						new ImageIcon("images/ic-att.gif"), possibilities
								.toArray(), "");

				if ((choice == null) || (choice.length() == 0))
					return;

				xsdTree.expendChoices.add(path);
				xsdTree.expendChoices.add(choice);
				

//				xsdTree.treeModel.reload((XsdNode) xsdTree.treeModel.getRoot());
				
				xsdTree.extendPath(node);
				xsdTree.check((XsdNode) xsdTree.treeModel.getRoot());

			}
		}
	}

	/**
	 * create the tree according to the schema loaded. The root node is
	 * displayed
	 *  
	 */
	public void createTree() {
		xsdTree.createTree();
		ToolTipManager.sharedInstance().registerComponent(xsdTree.tree);
		setCellRenderer();
		xsdTree.tree.addTreeSelectionListener(new XsdTreeSelectionListener());
		xsdTree.tree.setShowsRootHandles(true);
	}

	/**
	 * the cell renderer used by the constructor
	 *  
	 */
	public abstract void setCellRenderer();

	/**
	 * @author arnaud
	 * 
	 * The renderer for the tree
	 */
	public class XsdTreeRenderer extends DefaultTreeCellRenderer {
		ImageIcon iconAttribute;

		ImageIcon iconElement;

		Font affected;

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
				setText(getText()
						+ " ("
						+ ((AttributeDecl) node.getUserObject())
								.getSimpleType().getName() + ")");
				break;
			case Structure.ELEMENT:
				setText(getText()
						+ ".... ("
						+ ((ElementDecl) node.getUserObject()).getType()
								.getName() + ")");
			}

			return this;
		}
	}

//	public void redoChoice(String path, String choice) {
//		XsdNode currentNode = xsdTree.rootNode;
//		/* never a choice on rootNode */
//		String nextIndexes = path.substring(path.indexOf(".") + 1);
//		int index = 0;
//
//		/* for each element on the path */
//		while (nextIndexes.length() > 0) {
//			/* if choice do it */
//			Annotated annotated = (Annotated) (currentNode.getUserObject());
//			if (annotated.getStructureType() == Structure.GROUP) {
//				/* if it's a complex type, look inside */
//
//				Group g = (Group) annotated;
//				try {
//					if (g.getOrder().getType() == Order.CHOICE
//							&& xsdTree.manageChoices) {
//						XsdNode parent = (XsdNode) currentNode.getParent();
//						int position = parent.getIndex(currentNode);
//						ArrayList choices = xsdTree.getChoices(g);
//						ArrayList possibilities = new ArrayList();
//						for (int i = 0; i < choices.size(); i++) {
//							try {
//								possibilities
//										.add(((ElementDecl) choices.get(i))
//												.getName());
//							} catch (ClassCastException e) {
//								/* a group: give an overview */
//								possibilities
//										.add(XsdNode
//												.choiceToString((Group) choices
//														.get(i)));
//							}
//						}
//						((XsdNode) currentNode.getParent()).remove(parent
//								.getIndex(currentNode));
//						XsdNode newNode;
//						newNode = new XsdNode((Annotated) choices
//								.get(possibilities.indexOf(choice)));
//						newNode.isRequired = currentNode.isRequired;
//						newNode.min = currentNode.min;
//						newNode.max = currentNode.max;
//						newNode.originalParent = currentNode;
//						parent.insert(newNode, position);
//						currentNode = newNode;
//					} else {
//						/* if not extended, do it */
//						if (!currentNode.isExtended) {
//							xsdTree.extendPath(currentNode);
//						} else {
//							index = Integer.parseInt(nextIndexes.substring(0,
//									nextIndexes.indexOf(".")));
//							nextIndexes = nextIndexes.substring(nextIndexes
//									.indexOf(".") + 1);
//							currentNode = (XsdNode) currentNode
//									.getChildAt(index);
//						}
//					}
//				} catch (StringIndexOutOfBoundsException e) {
//					return;
//				}
//			} else {
//				/* if not extended, do it */
//				if (!currentNode.isExtended) {
//					xsdTree.extendPath(currentNode);
//				} else {
//					if (nextIndexes.indexOf(".") >= 0) {
//						index = Integer.parseInt(nextIndexes.substring(0,
//								nextIndexes.indexOf(".")));
//						nextIndexes = nextIndexes.substring(nextIndexes
//								.indexOf(".") + 1);
//						currentNode = (XsdNode) currentNode.getChildAt(index);
//					} else {
//
//						index = Integer.parseInt(nextIndexes);
//						nextIndexes = "-1";
//						try {
//							currentNode = (XsdNode) currentNode
//									.getChildAt(index);
//						} catch (ArrayIndexOutOfBoundsException e) {
//							return;
//						}
//					}
//				}
//			}
//		}
//	}


	public void reload() {
		xsdTree.treeModel.reload();
	}

	/**
	 * check elements and attributes for the structure contained in this node
	 * create nodes and add'em to the tree
	 */
//	public void extendPath(XsdNode node) {
//		Annotated annotated = (Annotated) (node.getUserObject());
//		String path = xsdTree.getPathForNode(node);
//		if (annotated.getStructureType() == Structure.GROUP) {
//			/* if it's a complex type, look inside */
//			Group g = (Group) annotated;
//
////			XsdNode parent = (XsdNode) node.getParent();
//
//			/*
//			 * if a sequence: add all childs if a choice, ask user
//			 */
//			if (g.getOrder().getType() == Order.CHOICE && xsdTree.manageChoices) {
//				ArrayList choices = xsdTree.getChoices(g);
//				ArrayList possibilities = new ArrayList();
//
//				for (int i = 0; i < choices.size(); i++) {
//					try {
//						possibilities.add(((ElementDecl) choices.get(i))
//								.getName());
//					} catch (ClassCastException e) {
//						/* a group: give an overview */
//						possibilities.add(XsdNode
//								.choiceToString((Group) choices.get(i)));
//					}
//				}
//
//				String choice = (String) JOptionPane.showInputDialog(
//						new JFrame(),
//						"what type of element do you want to add?",
//						"[PSI makers] choice", JOptionPane.QUESTION_MESSAGE,
//						new ImageIcon("images/ic-att.gif"), possibilities
//								.toArray(), "");
//
//				if ((choice == null) || (choice.length() == 0))
//					return;
//
//				xsdTree.expendChoices.add(path);
//				xsdTree.expendChoices.add(choice);
//			} else if (g.getOrder().getType() == Order.CHOICE) {
//				xsdTree.expendChoices.add(path);
//			}
//		}
//		xsdTree.extendPath(node);
//		xsdTree.check((XsdNode) xsdTree.treeModel.getRoot());
//		xsdTree.treeModel.reload((XsdNode) xsdTree.treeModel.getRoot());
//	}

}