package psidev.psi.mi.validator.extensions.mi25.xpath;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.framework.Context;
import psidev.psi.mi.validator.framework.xpath.XPathResult;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * <b> -- Short Description -- </b>.
 * <p/>
 * -- Description of Class goes here -- </p>
 *
 * @author Matthias Oesterheld
 * @version $Id: Mi25XPathResult.java,v 1.1 2006/01/18 16:55:44 skerrien Exp $
 * @since 05.01.2006; 15:26:34
 */
public class Mi25XPathResult extends XPathResult {

    public Mi25XPathResult( Pointer pointer, JXPathContext rootContext ) {
        super( pointer, rootContext );
    }

    public Context getContext() {
        Mi25Context context = new Mi25Context();
        Pointer root = rootContext.getPointer( "self::node()" );
        Pointer currentPointer = pointer;
        while ( !currentPointer.equals( root ) ) {
            context.setId( currentPointer.getNode() );
            JXPathContext relCTX = rootContext.getRelativeContext( currentPointer );
            currentPointer = relCTX.getPointer( "parent::node()" );
        }
        context.setId( currentPointer.getNode() );
        return context;
    }

}
