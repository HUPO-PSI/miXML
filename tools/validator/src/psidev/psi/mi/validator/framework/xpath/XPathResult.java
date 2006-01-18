package psidev.psi.mi.validator.framework.xpath;

import psidev.psi.mi.validator.framework.Context;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * <b> -- Short Description -- </b>.
 * <p/>
 * TODO document that method.
 *
 * @author Matthias Oesterheld
 * @version $Id: XPathResult.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since 05.01.2006; 15:18:58
 */
public class XPathResult {

    /**
     * TODO document that method.
     */
    protected Pointer pointer;

    /**
     * TODO document that method.
     */
    protected JXPathContext rootContext;

    /**
     * TODO document that method.
     * @param pointer
     * @param rootContext
     */
    public XPathResult( Pointer pointer, JXPathContext rootContext ) {
        this.pointer = pointer;
        this.rootContext = rootContext;
    }

    /**
     * TODO document that method.
     * @return  TODO
     */
    public Object getResult() {
        return pointer.getNode();
    }

    /**
     * TODO document that method.
     * @return TODO
     */
    public Context getContext() {
        return new Context( pointer.getNode().toString() );
    }
}