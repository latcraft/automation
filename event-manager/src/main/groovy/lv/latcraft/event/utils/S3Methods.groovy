package lv.latcraft.event.utils

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.Permission
import com.amazonaws.services.s3.model.PutObjectRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@TypeChecked
@CompileStatic
class S3Methods {

  static final String BUCKET_NAME = 'latcraft-images'

  static AmazonS3Client getS3() {
    new AmazonS3Client()
  }

  static AccessControlList anyoneWithTheLink() {
    AccessControlList acl = new AccessControlList()
    acl.grantPermission(GroupGrantee.AllUsers, Permission.Read)
    acl
  }

  static PutObjectRequest putRequest(String targetFileName, File localFile) {
    new PutObjectRequest(
      BUCKET_NAME,
      targetFileName,
      localFile
    ).withAccessControlList(anyoneWithTheLink())
  }

}
