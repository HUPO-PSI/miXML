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


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import psidev.psi.mi.filemakers.xmlMaker.mapping.DictionaryMapping;
import psidev.psi.mi.filemakers.xsd.Utils;


/**
 * Management of a dictionnary. A dictionnary is readed in a file and associates
 * the first word of each line to a list of definitions, i.e. the list of words
 * following on the same line.
 * 
 * The dictionnary can be read from a flat file, for exemble a tab delimited
 * file , which field separator can be set.
 * 
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 */
public class Dictionary {

	/**
	 * 
	 * @uml.property name="index"
	 */
	public int index;

	/**
	 * keep the definitions:the key is the word and the value a list (ArrayList)
	 * of definitions
	 * 
	 * @uml.property name="dictionnary"
	 */
	public HashMap dictionnary = new HashMap();

	/**
	 * indicates if the dictionnary has to take care of the case of the
	 * characters or not
	 * 
	 * @uml.property name="caseSensitive"
	 */
	public boolean caseSensitive;

	/**
	 * The name for the controlled vocabulary, i.e. the name of the file on
	 * which the dictionnary has been loaded.
	 * 
	 * @uml.property name="name"
	 */
	public String name;

	public BufferedReader input;

	/**
	 * 
	 * @uml.property name="fileURL"
	 */
	public URL fileURL;

	/**
	 * The field separator
	 * 
	 * @uml.property name="separator"
	 */
	public String separator;

	/**
	 * The longest list readed on a line. Can be used to have the best overview
	 * of a value and its available definitions or to choose a definition.
	 * 
	 * @uml.property name="maxDefinitionLine"
	 */
	public String maxDefinitionLine;

	/**
	 * load a dictionnary
	 * 
	 * @param f
	 *            the file containing the dictionnary
	 * @param separator
	 *            the string separating the words
	 * @throws FileNotFoundException
	 */
	public void load(URL url, String separator) throws FileNotFoundException,
			java.util.regex.PatternSyntaxException, IOException {

		this.fileURL = url;
		this.input = new BufferedReader(new InputStreamReader(fileURL
				.openStream()));

		Utils.lastVisitedDirectory = url.getPath();
		Utils.lastVisitedDictionaryDirectory = url.getPath();

		int maxDefinitionsNumber = 0;

		while (true) {
			try {
				String line = input.readLine().trim();
				String[] values = (line + " ").split(separator);
				if (values.length > 0) {
					// make it possible to have empty lines

					String value = values[0].trim();
					ArrayList definitions = new ArrayList();

					for (int i = 1; i < values.length; i++) {
						definitions.add(values[i].trim() + " ");
					}

					if (definitions.size() > maxDefinitionsNumber) {
						maxDefinitionsNumber = definitions.size();
						maxDefinitionLine = line;
					}

					if (!caseSensitive) {
						dictionnary
								.put(value.toLowerCase().trim(), definitions);
					} else {
						dictionnary.put(value.trim(), definitions);
					}
				}
				name = Utils.relativizeURL(fileURL).getPath();
				this.separator = separator;
			} catch (java.util.regex.PatternSyntaxException ex) {
				/** TODO : manage exception */
				return;
			} catch (Exception e) { /* end of file */
				return;
			}
		}
	}

	/**
	 * constructor by default. The ditionnary is set as case sensitive
	 *  
	 */
	public Dictionary() {
		super();
		caseSensitive = true;
	}

	/**
	 * constructor with specification of case sensitivity
	 * 
	 * @param caseSensitive
	 */
	public Dictionary(boolean caseSensitive) {
		super();
		this.caseSensitive = caseSensitive;
	}

	/**
	 * create a new instance of Dictionnary with specified case sensitivity and
	 * load specified file using the separator
	 * 
	 * @param f
	 *            the file to load
	 * @param separator
	 *            the separator for words on each line
	 * @param caseSensitive
	 * @throws FileNotFoundException
	 */
	public Dictionary(URL url, String separator, boolean caseSensitive)
			throws FileNotFoundException, IOException {
		this.caseSensitive = caseSensitive;
		this.fileURL = url;
		load(fileURL, separator);
	}

	/**
	 * get the value associated to this word
	 * 
	 * @param value
	 *            the word
	 * @param definitionNumber
	 *            the number of the definition to look for, as a value can have
	 *            more than one definition, i.e. the postition of the definition
	 *            on a line.
	 * @return the definition
	 */
	public String getDefinition(String value, int definitionNumber) {
		try {
			if (caseSensitive) {
				return (String) ((ArrayList) dictionnary.get(value))
						.get(definitionNumber - 1);
			}
			/*
			 * definitionNumber -1 because we do not count the value but only
			 * the definitions
			 */
			else {
				return (String) ((ArrayList) dictionnary.get(value
						.toLowerCase())).get(definitionNumber - 1);
			}
		} catch (NullPointerException e) {
			return null;
		} catch (IndexOutOfBoundsException e2) {
			return null;
		}
	}

	public String[] exampleList() {
		if (maxDefinitionLine == null) {
			String value[] = {};
			return value;
		}
		return (maxDefinitionLine).split(separator);
	}

	public String exampleLine() {
		if (maxDefinitionLine == null) {
			return "";
		}
		return maxDefinitionLine;
	}

	/**
	 * @return the name of the dictionnary
	 */
	public String toString() {
		return name;
	}

	/**
	 * 
	 * @uml.property name="separator"
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * 
	 * @uml.property name="caseSensitive"
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @return Returns the dictionnary.
	 * 
	 * @uml.property name="dictionnary"
	 */
	public HashMap getDictionnary() {
		return dictionnary;
	}

	/**
	 * @param dictionnary
	 *            The dictionnary to set.
	 * 
	 * @uml.property name="dictionnary"
	 */
	public void setDictionnary(HashMap dictionnary) {
		this.dictionnary = dictionnary;
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
	 * @return Returns the maxDefinitionLine.
	 * 
	 * @uml.property name="maxDefinitionLine"
	 */
	public String getMaxDefinitionLine() {
		return maxDefinitionLine;
	}

	/**
	 * @param maxDefinitionLine
	 *            The maxDefinitionLine to set.
	 * 
	 * @uml.property name="maxDefinitionLine"
	 */
	public void setMaxDefinitionLine(String maxDefinitionLine) {
		this.maxDefinitionLine = maxDefinitionLine;
	}

	/**
	 * @return Returns the name.
	 * 
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 * 
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param caseSensitive
	 *            The caseSensitive to set.
	 * 
	 * @uml.property name="caseSensitive"
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @param separator
	 *            The separator to set.
	 * 
	 * @uml.property name="separator"
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public DictionaryMapping getMapping() {
		DictionaryMapping mapping = new DictionaryMapping();
		if (this.fileURL != null)
			mapping.setFileURL(Utils.relativizeURL(this.fileURL).getPath());
		mapping.setCaseSensitive(this.isCaseSensitive());
		mapping.setSeparator(this.separator);
		return mapping;
	}

	/**
	 * @return Returns the fileURI.
	 * 
	 * @uml.property name="fileURL"
	 */
	public URL getFileURL() {
		return fileURL;
	}

	/**
	 * @param fileURI
	 *            The fileURI to set.
	 * 
	 * @uml.property name="fileURL"
	 */
	public void setFileURL(URL fileURI) {
		this.fileURL = fileURI;
	}

}