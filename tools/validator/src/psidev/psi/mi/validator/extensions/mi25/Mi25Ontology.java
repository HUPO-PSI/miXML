package psidev.psi.mi.validator.extensions.mi25;

import psidev.psi.mi.validator.framework.ontology.AbstractOntology;
import psidev.psi.mi.validator.framework.ontology.Ontology;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

/**
 * <b> PSI-MI 2.5.1 Specific Ontology </b>.
 * <p/>
 *
 * @author Matthias Oesterheld
 * @version $Id: Mi25Ontology.java,v 1.3 2006/04/19 11:12:11 luisa_montecchi Exp $
 * @since 04.01.2006; 15:37:07
 */
public class Mi25Ontology extends AbstractOntology {

    //////////////////////////////
    // Cosntants

    public static final String PUBMED = "pubmed";
    public static final String DOI = "digital object identifier";
    public static final String PUBMED_MI = "MI:0446";
    public static final String DOI_MI = "MI:0574";
    public static final String BAIT_MI = "MI:0496";
    public static final String UNIPROT_MI = "MI:0486";


    ////////////////////////////
    // Mapping name -> MI

    public static Map<String, String> shortlabel2mi = new HashMap<String, String>();

    static {

        // TODO load that list (name, synonym...) using the OBO ontology.

        // Please put only loweracse labels !!!
        shortlabel2mi.put("pubmed", "MI:0446");
        shortlabel2mi.put("pmid", "MI:0446");
        shortlabel2mi.put("digital object identifier", "MI:0574");
        shortlabel2mi.put("doi", "MI:0574");
        shortlabel2mi.put("cabri", "MI:0246");
        shortlabel2mi.put("chebi", "MI:0474");
        shortlabel2mi.put("cygd", "MI:0464");
        shortlabel2mi.put("ddbj/embl/genbank", "MI:0475");
        shortlabel2mi.put("ensembl", "MI:0476");
        shortlabel2mi.put("entrez gene/locuslink", "MI:0477");
        shortlabel2mi.put("flybase", "MI:0478");
        shortlabel2mi.put("huge", "MI:0249");
        shortlabel2mi.put("international protein index", "MI:0675");
        shortlabel2mi.put("ipi", "MI:0675");
        shortlabel2mi.put("kegg", "MI:0470");
        shortlabel2mi.put("mgd/mgi", "MI:0479");
        shortlabel2mi.put("newt", "MI:0247");
        shortlabel2mi.put("omim", "MI:0480");
        shortlabel2mi.put("refseq", "MI:0481");
        shortlabel2mi.put("rfam", "MI:0482");
        shortlabel2mi.put("rgd", "MI:0483");
        shortlabel2mi.put("sgd", "MI:0484");
        shortlabel2mi.put("uniparc", "MI:0485");
        shortlabel2mi.put("uniprot knowledge base", "MI:0486");
        shortlabel2mi.put("uniprotkb", "MI:0486");
        shortlabel2mi.put("uniprot", "MI:0486");
        shortlabel2mi.put("swissprot", "MI:0486");
        shortlabel2mi.put("trembl", "MI:0486");
        shortlabel2mi.put("wormbase", "MI:0487");
    }

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
    public static final Pattern MI_IDENTIFIER_PATTERN = Pattern.compile(MI_IDENTIFIER_REGEX);

    /////////////////////////////
    // Constructor

    public Mi25Ontology(Ontology ontology) {
        super(ontology);
    }

    /////////////////////////////
    // Convenience methods

    /**
     * checks that an MI identifier format is valid against a regular expression.
     *
     * @param id
     * @return true is the identifier is valid, false otherwise.
     */
    public boolean isValidIdentifier(String id) {

        if (id == null) {
            return false;
        }

        // validate the Id against that regular expression
        Matcher matcher = MI_IDENTIFIER_PATTERN.matcher(id);
        return matcher.matches();
    }

    public OntologyTerm getInteractionDetectionTypeRoot() {
        return search(InteractionDetectionTypeRoot);
    }

    public OntologyTerm getParticipantIdentificationTypeRoot() {
        return search(ParticipantIdentificationTypeRoot);
    }

    public OntologyTerm getFeatureDetectionTypeRoot() {
        return search(FeatureDetectionTypeRoot);
    }

    public OntologyTerm getFeatureTypeRoot() {
        return search(FeatureTypeRoot);
    }

    public OntologyTerm getInteractionTypeRoot() {
        return search(InteractionTypeRoot);
    }

    public OntologyTerm getAliasTypeRoot() {
        return search(AliasTypeRoot);
    }

    public OntologyTerm getInteractorTypeRoot() {
        return search(InteractorTypeRoot);
    }

    public OntologyTerm getExperimentalPreparationRoot() {
        return search(ExperimentalPreparationRoot);
    }

    public OntologyTerm getXrefTypeRoot() {
        return search(XrefTypeRoot);
    }

    public OntologyTerm getDatabaseCitationRoot() {
        return search(DatabaseCitationRoot);
    }

    public OntologyTerm getExperimentalRoleRoot() {
        return search(ExperimentalRoleRoot);
    }

    public OntologyTerm getBiologicalRoleRoot() {
        return search(BiologicalRoleRoot);
    }

    public OntologyTerm getAttributeNameRoot() {
        return search(AttributeNameRoot);
    }

    /**
     * Give an MI reference by Controlled Vocabulary name.
     * @param name the cv name
     * @return an MI reference or null if none found.
     */
    public String getMiByCvName(final String name) {

        if (name == null) {
            throw new IllegalArgumentException("Please give a non null name.");
        }
        return shortlabel2mi.get(name.toLowerCase());
    }
}