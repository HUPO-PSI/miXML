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

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JTextPaneMessageManager extends JTextPane implements
		MessageManagerInt {

	public void sendMessage(String message, int type) {
		StyledDocument doc = (StyledDocument) this.getDocument();
		Style style;
		try {
			if (type == errorMessage) {
				style = doc.addStyle("error", null);
				StyleConstants.setForeground(style, Color.RED);
				// messagesPane.setForeground(Color.RED);
				message = "[ERROR]   " + message;
			} else if (type == warningMessage) {
				style = doc.addStyle("error", null);
				StyleConstants.setForeground(style, Color.ORANGE);
				// messagesPane.setForeground(Color.RED);
				message = "[WARNING] " + message;
			} else {
				style = doc.addStyle("simple", null);
				StyleConstants.setForeground(style, Color.BLUE);
				// messagesPane.setForeground(Color.RED);
				// messagesPane.setForeground(Color.BLUE);
			}

			this.getDocument().insertString(this.getDocument().getLength(),
					message + "\n", style);
			this.setCaretPosition(this.getDocument().getLength());
		} catch (BadLocationException e) {
		}
	}
	
	public JTextPaneMessageManager() {
		
	}
	

}
