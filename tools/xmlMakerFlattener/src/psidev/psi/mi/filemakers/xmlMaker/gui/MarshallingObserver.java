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
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import psidev.psi.mi.filemakers.xmlMaker.structure.MarshallingObservable;
import psidev.psi.mi.filemakers.xsd.Utils;


/**
 * This class can observe a tree and display information about the processed
 * marshalling
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class MarshallingObserver extends JFrame implements Observer, Runnable {
	JTextArea editorPane = new JTextArea();

	JTextField linePane = new JTextField();

	private MarshallingObservable observable;

	public MarshallingObserver() {
		super("make XML document");
		setSize(400, 300);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorPane.setText("Start marshalling");
		editorPane.setEditable(false);
		editorPane.setLineWrap(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(linePane, BorderLayout.SOUTH);
		setVisible(true);
	}

	public void update(Observable arg0, Object arg1) {
		linePane.setText(Utils.getFileName(((MarshallingObservable) arg0)
				.getCurrentFlatFile())
				+ ", line "
				+ new Integer(((MarshallingObservable) arg0).getCurrentLine())
						.toString());
		if (arg1 == ((MarshallingObservable) arg0).getMessage()) {
			editorPane.append("\n"
					+ ((MarshallingObservable) arg0).getMessage());
		}
		if (arg1 == ((MarshallingObservable) arg0).getElement()) {
			editorPane.append("\n");
			for (int i = 0; i < ((MarshallingObservable) arg0).getIndentation(); i++) {
				editorPane.append(" ");
			}
			editorPane.append(((MarshallingObservable) arg0).getElement()
					+ ": ");
			editorPane.append(Utils.getFileName(((MarshallingObservable) arg0)
					.getCurrentFlatFile()));
		}
		this.update(this.getGraphics());
	}

	/**
	 * @param observable
	 *            The observable to set.
	 */
	public void setObservable(MarshallingObservable observable) {
		this.observable = observable;
	}

	public void start() {
		(new Thread(this)).start();
	}

	public void run() {
		(new Thread(this)).start();
	}
}