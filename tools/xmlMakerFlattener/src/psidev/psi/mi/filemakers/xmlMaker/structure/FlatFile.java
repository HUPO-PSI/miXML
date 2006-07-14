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


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import psidev.psi.mi.filemakers.xmlMaker.mapping.FlatFileMapping;
import psidev.psi.mi.filemakers.xsd.FileMakersException;
import psidev.psi.mi.filemakers.xsd.Utils;


/**
 * The class provide graphical management for a tab delimited file <br>
 * It allows to load a file, choose the field delimitor, and recursively enter
 * into the fields and split them.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class FlatFile {

	public FlatFileMapping getMapping() {
		if (this.fileURL == null)
			return null;
		FlatFileMapping mapping = new FlatFileMapping();
		mapping.setFileURL(Utils.relativizeURL(this.fileURL).getPath());
		mapping.setLineSeparator(this.lineSeparator);
		mapping.setSeparators(this.separators);
		mapping.setFisrtLineForTitle(this.firstLineForTitles());
		return mapping;
	}

	/**
	 * 
	 * @uml.property name="index"
	 */
	public int index = 0;

	/**
	 * 
	 * @uml.property name="indexI"
	 */
	public Integer indexI = new Integer(9);

	/**
	 * true if first line of the file contains titles and should not be parse
	 * 
	 * @uml.property name="titleLine"
	 */
	public boolean firstLineForTitles = false;

	/**
	 * associate a path to a separator
	 * 
	 * @uml.property name="separators"
	 */
	private HashMap separators = new HashMap();

	/**
	 * current line
	 * 
	 * @uml.property name="line"
	 */
	public String line = "";

	/**
	 * current line number
	 * 
	 * @uml.property name="lineNumber"
	 */
	public int lineNumber = 0;

	public void setSeparator(String path, String separator) {
		separators.remove(path);
		separators.put(path, separator);
	}

	public String getSeparator(String path) {
		return (String) separators.get(path);
	}

	/**
	 * part of previous line that has not been readed used when the line
	 * separator is not the end of the line
	 * 
	 * @uml.property name="restOfPreviousLine"
	 */
	public String restOfPreviousLine = new String();

	/**
	 * 
	 * @uml.property name="endOfFile"
	 */
	public boolean endOfFile = true;

	/**
	 * line separator: if null, lines will be readed one by one in the file else
	 * the readline function will read the file untill it find the separator
	 * 
	 * @uml.property name="lineSeparator"
	 */
	public String lineSeparator = null;

	public String getElementAt(String path, String modelPath) {
//		System.out.println(path + "," + modelPath);
		if (path.length() == 0)
			return line;
		if (modelPath == null)
			modelPath = path;
		int pointIndex = 0;
		int nextPointIndex;
		String subpath = "";
		String[] paths = path.split("\\.");
		String[] modelPaths = modelPath.split("\\.");
		String index = "0";
		String field = line;

		int len = paths.length;

		for (int i = 0; i < paths.length; i++) {
			String separator = (String) separators.get(subpath);
			String[] fields;
			if (separator == null) {
				fields = new String[1];
				fields[0] = field;
			} else
				fields = field.split(separator);
			try {
				field = fields[Integer.parseInt(paths[i])];
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				/* no element in this subfield */
				return "";
			}
			if (i > 0)
				subpath += "." + modelPaths[i];
			else
				subpath += modelPaths[i];
		}

		return field;
	}

	/**
	 * set current line in this Object and recursively in all lists
	 * 
	 * @uml.property name="line"
	 */
	public void setLine(String aLine) {
		line = aLine;
	}

	public BufferedReader input;

	public URL fileURL;

	/**
	 * initialise the reader and the file
	 */
	public void reload() throws FileMakersException, MalformedURLException,
			IOException {
		try {
			if (fileURL != null) {
				this.input = new BufferedReader(new InputStreamReader(fileURL
						.openStream()));
			}
			lineNumber = 0;
		} catch (FileNotFoundException fe) {
			throw new FileMakersException(
					"[PSI makers: PSI maker] Flat File: unable to load file");
		}
	}

	/**
	 * load a flat file. The file is choosed by the user in an option panel
	 */
	public void load(URL url) throws FileNotFoundException,
			NullPointerException, MalformedURLException, IOException {
		lineNumber = 0;
		this.fileURL = url;
		this.input = new BufferedReader(new InputStreamReader((fileURL
				.openStream())));
		nextLine();
	}

	/**
	 * try to read a new line
	 */
	public void nextLine() {
		if (input == null) {
			endOfFile = true;
			return;
		}

		if (lineSeparator == null) {
			try {
				String newLine = input.readLine();
				if (newLine != null) {
					setLine(newLine);
					endOfFile = false;
					lineNumber++;
				} else {
					endOfFile = true;
				}
			} catch (IOException e) {
				System.out.println("nextline pb: " + e.toString());
				endOfFile = true;
			}
		} else /* read until lineSeparator is found */{
			String line = restOfPreviousLine;
			StringBuffer newLine = new StringBuffer();
			try {
				while (line.indexOf(lineSeparator) < 0) {
					newLine.append(line);
					line = input.readLine();
					lineNumber++;
				}
				newLine.append(line.substring(0, line.indexOf(lineSeparator)));
				restOfPreviousLine = line.substring(line.indexOf(lineSeparator)
						+ lineSeparator.length());

				if (newLine != null) {
					setLine(newLine.toString());
					endOfFile = false;
				} else {
					endOfFile = true;
				}

			} catch (IOException e) {
				if (newLine.length() != 0) {
					setLine(newLine.toString());
					endOfFile = true;
				} else {
					endOfFile = true;
				}
			}
		}
	}

	/**
	 * try to read a new line until one with selected field not empty is found
	 */
	public void nextLineWithField(String path) {
		if (input == null)
			return;

		nextLine();
		String elt = getElementAt(path, null);
		while (getElementAt(path, null).length() == 0 && !endOfFile) {
			elt = getElementAt(path, null);
			nextLine();
		}
	}

	/**
	 * @return true if a line has been readed
	 */
	public boolean hasLine() {
		//		return line != null;
		return !endOfFile;
	}

	/**
	 * go back to the beginning of the file
	 */
	public void restartFile() throws FileMakersException, IOException {
		reload();
		nextLine();
	}

	/**
	 * Return true if the first line of the file is used for titles. Used to
	 * know wether or not to parse the first line.
	 * 
	 * @return true if the first line of the file is used for titles
	 */
	public boolean firstLineForTitles() {
		return firstLineForTitles;
	}

	/**
	 * 
	 * @uml.property name="lineNumber"
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	public String getCurLine() {
		return line;
	}

	public void save(XMLEncoder oos) {
		oos.writeObject(new Boolean(firstLineForTitles));
		oos.writeObject(lineSeparator);
		oos.writeObject(lineSeparator);
		oos.writeObject(separators);
	}

	public void load(XMLDecoder ois) {
		firstLineForTitles = ((Boolean) ois.readObject()).booleanValue();
		lineSeparator = (String) ois.readObject();
		/** TODO: if file not found -> open a filechooser */
		separators = (HashMap) ois.readObject();
		String filePath = (String) ois.readObject();
	}

	/**
	 * @return Returns the endOfFile.
	 * 
	 * @uml.property name="endOfFile"
	 */
	public boolean isEndOfFile() {
		return endOfFile;
	}

	/**
	 * @param endOfFile
	 *            The endOfFile to set.
	 * 
	 * @uml.property name="endOfFile"
	 */
	public void setEndOfFile(boolean endOfFile) {
		this.endOfFile = endOfFile;
	}

	/**
	 * @return Returns the index.
	 * 
	 * @uml.property name="index"
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            The index to set.
	 * 
	 * @uml.property name="index"
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return Returns the indexI.
	 * 
	 * @uml.property name="indexI"
	 */
	public Integer getIndexI() {
		return indexI;
	}

	/**
	 * @param indexI
	 *            The indexI to set.
	 * 
	 * @uml.property name="indexI"
	 */
	public void setIndexI(Integer indexI) {
		this.indexI = indexI;
	}

	/**
	 * @return Returns the lineSeparator.
	 * 
	 * @uml.property name="lineSeparator"
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * @param lineSeparator
	 *            The lineSeparator to set.
	 * 
	 * @uml.property name="lineSeparator"
	 */
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * @return Returns the restOfPreviousLine.
	 * 
	 * @uml.property name="restOfPreviousLine"
	 */
	public String getRestOfPreviousLine() {
		return restOfPreviousLine;
	}

	/**
	 * @param restOfPreviousLine
	 *            The restOfPreviousLine to set.
	 * 
	 * @uml.property name="restOfPreviousLine"
	 */
	public void setRestOfPreviousLine(String restOfPreviousLine) {
		this.restOfPreviousLine = restOfPreviousLine;
	}

	/**
	 * @return Returns the separators.
	 * 
	 * @uml.property name="separators"
	 */
	public HashMap getSeparators() {
		return separators;
	}

	/**
	 * @param separators
	 *            The separators to set.
	 * 
	 * @uml.property name="separators"
	 */
	public void setSeparators(HashMap separators) {
		this.separators = separators;
	}

	/**
	 * @return Returns the titleLine.
	 * 
	 * @uml.property name="titleLine"
	 */
	public boolean isTitleLine() {
		return firstLineForTitles;
	}

	/**
	 * @param titleLine
	 *            The titleLine to set.
	 * 
	 * @uml.property name="titleLine"
	 */
	public void setTitleLine(boolean titleLine) {
		this.firstLineForTitles = titleLine;
	}

	/**
	 * @return Returns the line.
	 * 
	 * @uml.property name="line"
	 */
	public String getLine() {
		return line;
	}

	/**
	 * @param lineNumber
	 *            The lineNumber to set.
	 * 
	 * @uml.property name="lineNumber"
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @param firstLineForTitles
	 *            The firstLineForTitles to set.
	 */
	public void setFirstLineForTitles(boolean firstLineForTitles) {
		System.out.println("title line: " + firstLineForTitles);
		this.firstLineForTitles = firstLineForTitles;
	}
}