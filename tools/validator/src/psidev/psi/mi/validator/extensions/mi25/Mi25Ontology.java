package psidev.psi.mi.validator.extensions.mi25;

import psidev.psi.mi.validator.framework.ontology.AbstractOntology;
import psidev.psi.mi.validator.framework.ontology.Ontology;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b> PSI-MI 2.5.1 Specific Ontology </b>.
 * <p/>
 *
 * @author Matthias Oesterheld
 * @version $Id: Mi25Ontology.java,v 1.1 2006/01/18 16:55:43 skerrien Exp $
 * @since 04.01.2006; 15:37:07
 */
public class Mi25Ontology extends AbstractOntology {

    ///////////////////////////
    // PSI MI Root terms

    public static final String InteractionDetectionTypeRoot = "MI:0001";
    public static final String ParticipantIdentificationTypeRoot = "MI:0002";
    public static final String FeatureDetectionTypeRoot = "MI:0003";
    public static final String FeatureTypeRoot = "MI:0116";
    public static final String InteractionTypeRoot = "MI:0190";
    public static final String AliasTypeRoot = "MI:0300";
    public static final String InteractorTypeRoot = "MI:0313";
    public static final String ExperimentalPreparationRoot = "MI:0346";
    public static final String XrefTypeRoot = "MI:0353";
    public static final String DatabaseCitationRoot = "MI:0444";
    public static final String ExperimentalRoleRoot = "MI:0495";
    public static final String BiologicalRoleRoot = "MI:0500";
    public static final String AttributeNameRoot = "MI:0590";

    /**
     * Pattern of an MI identifier as a regular expression
     */
    private static final String MI_IDENTIFIER_REGEX = "MI:[0-9]{4}";

    /**
     * Precompile the Pattern Matcher for obvious efficiency reason.
     */
    public static final Pattern MI_IDENTIFIER_PATTERN = Pattern.compile( MI_IDENTIFIER_REGEX );


    /////////////////////////////
    // Constructor

    public Mi25Ontology( Ontology ontology ) {
        super( ontology );
    }


    /////////////////////////////
    // Convenience methods

    /**
     * checks that an MI identifier format is valid against a regular expression.
     * @param id
     * @return true is the identifier is valid, false otherwise.
     */
    public boolean isValidIdentifier( String id ) {

        if( id == null ) {
            return false;
        }

        // validate the Id against that regular expression
        Matcher matcher = MI_IDENTIFIER_PATTERN.matcher( id );
        return matcher.matches();
    }

    public OntologyTerm getInteractionDetectionTypeRoot() {
        return search( InteractionDetectionTypeRoot );
    }

    public OntologyTerm getParticipantIdentificationTypeRoot() {
        return search( ParticipantIdentificationTypeRoot );
    }

    public OntologyTerm getFeatureDetectionTypeRoot() {
        return search( FeatureDetectionTypeRoot );
    }

    public OntologyTerm getFeatureTypeRoot() {
        return search( FeatureTypeRoot );
    }

    public OntologyTerm getInteractionTypeRoot() {
        return search( InteractionTypeRoot );
    }

    public OntologyTerm getAliasTypeRoot() {
        return search( AliasTypeRoot );
    }

    public OntologyTerm getInteractorTypeRoot() {
        return search( InteractorTypeRoot );
    }

    public OntologyTerm getExperimentalPreparationRoot() {
        return search( ExperimentalPreparationRoot );
    }

    public OntologyTerm getXrefTypeRoot() {
        return search( XrefTypeRoot );
    }

    public OntologyTerm getDatabaseCitationRoot() {
        return search( DatabaseCitationRoot );
    }

    public OntologyTerm getExperimentalRoleRoot() {
        return search( ExperimentalRoleRoot );
    }

    public OntologyTerm getBiologicalRoleRoot() {
        return search( BiologicalRoleRoot );
    }

    public OntologyTerm getAttributeNameRoot() {
        return search( AttributeNameRoot );
    }
}