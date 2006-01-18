package psidev.psi.mi.validator.extensions.mi25;

import psidev.psi.mi.validator.extensions.mi25.model.EntrySet;
import psidev.psi.mi.validator.framework.Validator;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <b> PSI-MI 2.5.1 Specific Validator </b>.
 * <p/>
 *
 * @since 04.01.2006; 15:36:59
 * @version $Id: Mi25Validator.java,v 1.1 2006/01/18 16:55:43 skerrien Exp $
 * @author Matthias Oesterheld
 */
public class Mi25Validator extends Validator {

    public Mi25Validator( InputStream configFile ) throws ValidatorException {
        super( configFile );
    }

    public Collection<ValidatorMessage> validate( InputStream is ) throws ValidatorException {
        try {
            // unmarshal to entrySet objects
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            JAXBContext jc = JAXBContext.newInstance( "psidev.psi.mi.validator.extensions.mi25.model" );
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            EntrySet entrySet = (EntrySet) u.unmarshal( is );
            Iterator i1 = entrySet.getEntry().iterator();
            List interactions = new ArrayList();
            while ( i1.hasNext() ) {
                EntrySet.EntryType entry = (EntrySet.EntryType) i1.next();
                interactions.addAll( entry.getInteractionList().getInteraction() );
            }

            return super.validate( interactions );

        } catch ( JAXBException e ) {
            throw new ValidatorException( "Unable to unmarshall XML file", e );
        }
    }
}