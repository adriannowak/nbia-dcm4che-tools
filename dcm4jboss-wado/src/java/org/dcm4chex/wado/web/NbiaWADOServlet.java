package org.dcm4chex.wado.web;

import org.apache.log4j.Logger;

public class NbiaWADOServlet extends WADOServlet {
	
    /** serialVersionUID because super class is serializable. */
    private static final long serialVersionUID = 3257008748022085682L;

    private static Logger log = Logger.getLogger( WADOServlet.class.getName() );
	
    /**
     * Initialize the WADOServiceDelegator.
     * <p>
     * Set the name of the MBean from servlet init param.
     */
    public void init() {
        delegate = new NbiaWADOServiceDelegate();
        delegate.init( getServletConfig() );
    }

}
