package org.dcm4chex.wado.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dcm4chex.wado.common.WADORequestObject;
import org.dcm4chex.wado.common.WADOResponseObject;
import org.dcm4chex.wado.mbean.WADOStreamResponseObjectImpl;

public class NbiaWADOServiceDelegate extends WADOServiceDelegate {
	
	private static Logger log = Logger.getLogger( NbiaWADOServiceDelegate.class.getName() );
	
	/**
	 * Makes the MBean call to get the WADO response object for given WADO request.
	 * 
	 * @param reqVO	The WADO request.
	 * 
	 * @return The WADO response object.
	 */
	public WADOResponseObject getWADOObject( WADORequestObject reqVO ) {
		WADOResponseObject resp = null;
		reqVO.setStudyPermissionCheckDisabled( isStudyPermissionCheckDisabled(reqVO.getRequest()) );
		try {
	        Object o = server.invoke(wadoServiceName,
	                "getWADOObject",
	                new Object[] { reqVO },
	                new String[] { WADORequestObject.class.getName() } );
	        resp = (WADOResponseObject) o;
		} catch ( Exception x ) {
			log.error( "Exception occured in NBIA getWADOObject: "+x.getMessage(), x );
			resp = new WADOStreamResponseObjectImpl( null, "text.html", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error in WADO service ("+wadoServiceName+"): "+x.getMessage());
		}
        return resp;
	}

}
