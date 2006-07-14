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

import java.util.ArrayList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XmlErrorHandler implements ErrorHandler {
	public ArrayList errors = new ArrayList();

	public final static int error = 0;
	public final static int warning = 1;
	
	public void warning(SAXParseException exception) throws SAXException {
		errors.add("Warning	" + "  Line:    " + exception.getLineNumber() + "	"
				+ "  URI:     " + exception.getSystemId() + "	" + "  Message: "
				+ exception.getMessage());
	}

	public void error(SAXParseException exception) throws SAXException {
		errors.add("Error " + "  Line:    " + exception.getLineNumber() + "	"
				+ "  URI:     " + exception.getSystemId() + "	" + "  Message: "
				+ exception.getMessage());
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		errors.add("Fatal Error	" + "  Line:    " + exception.getLineNumber()
				+ "	" + "  URI:     " + exception.getSystemId() + "	"
				+ "  Message: " + exception.getMessage());
	}
}

