package lv.latcraft.event.integrations

import com.amazonaws.services.kms.AWSKMS
import com.amazonaws.services.kms.AWSKMSClient
import com.amazonaws.services.kms.model.DecryptRequest
import com.amazonaws.services.kms.model.DecryptResult

class Configuration {

  private final static Properties LOCAL_PROPERTIES = new Properties()

  static {
    File localPropertiesFile = new File('local.properties')
    if (localPropertiesFile.exists()) {
      LOCAL_PROPERTIES.load(localPropertiesFile.newInputStream())
    }
    // TODO: implement read from KMS if it is available
  }

  private static Properties decrypt() {
    Properties props = new Properties()
    DecryptRequest decryptRequest = new DecryptRequest()
    // TODO: decrypt data in lambda context
    DecryptResult result = new AWSKMSClient().decrypt(decryptRequest)
    props.load(new ByteArrayInputStream(result.plaintext.array()))
    props
  }

  private static String getConfigProperty(String name) {
    System.getProperty(name) ?: LOCAL_PROPERTIES.get(name)
  }

  static String getEventbriteToken() {
    getConfigProperty('latcraftEventbriteToken')
  }

  static String getGitHubToken() {
    getConfigProperty('latcraftGitHubToken')
  }

  static String getSendGridApiKey() {
    getConfigProperty('latcraftSendGridApiKey')
  }

  static String getEventDataFile() {
    getConfigProperty('latcraftEventDataFile')
  }

  static String getEventbriteTemplateBaseDir() {
    getConfigProperty('latcraftEventBriteTemplateBaseDir')
  }

  static String getEventbriteVenueId() {
    getConfigProperty('latcraftEventbriteVenueId')
  }

  static String getEventbriteCapacity() {
    getConfigProperty('latcraftEventbriteCapacity')
  }

  static String getEventbriteOrganizerId() {
    getConfigProperty('latcraftEventbriteOrganizerId')
  }

  static String getEventbriteLogoId() {
    getConfigProperty('latcraftEventbriteLogoId')
  }

  static String getEventbriteCategoryId() {
    getConfigProperty('latcraftEventbriteCategoryId')
  }

  static String getEventbriteSubcategoryId() {
    getConfigProperty('latcraftEventbriteSubcategoryId')
  }

  static String getEventbriteFormatId() {
    getConfigProperty('latcraftEventbriteFormatId')
  }

  static String getDefaultSlackHookUrl() {
    getConfigProperty('latcraftSlackHookUrl')
  }

  static String getSendGridDefaultListId() {
    getConfigProperty('latcraftSendGridDefaultListId')
  }

  static String getSendGridDefaultSenderId() {
    getConfigProperty('latcraftSendGridDefaultSenderId')
  }

  static String getSendGridDefaultUnsubscribeGroupId() {
    getConfigProperty('latcraftSendGridDefaultUnsubscribeGroupId')
  }

  static String getNewsletterTemplateBaseDir() {
    getConfigProperty('latcraftNewsletterTemplateBaseDir')
  }

  static String getAwsAccessKeyId() {
    getConfigProperty('latcraftAwsAccessKeyId')
  }

  static String getAwsSecretAccessKey() {
    getConfigProperty('latcraftAwsSecretAccessKey')
  }

  static String getGitCommitter() {
    getConfigProperty('latcraftGitCommitter')
  }

  static String getGitEmail() {
    getConfigProperty('latcraftGitEmail')
  }

}
