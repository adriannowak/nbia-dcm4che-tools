/*
 * Generated by XDoclet - Do not edit!
 */
package org.dcm4chex.archive.ejb.interfaces;

/**
 * Local home interface for File.
 * @xdoclet-generated at ${TODAY}
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version 2.17.3
 */
public interface FileLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/FileLocal";
   public static final String JNDI_NAME="ejb/File";

   public org.dcm4chex.archive.ejb.interfaces.FileLocal create(java.lang.String path , java.lang.String tsuid , long size , byte[] md5 , int status , org.dcm4chex.archive.ejb.interfaces.InstanceLocal instance , org.dcm4chex.archive.ejb.interfaces.FileSystemLocal filesystem)
      throws javax.ejb.CreateException;

   public java.util.Collection findFilesToCompress(long fspk, java.lang.String cuid, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findToCheckMd5(java.lang.String dirPath, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findByStatusAndFileSystem(java.lang.String dirPath, int status, java.sql.Timestamp notBefore, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findByFileSystem(java.lang.String dirPath, int offset, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesToLossyCompress(java.lang.String fsGroupId, java.lang.String cuid, java.lang.String sourceAET, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesToLossyCompress(java.lang.String fsGroupId, java.lang.String cuid, java.lang.String bodyPart, java.lang.String sourceAET, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesToLossyCompressWithExternalRetrieveAET(java.lang.String fsGroupId, java.lang.String retrieveAET, java.lang.String cuid, java.lang.String sourceAET, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesToLossyCompressWithExternalRetrieveAET(java.lang.String fsGroupId, java.lang.String retrieveAET, java.lang.String cuid, java.lang.String bodyPart, java.lang.String sourceAET, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesToLossyCompressWithCopyOnOtherFileSystemGroup(java.lang.String fsGroupId, java.lang.String otherFSGroupId, java.lang.String cuid, java.lang.String sourceAET, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesToLossyCompressWithCopyOnOtherFileSystemGroup(java.lang.String fsGroupId, java.lang.String otherFSGroupId, java.lang.String cuid, java.lang.String bodyPart, java.lang.String sourceAET, java.sql.Timestamp before, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findToSyncArchived(java.lang.String fsPath, int limit)
      throws javax.ejb.FinderException;

   public java.util.Collection findFilesOfTarFile(java.lang.String fsId, java.lang.String tarFilename)
      throws javax.ejb.FinderException;

   public org.dcm4chex.archive.ejb.interfaces.FileLocal findByPrimaryKey(java.lang.Long pk)
      throws javax.ejb.FinderException;

   public java.util.Collection selectByStatusAndFileSystem(java.util.List dirPath , int status , java.sql.Timestamp notBefore , java.sql.Timestamp before , int limit) 
      throws javax.ejb.FinderException;

   public java.sql.Timestamp minCreatedTimeOnFsWithFileStatus(java.util.List dirPath , int status) 
      throws javax.ejb.FinderException;

}