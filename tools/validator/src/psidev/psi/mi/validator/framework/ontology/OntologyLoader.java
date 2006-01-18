package psidev.psi.mi.validator.framework.ontology;

import psidev.psi.mi.validator.util.HttpProxyManager;
import psidev.psi.mi.validator.framework.ValidatorException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <b> Ontology URL Loader </b>.
 * <p/>
 * The Ontologies are referenced by a unique URL that is used as a key to cache them. </p>
 *
 * @author Matthias Oesterheld
 * @version $Id: OntologyLoader.java,v 1.1 2006/01/18 16:59:10 skerrien Exp $
 * @since 04.01.2006; 15:20:33
 */
public class OntologyLoader {

    ///////////////////////////////////////////////
    // methods and attributes for singleton

    /**
     * singleton instance
     */
    private static OntologyLoader ourInstance;

    // getInstance() method modified for Double Check Locking pattern
    public synchronized static OntologyLoader getInstance() {
        if ( ourInstance == null ) {
            synchronized ( OntologyLoader.class ) {
                if ( ourInstance == null ) {
                    ourInstance = new OntologyLoader();
                }
            }
        }
        return ourInstance;
    }

    private OntologyLoader() {
        // initialize global variables
        initGlobals();
    }

    /**
     * methods and attributes for global data **
     */
    private Map<URL, Ontology> cache;

    /**
     * initializes the cache
     */
    private void initGlobals() {
        cache = new HashMap<URL, Ontology>();
        try {
            HttpProxyManager.setup();
        } catch ( HttpProxyManager.ProxyConfigurationNotFound proxyConfigurationNotFound ) {
            proxyConfigurationNotFound.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an ontology from the cache, based on the URL. If the ontology has not been loaded before, it loads it
     * and puts it in the cache.
     *
     * @param url URL of ontology file
     *
     * @return The ontology
     */
    public Ontology load( URL url ) throws ValidatorException {
        Ontology o;

        if ( !cache.containsKey( url ) ) {
            try {
                o = loadOntology( url );
            } catch ( URISyntaxException e ) {
                throw new ValidatorException( "Could not load ontology from " + url, e );
            }
            cache.put( url, o );
        } else {
            o = cache.get( url );
        }

        return o;
    }

    /**
     * loads the Ontology from the specified location
     *
     * @param url URL
     *
     * @return Ontology the loaded ontology
     */
    private Ontology loadOntology( URL url ) throws URISyntaxException {

        OboLoader loader = new OboLoader();
        File oboFile = new File( url.toURI() );
        // TODO get OBO loader to take an InputStream as parameter, then we can really use URL, and not only File.
        return loader.parseOboFile( oboFile );
    }
}