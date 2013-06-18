package org.dcm4chex.wado.mbean;

import gov.nih.nci.nbia.internaldomain.GeneralImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.management.MBeanServer;
import javax.servlet.http.HttpServletResponse;

import org.dcm4che.data.Dataset;
import org.dcm4che.dict.DictionaryFactory;
import org.dcm4che.dict.Tags;
import org.dcm4chex.archive.ejb.interfaces.AEDTO;
import org.dcm4chex.archive.exceptions.UnknownAETException;
import org.dcm4chex.wado.common.WADORequestObject;
import org.dcm4chex.wado.common.WADOResponseObject;
import org.dcm4chex.wado.dao.ImageDAO;
import org.dcm4chex.wado.mbean.WADOSupport.ImageCachingException;
import org.dcm4chex.wado.mbean.WADOSupport.NeedRedirectionException;
import org.dcm4chex.wado.mbean.WADOSupport.NoImageException;
import org.dcm4chex.wado.mbean.cache.WADOCache;
import org.dcm4chex.wado.mbean.cache.WADOCacheImpl;

public class NbiaWADOSupport extends WADOSupport {
	
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NbiaWADOSupport.class);


    public NbiaWADOSupport(MBeanServer mbServer) {
        super(mbServer);
        
        disableCache = true;
    }
    
    public WADOResponseObject getWADOObject(WADORequestObject req)
    	    throws Exception {
	        log.info("Get NBIA WADO object for " + req.getObjectUID());
	        
	        Dataset dsQ = dof.newDataset();
            dsQ.putUI(Tags.SOPInstanceUID, req.getObjectUID());
            dsQ.putUI(Tags.SOPClassUID);
            dsQ.putLO(Tags.PatientID);
            dsQ.putPN(Tags.PatientName);
            dsQ.putUI(Tags.StudyInstanceUID);
            dsQ.putUI(Tags.SeriesInstanceUID);
            dsQ.putUI(Tags.MIMETypeOfEncapsulatedDocument);
            dsQ.putCS(Tags.QueryRetrieveLevel, "IMAGE");
	        
	        ImageDAO imageDAO = new ImageDAO();
	        GeneralImage generalImage = imageDAO.getGeneralImageBySOPInstanceUid(req.getObjectUID());
	        
	        if ( generalImage == null ) {
	        	log.error("Cant get DICOM Object file reference for " + req.getObjectUID());
	        	
	        	return new WADOStreamResponseObjectImpl(null, CONTENT_TYPE_HTML,
	                    HttpServletResponse.SC_NOT_FOUND,
	                    "DICOM object not found! objectUID:" + req.getObjectUID());
	        }

	        log.error("Print General Image Content" + generalImage.getFilename() );

	        objectFileName = generalImage.getFilename();
	        String contentType = req.getContentTypes().get(0).toString();
	        log.debug("preferred ContentType:" + contentType);
	        WADOResponseObject resp = null;
	        if (contentType == null) {
	            return new WADOStreamResponseObjectImpl(null, CONTENT_TYPE_HTML,
	                    HttpServletResponse.SC_NOT_ACCEPTABLE,
	                    "Requested object can not be served as requested content type! Requested contentType(s):"
	                    + req.getRequest().getParameter("contentType"));
	        }
//	        req.setObjectInfo(objectDs);
	        if (CONTENT_TYPE_JPEG.equals(contentType) || CONTENT_TYPE_PNG.equals(contentType)
	                || CONTENT_TYPE_PNG16.equals(contentType)) {
	            return this.handleImage(req, contentType);
	        } else if (CONTENT_TYPE_DICOM.equals(contentType)) {
	            return handleDicom(req); // audit log is done in handleDicom to
	            // avoid extra query.
	        }
	        File file = null;
	        try {
	            file = this.getDICOMFile(req.getStudyUID(), req.getSeriesUID(), req
	                    .getObjectUID());
	            if (file == null) {
	                if (log.isDebugEnabled())
	                    log.debug("Dicom object not found: " + req);
	                return new WADOStreamResponseObjectImpl(null, contentType,
	                        HttpServletResponse.SC_NOT_FOUND,
	                "DICOM object not found!");
	            }
	        } catch (IOException x) {
	            log.error("Exception in getWADOObject: " + x.getMessage(), x);
	            return new WADOStreamResponseObjectImpl(null, contentType,
	                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	            "Unexpected error! Cant get file");
	        } catch (NeedRedirectionException nre) {
	            return handleNeedRedirectException(req, contentType, nre);
	        }
//	        String sopCuid = objectDs.getString(Tags.SOPClassUID);
	        if (CONTENT_TYPE_DICOM_XML.equals(contentType)) {
	            if (dict == null)
	                dict = DictionaryFactory.getInstance().getDefaultTagDictionary();
	            resp = handleTextTransform(req, file, contentTypeDicomXML,
	                    getDicomXslURL(), dict);
/*	        } else if ( this.getEncapsulatedSopCuids().containsValue(sopCuid)) {
	            resp = handleEncaps(file, contentType);
	        } else if ( this.getVideoSopCuids().containsValue(sopCuid)){
	            resp = handleVideo(file);   */
	        } else if (CONTENT_TYPE_HTML.equals(contentType)) {
	            resp = handleTextTransform(req, file, contentType, getHtmlXslURL(),
	                    null);
	        } else if (CONTENT_TYPE_XHTML.equals(contentType)) {
	            resp = handleTextTransform(req, file, contentType,
	                    getXHtmlXslURL(), null);
	        } else if (CONTENT_TYPE_XML.equals(contentType)) {
	            resp = handleTextTransform(req, file, CONTENT_TYPE_XML,
	                    getXmlXslURL(), null);
	        } else {
	            log.debug("Content type not supported! :" + contentType
	                    + "\nrequested contentType(s):" + req.getContentTypes()
	                    + " SOP Class UID:"); // + objectDs.getString(Tags.SOPClassUID));
	            resp = new WADOStreamResponseObjectImpl(null, CONTENT_TYPE_DICOM,
	                    HttpServletResponse.SC_NOT_IMPLEMENTED,
	                    "This method is not implemented for requested (preferred) content type!"
	                    + contentType);
	        }
	        return resp;
     }
    
    public File getDICOMFile(String studyUID, String seriesUID,
            String instanceUID) throws IOException, NeedRedirectionException {
        Object dicomObject = null;
        try {
//            dicomObject = new File("/nbia_storage/0000000000/000/000/001.dcm");
        	String filename = objectFileName.replace("\\", "/");
        	
	        if( filename.toLowerCase().startsWith("c:/"))
	        	filename = filename.substring(2);
        	log.error("replaced \\" + filename );

            dicomObject = new File(filename);

        } catch (Exception e) {
            if (e.getCause() instanceof UnknownAETException) {
                //Indicate NeedRedirect with unknown external retrieve AET
                throw new NeedRedirectionException(
                        "Can't redirect WADO request to external retrieve AET! Unknown AET:"
                        +e.getCause().getMessage(), null);
            }
            log.error("Failed to get DICOM file:" + instanceUID, e);
        }
        if (dicomObject == null)
            return null; // not found!
        if (dicomObject instanceof File)
            return (File) dicomObject; // We have the File!
        if (dicomObject instanceof AEDTO) {
            AEDTO ae = (AEDTO) dicomObject;
            if ("DICOM_QR_ONLY".equals(ae.getWadoURL())) {
                return fetchFromExternalRetrieveAET(ae, studyUID, seriesUID, instanceUID);
            }
            throw new NeedRedirectionException(null, (AEDTO) dicomObject);
        }
        return null;
    }
    
    public WADOResponseObject handleDicom(WADORequestObject req) {
        File file = null;
        try {
            file = this.getDICOMFile(req.getStudyUID(), req.getSeriesUID(), req
                    .getObjectUID());
            if (file == null) {
                if (log.isDebugEnabled())
                    log.debug("Dicom object not found: " + req);
                return new WADOStreamResponseObjectImpl(null,
                        CONTENT_TYPE_DICOM, HttpServletResponse.SC_NOT_FOUND,
                "DICOM object not found!");
            }
        } catch (IOException x) {
            log.error("Exception in handleDicom: " + x.getMessage(), x);
            return new WADOStreamResponseObjectImpl(null, CONTENT_TYPE_DICOM,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Unexpected error! Cant get dicom object");
        } catch (NeedRedirectionException nre) {
            return handleNeedRedirectException(req, CONTENT_TYPE_DICOM, nre);
        }

        try {
            WADOStreamResponseObjectImpl resp = new WADOStreamResponseObjectImpl(
                    new FileInputStream(file), CONTENT_TYPE_DICOM,
                    HttpServletResponse.SC_OK, null);
            log.info("Original Dicom object file retrieved (useOrig=true) objectUID:"
                    + req.getObjectUID());
            Dataset ds = req.getObjectInfo();
//            ds.putPN(Tags.PatientName, ds.getString(Tags.PatientName)
//                    + " (orig)");
            resp.setPatInfo(ds);
            return resp;
        } catch (FileNotFoundException e) {
            log.error("Dicom File not found (useOrig=true)! file:" + file);
            return new WADOStreamResponseObjectImpl(null,
                    CONTENT_TYPE_DICOM,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Unexpected error! Cant get dicom object");
        }
    }
    

}
