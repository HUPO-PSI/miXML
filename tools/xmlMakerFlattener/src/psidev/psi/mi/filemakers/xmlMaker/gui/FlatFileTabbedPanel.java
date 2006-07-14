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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import psidev.psi.mi.filemakers.xmlMaker.structure.FlatFile;
import psidev.psi.mi.filemakers.xmlMaker.structure.FlatFileContainer;
import psidev.psi.mi.filemakers.xsd.Utils;


/**
 * This class displayed a TabbedPane that contains the TabFiles
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class FlatFileTabbedPanel extends JPanel {

	public JTextField fileLbl = new JTextField(15);

	/**
	 * receive tabFiles each tabFile is associated with a tab
	 * 
	 * @uml.property name="tabbedPane"
	 * @uml.associationEnd
	 * @uml.property name="tabbedPane" multiplicity="(1 1)"
	 */
	public JTabbedPane tabbedPane = new JTabbedPane();

	/**
	 * 
	 * @uml.property name="flatFileContainer"
	 * @uml.associationEnd
	 * @uml.property name="flatFileContainer" multiplicity="(1 1)"
	 */
	public FlatFileContainer flatFileContainer;

	/**
	 * 
	 * @uml.property name="flatFilePanel"
	 * @uml.associationEnd
	 * @uml.property name="flatFilePanel" multiplicity="(1 1)"
	 */
	public FlatFilePanel flatFilePanel;

	/**
	 * returns a new instance of TabFileTabbedPanel with one tab displaying an
	 * empty FlatList
	 */
	public FlatFileTabbedPanel(FlatFileContainer flatFiles) {
		this.setLayout(new BorderLayout());
		this.flatFileContainer = flatFiles;

		fileLbl.setEditable(false);

		Box buttonBox = new Box(BoxLayout.X_AXIS);

		JButton addTabb = new JButton("New");
		Utils.setDefaultSize(addTabb);
		addTabb.addActionListener(new addFlatFileListener());

		buttonBox.add(addTabb);
		buttonBox.add(fileLbl);

		flatFilePanel = new FlatFilePanel();
		tabbedPane.add(flatFilePanel);
		flatFileContainer.addFlatFile(flatFilePanel.flatFile);
		add(tabbedPane, BorderLayout.CENTER);
		add(buttonBox, BorderLayout.NORTH);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				FlatFilePanel curentFlatFile = (FlatFilePanel) tabbedPane
						.getSelectedComponent();
				if (curentFlatFile != null) {
					try {
						fileLbl.setText(Utils.relativizeURL(
								curentFlatFile.flatFile.fileURL).getPath());
					} catch (NullPointerException npe) {
						/** TODO: manage exception */
					}
				}
			}
		};

		tabbedPane.addMouseListener(mouseListener);

	}

	/**
	 * used to load a new flat file
	 */
	public class addFlatFileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			FlatFilePanel ffp = new FlatFilePanel();
			tabbedPane.add(ffp);
			flatFileContainer.addFlatFile(ffp.flatFile);
		}
	}

	/**
	 * get the path for selected cell in a list, i.e. indexOfTab.pathInTheList
	 * 
	 * @return a string representing the path, i.e.
	 *         indexOfTab.indexOfCellInRootList.IndexOfCellInSubList.[]
	 */
	public String getSelectedPath() {
		return tabbedPane.getSelectedIndex()
				+ "."
				+ ((FlatFilePanel) tabbedPane.getSelectedComponent())
						.getSelectedPath();
	}

	/**
	 * get the index of the tab sellected
	 * 
	 * @return the index of the tab sellected
	 */
	public int getSelectedIndex() {
		return tabbedPane.getSelectedIndex();
	}

	public FlatFile getSelectedFlatFile() {
		return ((FlatFilePanel) tabbedPane
				.getComponent(this.getSelectedIndex())).flatFile;
	}

	public FlatFilePanel getSelectedFlatFilePanel() {
		return (FlatFilePanel) tabbedPane.getComponent(this.getSelectedIndex());
	}

	/**
	 * get the FlatFile contained by the indexth tab
	 * 
	 * @param index
	 *            the index of a tab
	 * @return the FlatFile contained by the indexth tab
	 */
	public FlatFile getFlatFileByIndex(int index) {
		return ((FlatFilePanel) tabbedPane.getComponent(index)).flatFile;
	}

	/**
	 * give a name to a tab
	 *  
	 */
	public void setTabTitle(int index, String name) {
		tabbedPane.setTitleAt(index, name);
	}

	/**
	 * read again the container (from structure) and rebuilt the tabbedPane and
	 * the FlatFilePanel
	 */
	public void reload() {
		tabbedPane.removeAll();
		for (int i = 0; i < flatFileContainer.flatFiles.size(); i++) {
			FlatFilePanel ffp = new FlatFilePanel();
			ffp.flatFile = flatFileContainer.getFlatFile(i);
			ffp.updateList();
			tabbedPane.add(ffp);
		}
	}

}