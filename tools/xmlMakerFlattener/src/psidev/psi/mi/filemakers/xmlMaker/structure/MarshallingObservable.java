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
package psidev.psi.mi.filemakers.xmlMaker.structure;

import java.util.Observable;

/**
 * @author arnaud
 *  
 */
public class MarshallingObservable extends Observable {
	public int currentLine = 0;

	public String currentFlatFile = "";

	public String element = "";

	public String message = "";

	/**
	 * number of flat file in the stack, in order to make visible when a flat
	 * file is parsed as a subelement of another file
	 */
	public int indentation = 0;

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
		setChanged();
		notifyObservers(this.message);
	}

	/**
	 * @return Returns the element.
	 */
	public String getElement() {
		return element;
	}

	/**
	 * @param element
	 *            The element to set.
	 */
	public void setElement(String element) {
		this.element = element;
		setChanged();
		notifyObservers(this.element);
	}

	/**
	 * @return Returns the currentFlatFile.
	 */
	public String getCurrentFlatFile() {
		return currentFlatFile;
	}

	/**
	 * @param currentFlatFile
	 *            The currentFlatFile to set.
	 */
	public void setCurrentFlatFile(String currentFlatFile) {
		this.currentFlatFile = currentFlatFile;
		setChanged();
		notifyObservers(this.currentFlatFile);
	}

	/**
	 * @return Returns the currentLine.
	 */
	public int getCurrentLine() {
		return currentLine;
	}

	/**
	 * @param currentLine
	 *            The currentLine to set.
	 */
	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
		setChanged();
		notifyObservers();
	}

	/**
	 * @return Returns the indentation.
	 */
	public int getIndentation() {
		return indentation;
	}

	/**
	 * @param indentation
	 *            The indentation to set.
	 */
	public void setIndentation(int indentation) {
		this.indentation = indentation;
	}
}