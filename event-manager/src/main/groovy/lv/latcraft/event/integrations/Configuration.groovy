package lv.latcraft.event.integrations

class Configuration {

  static String getEventbriteToken() {
    System.getProperty('latcraftEventbriteToken')
  }

  static String getGitHubToken() {
    System.getProperty('latcraftGitHubToken')
  }

  static String getSendGridApiKey() {
    System.getProperty('latcraftSendGridApiKey')
  }

  static String getEventDataFile() {
    System.getProperty('latcraftEventDataFile')
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

  static String getDefaultSlackHookUrl() {
    // TODO: replace hook link with system property
    "https://hooks.slack.com/services/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"
  }

}
