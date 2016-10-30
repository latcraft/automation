package lv.latcraft.event.integrations

class Configuration {

  private final static Properties LOCAL_PROPERTIES = new Properties()

  static {
    File localPropertiesFile = new File('local.properties')
    if (localPropertiesFile.exists()) {
      LOCAL_PROPERTIES.load(localPropertiesFile.newInputStream())
    }
    // TODO: implement read from KMS if it is available
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

  static String getDefaultSlackHookUrl() {
    getConfigProperty('latcraftSlackHookUrl')
  }

  static String getSendGridDefaultListId() {
    "362055"
  }

  static String getSendGridDefaultSenderId() {
    "37076"
  }

  static String getSendGridDefaultUnsubscribeGroupId() {
    "611"
  }

  static String getGitCommitter() {
    "Latcraft Event Manager"
  }

  static String getGitEmail() {
    "hello@latcraft.lv"
  }


}
