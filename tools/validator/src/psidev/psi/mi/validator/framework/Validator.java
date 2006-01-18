package psidev.psi.mi.validator.framework;

import psidev.psi.mi.validator.framework.ontology.Ontology;
import psidev.psi.mi.validator.framework.ontology.OntologyLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * <b>Semantic XML Validator</b>.
 * <p/>
 * Validates a XML document against a set of rules. </p>
 *
 * @author Matthias Oesterheld & Samuel Kerrien
 * @version $Id: Validator.java,v 1.1 2006/01/18 16:57:59 skerrien Exp $
 * @since 04.01.2006; 15:37:20
 */
public abstract class Validator {

    /**
     * The ser of rules specific to that Validator.
     */
    private List<Rule> rules;

    /**
     * Instantiates the Validator using a config file. The config file contains a set of ontologies to be used by the
     * rules and the fully qualified names of the rule classes to be used. Detailled Format: <br />
     * <pre>
     * &lt;config&gt;<br/>
     *    &lt;ontologies&gt;<br/>
     *       &lt;ontology id="MI"&gt;http://url.to/ontology.obo&lt;/ontology&gt;<br/>
     *    &lt;/ontologies&gt;<br/>
     *    &lt;rules&gt;<br/>
     *       &lt;rule&gt;net.sf.psidev.mi.validator.myRule&lt;/rule&gt;<br/>
     *    &lt;/rules&gt;<br/>
     * &lt;/config&gt;<br/>
     * </pre>
     *
     * @param configFile
     *
     * @throws ValidatorException
     */
    public Validator( InputStream configFile ) throws ValidatorException {
        rules = new ArrayList();
        parseConfigFile( configFile );
    }

    /**
     * Validates a document.
     *
     * @param file InputStream holding document to be validated
     *
     * @return Collection of validator messages.
     */
    public abstract Collection<ValidatorMessage> validate( InputStream file ) throws ValidatorException;

    /**
     * Validates a collection of JAXB Objects against all the rules
     *
     * @param col collection of JAXB OBjects
     *
     * @return collection of validator messages
     */
    public Collection<ValidatorMessage> validate( Collection col ) throws ValidatorException {
        List messages = new ArrayList();
        Iterator rulez = rules.iterator();
        while ( rulez.hasNext() ) {
            Rule rule = (Rule) rulez.next();
            messages.addAll( validate( col, rule ) );
        }
        return messages;
    }

    /**
     * Validates a collection of JAXB Objects against a single rule
     *
     * @param col collection of JAXB OBjects
     *
     * @return collection of validator messages
     */
    private Collection<ValidatorMessage> validate( Collection col, Rule r ) throws ValidatorException {
        List messages = new ArrayList();
        Iterator objs = col.iterator();
        while ( objs.hasNext() ) {
            Object o = objs.next();
            messages.addAll( r.check( o ) );
        }
        return messages;
    }

    /**
     * Parse the config file and update the list of Rule of the current Validator.<br/> Each Rule is initialised with a
     * Map of Ontologies that have been read from the config file.
     *
     * @param configFile the configuration file.
     *
     * @throws ValidatorException
     */
    private void parseConfigFile( InputStream configFile ) throws ValidatorException {
        Map<String, Ontology> ontologies = new HashMap<String, Ontology>();
        Document document;

        // parse XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( configFile );
        } catch ( Exception e ) {
            throw new ValidatorException( "Error parsing config file", e );
        }

        // read all ontologies
        OntologyLoader loader = OntologyLoader.getInstance();
        NodeList ontos = document.getElementsByTagName( "ontology" );
        for ( int i = 0; i < ontos.getLength(); i++ ) {
            NodeList urls = ontos.item( i ).getChildNodes();
            String url = urls.item( 0 ).getNodeValue();
            String id = ( (Element) ontos.item( i ) ).getAttribute( "id" );
            try {
                ontologies.put( id, loader.load( new URL( url ) ) );
            } catch ( MalformedURLException e ) {
                throw new ValidatorException( "Malformed URL in config file", e );
            }
        }

        // instantiate rules with ontologies
        NodeList rs = document.getElementsByTagName( "rule" );
        for ( int i = 0; i < rs.getLength(); i++ ) {
            NodeList texts = rs.item( i ).getChildNodes();
            String className = texts.item( 0 ).getNodeValue();
            try {
                Class rule = Class.forName( className );
                Constructor c = rule.getConstructor( new Class[]{ java.util.Map.class } );
                Rule r = (Rule) c.newInstance( new Object[]{ ontologies } );
                rules.add( r );
            } catch ( Exception e ) {
                throw new ValidatorException( "Error instantiating rule (" + className + ")", e );
            }
        }
    }
}