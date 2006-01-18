package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.DbReferenceType;
import psidev.psi.mi.validator.extensions.mi25.xpath.Mi25XPathHelper;
import psidev.psi.mi.validator.extensions.mi25.xpath.Mi25XPathResult;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;
import psidev.psi.mi.validator.framework.xpath.XPathResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <b> A sample rule using the  </b>.
 * <p/>
 *
 * @author Matthias Oesterheld
 */
public class SimpleRule extends Mi25Rule {

    public SimpleRule( Map ontologies ) {
        super( ontologies );
    }

    public Collection<ValidatorMessage> check( Object jaxbObject ) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
        Mi25Ontology ontology = super.getMiOntology();


        for ( XPathResult xPathResult : Mi25XPathHelper.
                evaluateXPath( "participantList/participant/interactor/xref/primaryRef", jaxbObject ) ) {
            Mi25XPathResult result = (Mi25XPathResult) xPathResult;
            DbReferenceType ref = (DbReferenceType) result.getResult();
            String dbac = ref.getDbAc();
            String db = ref.getDb();
            if ( dbac == null || db == null ) {
                messages.add(
                        new ValidatorMessage( "No database or database accession",
                                              MessageLevel.ERROR,
                                              result.getContext(),
                                              this ) );
            } else {
                OntologyTerm term = ontology.search( dbac );
                if ( !db.equals( term.getShortName() ) ) {
                    messages.add(
                            new ValidatorMessage( dbac + " does not match " + db,
                                                  MessageLevel.ERROR,
                                                  result.getContext(),
                                                  this ) );
                }
            }
        }

        return messages;

    }


}
