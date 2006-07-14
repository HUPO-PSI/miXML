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

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Structure;

/**
 * @author Arnaud Ceol, University of Rome "Tor Vergata", Mint group,
 *         arnaud@cbm.bio.uniroma2.it
 *  
 */
public class XsdNode extends DefaultMutableTreeNode {

	/**
	 * a node is transparent, ie its father will directly look at the children 
	 * This kind of node is used to represent choices that have been done 
	 */
	public boolean transparent = false;

	public int cpt = 0;

	public int nextNumber() {
		cpt++;
		return cpt;
	}

	/**
	 * true if the node has been checked once for all, and do not need to be
	 * anymore (case of flat file element)
	 */
	public boolean isSuperChecked = false;

	public boolean isExtended = false;

	public boolean isCheckedOk = false;

	public boolean isRequired;

	public boolean isUsed;

	/**
	 * how many time the node is used, ie how many sub-elements are used
	 */
	public int use;

	/**
	 * keep trace of the parent of this node for choices
	 */
	public XsdNode originalParent = null;

	/**
	 * how many node of this type are needed by the father node according to the
	 * schema
	 */
	public int min = 0;

	/**
	 * how many node of this type can be contained by the father node according
	 * to the schema
	 */
	public int max;

	/**
	 * create a new node with same attribute values as this node
	 */
	public XsdNode createBrother() {
		XsdNode brother = new XsdNode();
		brother.setUserObject((Annotated) this.getUserObject());
		brother.isRequired = this.isRequired;
		brother.min = this.min;
		brother.max = this.max;
		if (this.originalParent != null) {
			brother.originalParent = this.originalParent.createBrother();
		}
		return brother;
	}

	/**
	 * check if according to the schema the father element can contain more than
	 * one element of this type
	 * 
	 * @return true if the node is duplicable according to the schema
	 */
	public boolean isDuplicable() {
		return max > 1 || max == -1;
	}

	public void init() {
		isUsed = false;
		use = 0;
	}

	public XsdNode() {
		super();
	}

	public XsdNode(Annotated aValue) {
		super(aValue);
		try {
			if (((AttributeDecl) aValue).isRequired()) {
				isRequired = true;
				min = 1;
			}
			max = 1;
		} catch (ClassCastException e) {
			try {
				if (((Particle) aValue).getMinOccurs() != 0) {
					isRequired = true;
					min = ((Particle) aValue).getMinOccurs();
				}
				max = ((Particle) aValue).getMaxOccurs();
			} catch (ClassCastException e2) {
				e2.printStackTrace(System.err);
			}
		}
//		try {
//			/* ref type */
//			if (((ElementDecl) aValue).getType().getName().compareTo(
//					AbstractXsdTreeStruct.refType) == 0
//					&& !AbstractXsdTreeStruct.refTypeList.contains(this
//							.toString())) {
//				AbstractXsdTreeStruct.refTypeList.add(this.toString());
//			}
//		} catch (Exception e2) {
//		}
	}

	/**
	 * increment by one the number of time this node is used, and all parent
	 * nodes recursively
	 */
	public void use() {
		use++;
		if (this.getParent() != null) {
			((XsdNode) this.getParent()).use();
		}
		isUsed = true;
	}

	/**
	 * idem use()
	 */
	public void useOnlyThis() {
		use();
	}

	/**
	 * idem unuse()
	 */
	public void unuseOnlyThis() {
		unuse();
	}

	/**
	 * decrement by one the number of time this node is used, and all parent
	 * nodes recursively
	 */
	public void unuse() {
		use--;
		if (this.getParent() != null) {
			((XsdNode) this.getParent()).unuse();
		}
		isUsed = use > 0;
	}

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * return the either the name of the element or attribute represented by
	 * this node, or the choices available for this element.
	 */
	public String toString() {
		
		switch (((Annotated) this.getUserObject()).getStructureType()) {
		case Structure.ATTRIBUTE:
			return ((AttributeDecl) this.getUserObject()).getName();
		case Structure.ELEMENT:
			return ((ElementDecl) this.getUserObject()).getName();
		case Structure.GROUP:
			return choiceToString((Group) this.getUserObject());
		default:
			return ((Annotated) this.getUserObject()).toString();
		}
	}

	/**
	 * transforms a list of choices returned for example by the methods
	 * <code>getChoices</code> and create a single <code>String</code> to
	 * describe it using for example "|" symbol to express a choice between two
	 * elements
	 * 
	 * @param g
	 *            a single <code>String</code> to describe a list of choices
	 * @return
	 */
	public static String choiceToString(Group g) {
		Enumeration children = g.enumerate();
		String init;
		String delimiter = "";
		if (g.getOrder().getType() == Order.CHOICE)
			init = " | ";
		else
			init = " & ";
		String value = "(";
		while (children.hasMoreElements()) {
			Annotated child = (Annotated) children.nextElement();
			switch (child.getStructureType()) {
			case Structure.ATTRIBUTE:
				value += delimiter + ((AttributeDecl) child).getName();
				break;
			case Structure.ELEMENT:
				value += delimiter + ((ElementDecl) child).getName();
				break;
			default:
				value += delimiter + choiceToString((Group) child);
			}
			delimiter = init;
		}
		return value + ")";
	}

	public String choiceToString() {
		try {
			return choiceToString((Group) this.getUserObject());
		} catch (ClassCastException cce) {
			/* not a group */
			return "";
		}
	}

	/**
	 * could not overwrite getPath method
	 * 
	 * @return
	 */
	public String getPath2String() {
		TreeNode nodesPath[] = super.getPath();
		String path = "0";
		for (int i = 0; i < nodesPath.length - 1; i++) {
			path += "." + nodesPath[i].getIndex(nodesPath[i + 1]);
		}
		return path;
	}

	/**
	 * set this node and all its children recursively as super checked
	 * 
	 */
	public void setSuperChecked() {
		this.isSuperChecked = true;
		Enumeration children = this.children();
		while (children.hasMoreElements()) {
			((XsdNode) children.nextElement()).setSuperChecked();
		}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		if (name == null)
			return this.toString();
		return name;
	}
}