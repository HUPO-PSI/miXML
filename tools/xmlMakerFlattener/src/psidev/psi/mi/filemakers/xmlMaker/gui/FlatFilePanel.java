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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import psidev.psi.mi.filemakers.xmlMaker.structure.FlatFile;
import psidev.psi.mi.filemakers.xsd.Utils;


/**
 * The class provide graphical management for a tab delimited file <br>
 * It allows to load a file, choose the field delimitor, and recursively enter
 * into the fields and split them.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class FlatFilePanel extends JPanel {

	public String lastVisitedDirectory = ".";

	/**
	 * 
	 * @uml.property name="flatFile"
	 * @uml.associationEnd
	 * @uml.property name="flatFile" multiplicity="(1 1)"
	 */
	public FlatFile flatFile = new FlatFile();

	public class skipFirstLineListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			setSkipFirstLine();
		}
	}

	/**
	 * used to set the separator
	 */
	public class setSeparatorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String s = (String) JOptionPane.showInputDialog(new JFrame(
					"[PSI makers: PSI maker] Flat File"),
					"Separator (use regular expression, e.g.: ; ;|:|, \\| ",
					flatFile.getSeparator(currentPath));
			if (s != null)
				try {
					flatFile.setSeparator(currentPath, s);
					//					if (flatFile.line != null) {
					updateList();
					separatorLbl.setText(s);
					//					}
				} catch (java.util.regex.PatternSyntaxException ex) {
					JOptionPane
							.showMessageDialog(
									new JFrame(),
									"the separator specified is not a valid regular expression.",
									"Separator", JOptionPane.ERROR_MESSAGE);
				}
		}
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			try {
				setToolTipText(value.toString());
				String s = value.toString();
				setText(s);
			} catch (NullPointerException npe) {
				/* no value */
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}


	public String currentPath = "";

	/**
	 * 
	 * @uml.property name="listModel"
	 * @uml.associationEnd
	 * @uml.property name="listModel" multiplicity="(0 -1)"
	 *               elementType="java.lang.String"
	 */
	public DefaultListModel listModel = new DefaultListModel();

	/**
	 * 
	 * @uml.property name="listPanel"
	 * @uml.associationEnd
	 * @uml.property name="listPanel" multiplicity="(1 1)"
	 */
	public JPanel listPanel = new JPanel(new BorderLayout());

	/**
	 * 
	 * @uml.property name="list"
	 * @uml.associationEnd
	 * @uml.property name="list" multiplicity="(1 1)"
	 */
	public JList list = new JList(listModel);

	/** associate a path to a FlatFilePanel */

	public FlatFilePanel() {
		super(new BorderLayout());

		separatorLbl.setEditable(false);

		Box buttonsPanel = new Box(BoxLayout.Y_AXIS);

		Box nextLineBox = new Box(BoxLayout.X_AXIS);
		nextLineBox.setBorder(new TitledBorder(""));
		JPanel cellBox = new JPanel(new GridLayout(3, 2));

		JButton firstLineb = new JButton("<<");
		Utils.setDefaultSize(firstLineb);
		firstLineb.addActionListener(new firstLineListener());

		nextLineBox.add(firstLineb);

		JButton nextLineb = new JButton(">");
		Utils.setDefaultSize(nextLineb);
		nextLineb.addActionListener(new nextLineListener());

		nextLineBox.add(nextLineb);

		JButton nextLineWithFieldb = new JButton(">>");
		Utils.setDefaultSize(nextLineWithFieldb);
		nextLineWithFieldb.addActionListener(new nextLineWithFieldListener());

		nextLineBox.add(nextLineWithFieldb);

		/* list */
		list.setFixedCellHeight(12);
		list.setFixedCellWidth(60);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setAutoscrolls(true);
		list.setCellRenderer(new MyCellRenderer());
		list.setVisible(true);

		Box listButtonsPanel = new Box(BoxLayout.X_AXIS);
		list.setVisible(true);

		JButton fileb = new JButton("Open");
		Utils.setDefaultSize(fileb);
		fileb.addActionListener(new loadFileListener());
		cellBox.add(fileb);
		skipFirstLineb.addItemListener(new skipFirstLineListener());
		cellBox.add(skipFirstLineb);

		JButton setSeparatorb = new JButton("Separator");
		Utils.setDefaultSize(setSeparatorb);
		setSeparatorb.addActionListener(new setSeparatorListener());
		cellBox.add(setSeparatorb);
		cellBox.add(separatorLbl);

		JButton enterb = new JButton("Split");
		Utils.setDefaultSize(enterb);
		enterb.addActionListener(new enterListener());

		cellBox.add(enterb);

		JButton backb = new JButton("Back");
		Utils.setDefaultSize(backb);
		backb.addActionListener(new backListener());

		cellBox.add(backb);

		buttonsPanel.add(cellBox);
		buttonsPanel.add(nextLineBox);

		JScrollPane scrollList = new JScrollPane(list);
		listPanel.add(scrollList, BorderLayout.CENTER);
		listPanel.add(listButtonsPanel, BorderLayout.SOUTH);
		add(listPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * load a flat file. The file is choosed by the user in an option panel
	 */
	public void loadFile() throws IOException {
		JTextField separator = new JTextField();
		JTextField fileName = new JTextField();
		JCheckBox forgetFirstLine = new JCheckBox();
		try {
			Box panel = new Box(BoxLayout.Y_AXIS);

			String defaultDirectory = Utils.lastVisitedDirectory;
			if (Utils.lastVisitedFlatFileDirectory != null)
				defaultDirectory = Utils.lastVisitedFlatFileDirectory;

			JFileChooser fc = new JFileChooser(defaultDirectory);

			panel.add(new JLabel("Line separator"));
			panel.add(separator);
			panel.add(new JLabel("first line for titles (do not parse it)"));
			panel.add(forgetFirstLine);
			fc.setAccessory(panel);

			int returnVal = fc.showOpenDialog(new JFrame());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			if (separator != null && separator.getText().length() != 0) {
				flatFile.lineSeparator = separator.getText();
			} else {
				flatFile.lineSeparator = null;
			}

			flatFile.firstLineForTitles = forgetFirstLine.isSelected();
			URL fileURL = fc.getSelectedFile().toURL();

			flatFile.load(fileURL);

			Utils.lastVisitedDirectory = fileURL.getPath();
			Utils.lastVisitedFlatFileDirectory = fileURL.getPath();

			updateList();
			skipFirstLineb.setSelected(flatFile.firstLineForTitles());
		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(new JFrame(),
					"Unable to load the file",
					"[PSI makers: PSI maker] Flat File",
					JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException ioe) {
			JOptionPane.showMessageDialog(new JFrame(),
					"Unable to load the file",
					"[PSI makers: PSI maker] Flat File",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateList() {
		String separator = flatFile.getSeparator(currentPath);
		if (separator == null) {
			listModel.removeAllElements();
			listModel.addElement(flatFile.getElementAt(currentPath, null));
		} else {
			String[] l;
			l = flatFile.getElementAt(currentPath, null).split(separator);
			int i = 0;
			while (i < l.length) {
				if (i < listModel.getSize()) {
					listModel.set(i, l[i].trim());
					i++;
				} else {
					listModel.addElement(l[i].trim());
					i++;
				}
			}
			while (i < listModel.getSize()) {
				listModel.set(i, "");
				/* if I had set it to null, it would be seen on the list */
				i++;
			}
		}
		separatorLbl.setText(flatFile.getSeparator(currentPath));
	}

	/**
	 * used for loading a file
	 */
	public class loadFileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				loadFile();
			} catch (IOException urie) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Unable to load file",
						"[PSI makers: PSI maker] load flat file",
						JOptionPane.ERROR_MESSAGE);
				/** TODO: manage exception */
			}
		}
	}

	/**
	 * used for reading a new line and update the lists
	 */
	public class nextLineListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			flatFile.nextLine();
			updateList();
			if (!flatFile.hasLine()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"No more line to read",
						"[PSI makers: PSI maker] Flat File",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public class nextLineWithFieldListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (list.getSelectedIndex() == -1)
				return;

			if (currentPath == "")
				flatFile.nextLineWithField("" + list.getSelectedIndex());
			else
				flatFile.nextLineWithField(currentPath + "."
						+ list.getSelectedIndex());

			updateList();
			if (!flatFile.hasLine()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"No more line to read",
						"[PSI makers: PSI maker] Flat File",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * used for reloading the file and display the first line and update the
	 * lists
	 */
	public class firstLineListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				flatFile.reload();
				flatFile.nextLine();
				updateList();
			} catch (Exception fme) {
				/** TODO : manage it */
			}
		}
	}

	/**
	 * used to set the line separator
	 */
	public class setLineSeparatorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String s = (String) JOptionPane
					.showInputDialog(
							new JFrame("[PSI makers: PSI maker] Flat File"),
							"Line Separator (use regular expression, e.g.: // \n",
							"//");

			if (s != null)
				flatFile.lineSeparator = s;
			updateList();
		}
	}

	public String getSelectedPath() {
		if (currentPath == "" && list.getSelectedIndex() == -1)
			return "";
		if (currentPath == "")
			return "" + list.getSelectedIndex();
		if (list.getSelectedIndex() == -1)
			return currentPath;
		return currentPath + "." + list.getSelectedIndex();
	}

	/**
	 * used to create a new list with selected cell
	 */
	public class enterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentPath == "")
				currentPath += list.getSelectedIndex();
			else
				currentPath += "." + list.getSelectedIndex();
			updateList();
		}
	}

	/**
	 * use to go back to parent cell
	 */
	public class backListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentPath.indexOf(".") == -1) {
				currentPath = "";
				updateList();
				return;
			}
			currentPath = currentPath
					.substring(0, currentPath.lastIndexOf("."));
			updateList();
			//		
			//			if (listPanel.flatList != flatFile.rootList) {
			//				listPanel.setVisible(false);
			//				listPanel.flatList = listPanel.flatList.getParentList();
			//				add(listPanel, BorderLayout.CENTER);
			//				listPanel.setVisible(true);
			//			}
		}
	}

	public JTextField separatorLbl = new JTextField(3);

	JCheckBox skipFirstLineb = new JCheckBox("skip first line");

	public void setSkipFirstLine() {
		flatFile.setFirstLineForTitles(skipFirstLineb.isSelected());
	}

}