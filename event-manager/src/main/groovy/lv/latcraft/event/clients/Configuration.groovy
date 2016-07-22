package lv.latcraft.event.clients

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


  //  defaultSenderId = "37076"
//  defaultListId = "362055"
//  defaultUnsubscribeGroupId = "611"


  static String getGitCommitter() {
    "Latcraft Event Manager"
  }

  static String getGitEmail() {
    "hello@latcraft.lv"
  }

}
