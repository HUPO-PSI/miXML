package psidev.psi.mi.validator.extensions.mi25.xpath;

import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.xpath.XPathHelper;
import psidev.psi.mi.validator.framework.xpath.XPathResult;

import java.util.List;

/**
 * <b> -- Short Description -- </b>.
 * <p/>
 * -- Description of Class goes here -- </p>
 *
 * @author Matthias Oesterheld
 * @version $Id: Mi25XPathHelper.java,v 1.1 2006/01/18 16:55:44 skerrien Exp $
 * @since 05.01.2006; 16:20:11
 */
public class Mi25XPathHelper extends XPathHelper {

    public static List<XPathResult> evaluateXPath( String xpath, Object root ) throws ValidatorException {
        return evaluateXPathWithClass( xpath, root, Mi25XPathResult.class );
    }

}
