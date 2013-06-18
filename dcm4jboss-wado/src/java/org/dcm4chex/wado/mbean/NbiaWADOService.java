package org.dcm4chex.wado.mbean;

import org.dcm4chex.wado.common.WADORequestObject;
import org.dcm4chex.wado.common.WADOResponseObject;

public class NbiaWADOService extends WADOService {
	private WADOSupport support = new NbiaWADOSupport(this.server);
	
	 /**
     * Get the requested DICOM object as File packed in a WADOResponseObject.
     * <p>
     * 
     * @param reqVO
     *            The request parameters packed in an value object.
     * 
     * @return The value object containing the retrieved object or an error.
     * @throws Exception 
     */
    public WADOResponseObject getWADOObject(WADORequestObject reqVO) throws Exception {
        long t1 = System.currentTimeMillis();
        WADOResponseObject resp = support.getWADOObject(reqVO);
        if (support.isAuditLogEnabled(reqVO)) {
//            logExport(reqVO, resp);
        } else {
            log.debug("Suppress audit log! Disabled for host ip:"
                    + (support.isDisableDNS() ? reqVO.getRemoteAddr() : reqVO.getRemoteHost()));
        }
        long t2 = System.currentTimeMillis();
        log.debug("NBIA getWADOObject(): " + (t2 - t1) + "ms");
        return resp;
    }
}
