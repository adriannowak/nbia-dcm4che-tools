package org.dcm4chex.wado.dao;


import gov.nih.nci.nbia.internaldomain.GeneralImage;

import org.dcm4chex.wado.common.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import org.apache.log4j.Logger;

public class ImageDAO {
	protected Logger log = Logger.getLogger(getClass().getName());
	
	public GeneralImage getGeneralImageBySOPInstanceUid(String sopInstanceUid) {

//        DetachedCriteria criteria = DetachedCriteria.forClass(GeneralImage.class);
//		criteria.add(Restrictions.eq("id", imagePkId));
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		GeneralImage generalImage = null;

		try {
			transaction = session.beginTransaction();
			generalImage = (GeneralImage) session.createCriteria(GeneralImage.class).add(Restrictions.eq("SOPInstanceUID", sopInstanceUid)).uniqueResult();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			log.error("Image Retrieval failed" + e);
		} finally {
			session.close();
		}
		
		return generalImage;
	}	

}
