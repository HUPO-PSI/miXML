package psidev.psi.mi.validator.extensions.mi25.rules;

import psidev.psi.mi.validator.extensions.mi25.Mi25Context;
import psidev.psi.mi.validator.extensions.mi25.Mi25Ontology;
import psidev.psi.mi.validator.extensions.mi25.Mi25Rule;
import psidev.psi.mi.validator.extensions.mi25.model.*;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.framework.MessageLevel;
import psidev.psi.mi.validator.framework.ontology.model.OntologyTerm;

import java.util.*;

/**
 * <b> check that each experiment has a valid bibref to pubmed or DOI. </b>.
 * <p/>
 *
 * @author Luisa Montecchi
 * @version $Id: ExperimentBibRefRule.java,v 1.1 2006/04/19 10:58:24 luisa_montecchi Exp $
 * @since 15-Feb-2006
 */
public class ExperimentBibRefRule extends Mi25Rule {

    public ExperimentBibRefRule(Map ontologies) {
        super(ontologies);

        // describe the rule.
        setName("Experiment BibRef Check");
        setDescription("Checks that each experiment has a reference to a paper");
        addTip("Search at http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=PubMed or " +
                "at http://www.ebi.ac.uk/Databases/MEDLINE/medline.html " +
                "a PubMedID(PMID) or DOI code for the publication");
    }

    /**
     * Check based on CV database and CV xref type, pmid must be an integer and DOI reg-exp.
     *
     * @param jaxbObject an interaction to check on.
     * @return a collection of validator messages.
     * @throws psidev.psi.mi.validator.framework.ValidatorException
     *          if we fail to retreive the MI Ontology.
     */
    public Collection<ValidatorMessage> check(Object jaxbObject) throws ValidatorException {

        checkExpandedFormat(jaxbObject);

        // Object upon which we perform the check
        InteractionElementType interaction = (InteractionElementType) jaxbObject;
        int interactionId = interaction.getId();

        // list of messages to return
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        // write the rule here ...
        for (Object exp : interaction.getExperimentList().getExperimentRefOrExperimentDescription()) {
            if (exp instanceof InteractionElementType.ExperimentListType.ExperimentDescription) {

                ExperimentType experimentDescription = (ExperimentType) exp;

                int experimentId = experimentDescription.getId();

                DbReferenceType xref = experimentDescription.getBibref().getXref().getPrimaryRef();
                String db = xref.getDb();
                if (db.equals("")) {
                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setExperimentId(experimentId);

                    messages.add(new ValidatorMessage("The attribute db in primary ref for experiment bibref is an empty string",
                            MessageLevel.FATAL,
                            context,
                            this));
                }

                String id = xref.getId();
                if (id.equals("")) {
                    Mi25Context context = new Mi25Context();
                    context.setInteractionId(interactionId);
                    context.setExperimentId(experimentId);

                    messages.add(new ValidatorMessage("The attribute id in primary ref for experiment bibref is an empty string",
                            MessageLevel.FATAL,
                            context,
                            this));
                }

                if (! id.equals("") && ! db.equals("")) {
                    Mi25Ontology ont = super.getMiOntology();     //  db is name not MI and MI not mandatory
                    if (! db.equals(Mi25Ontology.PUBMED) && ! db.equals(Mi25Ontology.DOI)) {
                        Mi25Context context = new Mi25Context();
                        context.setInteractionId(interactionId);
                        context.setExperimentId(experimentId);
                        messages.add(new ValidatorMessage("The attribute db in primary ref is different from 'pubmed' or 'doi' or 'digital object identifier' (" + db + ")",
                                MessageLevel.FATAL,
                                context,
                                this));

                    } else if (db.equals(Mi25Ontology.PUBMED)) {
                        try {
                            int idInteger = Integer.parseInt(id);


                        } catch (NumberFormatException nfe) {

                            Mi25Context context = new Mi25Context();
                            context.setInteractionId(interactionId);
                            context.setExperimentId(experimentId);
                            messages.add(new ValidatorMessage("PubMedID is not valid, it is not an integer(" + id + ")",
                                    MessageLevel.FATAL,
                                    context,
                                    this));
                        }
                    }
                }
            }
        } // for

        return messages;
    }
}