package org.dcm4chex.wado.dao;


import gov.nih.nci.nbia.internaldomain.GeneralImage;

import org.dcm4chex.wado.common.HibernateUtil;
import org.hibernate.Criteria;
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
//			generalImage = (GeneralImage) session.createCriteria(GeneralImage.class).add(Restrictions.eq("SOPInstanceUID", sopInstanceUid)).uniqueResult();
			
/*			Another way of creating Criteria
 			Criteria c = session.createCriteria(GeneralImage.class);
			c.add(Restrictions.eq("SOPInstanceUID", sopInstanceUid));
			Criteria cx = c.createCriteria("generalSeries");
			cx.add(Restrictions.eq("visibility", "1"));
			generalImage = (GeneralImage) c.uniqueResult();  */
			
			Criteria crit = session.createCriteria(GeneralImage.class, "generalImage");
			crit.createAlias("generalImage.generalSeries", "generalSeries");
			crit.add(Restrictions.eq("generalImage.SOPInstanceUID", sopInstanceUid));
			crit.add(Restrictions.eq("generalSeries.visibility", "1"));
			generalImage = (GeneralImage) crit.uniqueResult();
			
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
