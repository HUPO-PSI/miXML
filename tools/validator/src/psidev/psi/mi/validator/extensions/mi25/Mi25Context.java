package psidev.psi.mi.validator.extensions.mi25;

import psidev.psi.mi.validator.framework.Context;
import psidev.psi.mi.validator.extensions.mi25.model.*;

/**
 * <b> PSI-MI 2.5.1 Specific Context </b>.
 * <p/>
 *
 * @author Matthias Oesterheld
 * @version $Id: Mi25Context.java,v 1.1 2006/01/18 16:55:43 skerrien Exp $
 * @since 04.01.2006; 15:56:04
 */
public class Mi25Context extends Context {

    //////////////////////////
    // Instance variable

    private int interactionId = -1;
    private int experimentId = -1;
    private int participantId = -1;
    private int interactorId = -1;
    private int featureId = -1;

    public Mi25Context( String context ) {
        super( context );
    }

    public Mi25Context() {
        super( null );
    }

    ///////////////////////////////
    // Setters

    public int getInteractionId() {
        return interactionId;
    }

    public void setInteractionId( int interactionId ) {
        this.interactionId = interactionId;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId( int experimentId ) {
        this.experimentId = experimentId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId( int participantId ) {
        this.participantId = participantId;
    }

    public int getInteractorId() {
        return interactorId;
    }

    public void setInteractorId( int interactorId ) {
        this.interactorId = interactorId;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId( int featureId ) {
        this.featureId = featureId;
    }

    public void setId( Object element ) {
        if (element instanceof InteractionElementType) {
            InteractionElementType i = (InteractionElementType) element;
            this.setInteractionId(i.getId());
        } else if (element instanceof InteractorElementType) {
            InteractorElementType i = (InteractorElementType) element;
            this.setInteractorId(i.getId());
        } else if (element instanceof ParticipantType) {
            ParticipantType p = (ParticipantType) element;
            this.setParticipantId(p.getId());
        } else if (element instanceof ExperimentType) {
            ExperimentType e = (ExperimentType) element;
            this.setExperimentId(e.getId());
        } else if (element instanceof FeatureElementType) {
            FeatureElementType f = (FeatureElementType) element;
            this.setFeatureId(f.getId());
        }
    }

    ////////////////////////
    // toString

    public String toString() {
        StringBuffer sb = new StringBuffer( 128 );
        sb.append( "Context(" );

        if( interactionId != -1 ) {
            sb.append( " interaction[id='" ).append( interactionId ).append( "']" );
        }

        if( experimentId != -1 ) {
            sb.append( " experimentDescription[id='" ).append( experimentId ).append( "']" );
        }

        if( participantId != -1 ) {
            sb.append( " participant[id='" ).append( participantId ).append( "']" );
        }

        if( interactorId != -1 ) {
            sb.append( " interactor[id='" ).append( participantId ).append( "']" );
        }

        if( featureId != -1 ) {
            sb.append( " feature[id='" ).append( featureId ).append( "']" );
        }

        sb.append( " )" );
        return sb.toString();
    }
}