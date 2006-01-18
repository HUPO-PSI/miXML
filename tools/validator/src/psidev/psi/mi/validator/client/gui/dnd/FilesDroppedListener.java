package psidev.psi.mi.validator.client.gui.dnd;

/**
 * Implement this inner interface to listen for when files are dropped. For example your class declaration may begin
 * like this:
 * <code><pre>
 *      public class MyClass implements FileDrop.Listener
 *      ...
 *      public void filesDropped( java.io.File[] files )
 *      {
 *          ...
 *      }   // end filesDropped
 *      ...
 * </pre></code>
 *
 * The adapted source code was found at: http://iharder.sourceforge.net/filedrop/
 *
 * @author Robert Harder
 * @author rharder@usa.net
 * @version 1.0
 */
public interface FilesDroppedListener {

    /**
     * This method is called when files have been successfully dropped.
     * @param files An array of <tt>File</tt>s that were dropped.
     */
    public abstract void filesDropped( java.io.File[] files );
}