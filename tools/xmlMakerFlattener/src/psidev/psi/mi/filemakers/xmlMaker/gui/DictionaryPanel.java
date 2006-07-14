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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
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
import javax.swing.border.TitledBorder;

import psidev.psi.mi.filemakers.xmlMaker.structure.Dictionary;
import psidev.psi.mi.filemakers.xmlMaker.structure.DictionaryContainer;
import psidev.psi.mi.filemakers.xsd.Utils;


/**
 * Display a list of the dictionnary and allows to load or select them
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class DictionaryPanel extends JPanel {

	/**
	 * 
	 * @uml.property name="listModel"
	 * @uml.associationEnd
	 * @uml.property name="listModel" multiplicity="(0 -1)"
	 *               elementType="mint.filemakers.xmlMaker.structure.Dictionary"
	 */
	public DefaultListModel listModel = new DefaultListModel();

	public JTextField separatorLbl = new JTextField(3);

	JCheckBox caseSensitiveb = new JCheckBox("case sensitive");

	/**
	 * Load a dictionnary. The dictionnary is load from a file specified in an
	 * OptionPanel
	 */
	public void editDictionnary() throws IOException {

		if (list.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(new JFrame(),
					"No dictionnary selected", "[PSI makers: PSI maker]",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Dictionary curentDictionnary = (Dictionary) list.getSelectedValue();
		JTextField separator = new JTextField(curentDictionnary.getSeparator());
		JTextField fileName = new JTextField(curentDictionnary.getFileURL()
				.getPath());
		JCheckBox caseSensitive = new JCheckBox();
		caseSensitive.setSelected(curentDictionnary.isCaseSensitive());

		try {
			Box panel = new Box(BoxLayout.Y_AXIS);

			String defaultDirectory = Utils.lastVisitedDirectory;
			if (Utils.lastVisitedDictionaryDirectory != null)
				defaultDirectory = Utils.lastVisitedDictionaryDirectory;

			JFileChooser fc = new JFileChooser(defaultDirectory);

			fc.setSelectedFile(new File(curentDictionnary.getFileURL()
					.getPath()));
			panel.add(new JLabel("Separator"));
			panel.add(separator);
			panel.add(new JLabel("Case sensitive"));
			panel.add(caseSensitive);
			fc.setAccessory(panel);

			int returnVal = fc.showOpenDialog(new JFrame());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			URL url = fc.getSelectedFile().toURL();

			Dictionary newDico = new Dictionary(url, separator.getText(),
					caseSensitive.isSelected());

			listModel.setElementAt(newDico, list.getSelectedIndex());

			caseSensitiveb.setSelected(newDico.isCaseSensitive());
			separatorLbl.setText(newDico.getSeparator());

		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(new JFrame(), "Unable to load file",
					"[PSI makers: PSI maker] load dictionnary",
					JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException npe) {
			JOptionPane.showMessageDialog(new JFrame(), "Unable to load file",
					"[PSI makers: PSI maker] load dictionnary",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 
	 * @uml.property name="list"
	 * @uml.associationEnd
	 * @uml.property name="list" multiplicity="(0 -1)"
	 *               elementType="mint.filemakers.xmlMaker.structure.Dictionary"
	 */
	public JList list = new JList(listModel);


	/**
	 * 
	 * @uml.property name="dictionaries"
	 * @uml.associationEnd
	 * @uml.property name="dictionaries" multiplicity="(1 1)"
	 */
	public DictionaryContainer dictionaries;

	public DictionaryPanel(DictionaryContainer dictionaries) {
		super(new BorderLayout());
		separatorLbl.setEditable(false);

		this.dictionaries = dictionaries;

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Dictionary curentDictionnary = (Dictionary) list
						.getSelectedValue();
				if (curentDictionnary != null) {
					caseSensitiveb.setSelected(curentDictionnary.caseSensitive);
					separatorLbl.setText(curentDictionnary.getSeparator());
				}
			}
		};

		list.addMouseListener(mouseListener);

		Box buttonsPanel = new Box(BoxLayout.Y_AXIS);
		buttonsPanel.setBorder(new TitledBorder(""));

		Box line1Panel = new Box(BoxLayout.X_AXIS);
		line1Panel.setBorder(new TitledBorder(""));

		Box line2Panel = new Box(BoxLayout.X_AXIS);
		line2Panel.setBorder(new TitledBorder(""));

		JButton newDicob = new JButton("New");
		Utils.setDefaultSize(newDicob);
		newDicob.addActionListener(new addDictionnaryListener());
		line1Panel.add(newDicob);

		JButton loadDicob = new JButton("Edit");
		Utils.setDefaultSize(loadDicob);
		loadDicob.addActionListener(new editDictionnaryListener());
		line1Panel.add(loadDicob);

		JButton displayDicob = new JButton("View");
		Utils.setDefaultSize(displayDicob);
		displayDicob.addActionListener(new displayALineListener());
		line1Panel.add(displayDicob);

		JButton separatorb = new JButton("Separator");
		Utils.setDefaultSize(separatorb);
		separatorb.addActionListener(new separatorListener());
		line2Panel.add(separatorb);

		line2Panel.add(separatorLbl);

		caseSensitiveb.addItemListener(new caseSensitiveListener());
		line2Panel.add(caseSensitiveb);

		buttonsPanel.add(line1Panel);
		buttonsPanel.add(line2Panel);

		list.setFixedCellHeight(10);
		list.setFixedCellWidth(60);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setAutoscrolls(true);
		list.setVisible(true);

		listModel.removeAllElements();
		JScrollPane scrollList = new JScrollPane(list);
		add(scrollList, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	/**
	 * index of the dictionnary delected in the list
	 * 
	 * @return index of the dictionnary curently selected on the list
	 */
	public int getSelectedDictionnary() {
		return list.getSelectedIndex();
	}

	public String[] getExampleList() {
		return ((Dictionary) listModel.elementAt(list.getSelectedIndex()))
				.exampleList();
	}

	/**
	 * use to load a new dictionnary and add it to the list
	 */
	public class editDictionnaryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				editDictionnary();
			} catch (IOException urie) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Unable to load file",
						"[PSI makers: PSI maker] load flat file",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * use to remove a dictionnary and add it to the list
	 */
	public class removeDictionnaryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			removeDictionnary();
		}
	}

	/**
	 * use to remove a dictionnary and add it to the list
	 */
	public class addDictionnaryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				addDictionnary();
				list.setSelectedIndex(list.getLastVisibleIndex());
			} catch (IOException urie) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Unable to load file",
						"[PSI makers: PSI maker] load flat file",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/**
	 * use to remove a dictionnary and add it to the list
	 */
	public class separatorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			editSeparator();
		}
	}

	public class caseSensitiveListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			setCaseSensitive();
		}
	}

	/**
	 * display a line from the dictionnary
	 */
	public class displayALineListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			displayALine();
		}
	}

	/**
	 * Load a dictionnary. The dictionnary is load from a file specified in an
	 * OptionPanel
	 */
	public void addDictionnary() throws IOException {
		JTextField separator = new JTextField();
		JTextField fileName = new JTextField();
		JCheckBox caseSensitive = new JCheckBox();

		try {
			Box panel = new Box(BoxLayout.Y_AXIS);

			String defaultDirectory = Utils.lastVisitedDirectory;
			if (Utils.lastVisitedDictionaryDirectory != null)
				defaultDirectory = Utils.lastVisitedDictionaryDirectory;

			JFileChooser fc = new JFileChooser(defaultDirectory);

			panel.add(new JLabel("Separator"));
			panel.add(separator);
			panel.add(new JLabel("Case sensitive"));
			panel.add(caseSensitive);
			fc.setAccessory(panel);

			int returnVal = fc.showOpenDialog(new JFrame());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			URL url = fc.getSelectedFile().toURL();

			Dictionary dico = new Dictionary(url, separator.getText(),
					caseSensitive.isSelected());
			if (dico != null) {
				dico.index = listModel.getSize();
				listModel.addElement(dico);
				/* add in the structure */
				dictionaries.addDictionary(dico);
			}
			caseSensitiveb.setSelected(dico.isCaseSensitive());
			separatorLbl.setText(dico.getSeparator());

		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(new JFrame(), "Unable to load file",
					"[PSI makers: PSI maker] load dictionnary",
					JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException npe) {
			JOptionPane.showMessageDialog(new JFrame(), "Unable to load file",
					"[PSI makers: PSI maker] load dictionnary",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Load a dictionnary. The dictionnary is load from a file specified in an
	 * OptionPanel
	 */
	public void removeDictionnary() {
		JTextField separator = new JTextField();
		JTextField fileName = new JTextField();
		JCheckBox caseSensitive = new JCheckBox();

		if (list.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(new JFrame(),
					"No dictionnary selected", "[PSI makers: PSI maker]",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		/* ask for confirmation */
		int confirm = JOptionPane
				.showConfirmDialog(
						new JFrame(),
						"All associations done to this dictionnary will be lost. Do you want to continue?",
						"Associatation of a dictionnary",
						JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		listModel.setElementAt("empty", list.getSelectedIndex());
	}

	/**
	 * Load a dictionnary. The dictionnary is load from a file specified in an
	 * OptionPanel
	 */
	public void editSeparator() {

		if (list.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(new JFrame(),
					"No dictionnary selected", "[PSI makers: PSI maker]",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Dictionary curentDictionnary = (Dictionary) list.getSelectedValue();
		JTextField separator = new JTextField(curentDictionnary.getSeparator());

		try {
			String s = (String) JOptionPane.showInputDialog(new JFrame(
					"[PSI makers: PSI maker] Flat File"),
					"Line Separator (use regular expression, e.g.: \\| \n",
					"\\|");

			if (s != null) {
				curentDictionnary.setSeparator(s);
				separatorLbl.setText(s);
			}

		} catch (NullPointerException npe) {
			JOptionPane.showMessageDialog(new JFrame(), "Unable to load file",
					"[PSI makers: PSI maker] load dictionnary",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setCaseSensitive() {

		if (list.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(new JFrame(),
					"No dictionnary selected", "[PSI makers: PSI maker]",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Dictionary curentDictionnary = (Dictionary) list.getSelectedValue();
		curentDictionnary.setCaseSensitive(caseSensitiveb.isSelected());
	}

	/**
	 * Display in a new frame a list of the data found in the line with the most
	 * elements
	 *  
	 */
	public void displayALine() {
		if (list.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(new JFrame(),
					"No dictionnary selected", "[PSI makers: PSI maker]",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String exampleLine = ((Dictionary) list.getSelectedValue())
				.exampleLine();
		String separator = ((Dictionary) list.getSelectedValue())
				.getSeparator();
		JList exampleList = new JList((exampleLine + " ").split(separator));

		JScrollPane scrollList = new JScrollPane(exampleList);

		JFrame frame = new JFrame();
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(new JLabel("example line: " + exampleLine));
		box.add(new JLabel("separator: " + separator + ", case sensitive: "
				+ ((Dictionary) list.getSelectedValue()).isCaseSensitive()));
		box.add(scrollList);
		frame.getContentPane().add(box);
		frame.setTitle("dictionnary: " + list.getSelectedValue());
//		frame.setSize(600, 300);
		frame.pack();
		frame.show();
	}

	public void reload() {
		listModel.clear();
		for (int i = 0; i < dictionaries.dictionaries.size(); i++) {
			listModel
					.addElement(((Dictionary) dictionaries.dictionaries.get(i)));
		}
	}

}