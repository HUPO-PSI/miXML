package psidev.psi.mi.validator.framework.xpath;

import psidev.psi.mi.validator.framework.ValidatorException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b> -- TODO Short Description -- </b>.
 * <p/>
 *
 * @author Matthias Oesterheld
 * @version $Id: XPathHelper.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since 05.01.2006; 15:19:47
 */
public class XPathHelper {

    /**
     * TODO document that method.
     * @param xpath
     * @param root
     * @return TODO
     * @throws ValidatorException
     */
    public static List<XPathResult> evaluateXPath( String xpath, Object root ) throws ValidatorException {
        return evaluateXPathWithClass( xpath, root, XPathResult.class );
    }

    /**
     * TODO document that method.
     * @param xpath
     * @param root
     * @param clazz
     * @return TODO
     * @throws ValidatorException
     */
    protected static List<XPathResult> evaluateXPathWithClass( String xpath, Object root, Class clazz ) throws ValidatorException {
        JXPathContext ctx = JXPathContext.newContext( root );
        Iterator iter = ctx.iteratePointers( xpath );
        List<XPathResult> results = new ArrayList<XPathResult>();
        while ( iter.hasNext() ) {
            Pointer p = (Pointer) iter.next();
            try {
                Constructor constructor = clazz.getConstructor( new Class[]{ Pointer.class, JXPathContext.class } );
                results.add( (XPathResult) constructor.newInstance( new Object[]{ p, ctx } ) );
            } catch ( Exception e ) {
                throw new ValidatorException( "Error creating XPath Result class" );
            }
        }
        return results;
    }
}