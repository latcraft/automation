package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import lv.latcraft.event.utils.FileMethods

class PublishAnnouncementOnTwitter extends BaseTask {

  Map<String, String> execute(Map<String, String> input, Context context) {
    File twitterFile = FileMethods.temporaryFile("twitter", ".json")
  }

//  ext {
//
//
//    getTwitterTweets = {
//      new JsonSlurper().parse(twitterFile)
//    }
//
//    twitter = { Closure cl ->
//      Twitter client = new TwitterFactory().getInstance()
//      client.setOAuthConsumer(latcraftTwitterConsumerKey, latcraftTwitterConsumerSecret)
//
//      if (!latcraftTwitterOAuthToken || !latcraftTwitterOAuthSecret) {
//        AccessToken accessToken = null;
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        while (null == accessToken) {
//          def requestToken = client.getOAuthRequestToken()
//          println("Open the following URL and grant access to your account:");
//          println(requestToken.getAuthorizationURL());
//          println("Press enter.");
//          br.readLine()
//
//          println("Enter the verify(if available) and hit enter.");
//          println("Please ignore Gradle 'Building' text. Just type it.");
//          println("[verify]:");
//          String verify = br.readLine();
//          try {
//            if (verify.length() > 0) {
//              accessToken = client.getOAuthAccessToken(requestToken, verify);
//            } else {
//              accessToken = client.getOAuthAccessToken();
//            }
//          } catch (TwitterException te) {
//            if (401 == te.getStatusCode()) {
//              System.out.println("Unable to get the access token.");
//            } else {
//              te.printStackTrace();
//            }
//          }
//          client.verifyCredentials()
//        }
//
//        //persist to the accessToken for future reference.
//        latcraftTwitterOAuthToken = accessToken.getToken()
//        latcraftTwitterOAuthSecret = accessToken.getTokenSecret()
//        println("//**********************************************")
//        println("// Please update gradle.properties")
//        println("// latcraftTwitterOAuthToken=${latcraftTwitterOAuthToken}")
//        println("// latcraftTwitterOAuthSecret=${latcraftTwitterOAuthSecret}")
//      }
//
//      client.setOAuthAccessToken(new AccessToken(latcraftTwitterOAuthToken, latcraftTwitterOAuthSecret))
//
//      try {
//        cl(client)
//      } catch (TwitterException e) {
//        throw new GradleException("Error details: ${e.getErrorCode()} (HTTP ${e.getStatusCode()}) : ${e.getMessage()} (" +
//          "Access level: ${e.getAccessLevel()}, " +
//          "Network: ${e.isCausedByNetworkIssue()}" +
//          ")")
//      }
//    }
//  }
//
//  def getTwitterData() {
//    buildDir.mkdirs()
//    def result = []
//
//    twitter { Twitter api ->
//      def timeline = api.getUserTimeline(new Paging(1).count(latcraftTwitterPagingCount.toInteger()).sinceId(latcraftTwitterPagingSinceBigBangMomentId.toLong())
//      )
//      def data = timeline.toArray()
//      twitterFile.text = dumpJson(data)
//      result = data
//    }
//
//    return result
//  }
//
//  task getTwitterDataTask << {
//    getTwitterData()
//  }
//
//  getTwitterDataTask.outputs.temporaryFile twitterFile
//
////getTwitterDataTask.logging.captureStandardOutput LogLevel.INFO
////getTwitterDataTask.logging.captureStandardError LogLevel.INFO
//
//
//  task notifyTwitter(dependsOn: [getMasterData, getTwitterDataTask]) << {
//
//    def twitter_tweets = getTwitterTweets()
//    def updated_events = getEventData().collect { event ->
//
//      // Calculate unique event ID used to distinguish this event from others in various data sources.
//      String eventId = event.tickets
//
//      // Find EventBrite event ID if it is not yet set or missing.
//      String tweetId = event.tweetId
//      if (!tweetId) {
//        twitter_tweets.find { tweet ->
//          if (tweet.URLEntities.findAll { eventId && it.expandedURL == eventId}
//            && !tweet.retweeted
//            && !tweet.contributors) {
//            tweetId = tweet.id
//          }
//        }
//      }
//
//      if (tweetId) {
//        println("Tweet detected: Event = ${event.date} / ${event.theme} -> ${tweetId} (https://twitter.com/latcraft/status/${tweetId})")
//        event.tweetId = tweetId
//      } else if(isFutureEvent(event)){
//        println("Publish Future event: Event = ${event.date} / ${event.theme}")
//        twitter { Twitter api ->
//          String status_text = "${event.tweet_text ?: 'Lets talk about'} ${event.theme}. Join us on the ${event.date}!\n${event.tickets}"
//          // TODO: get inforgraphics data
//          def infographics = new File("/home/leonids/Downloads/CmGtLojWgAAQMlv.jpg:large.jpeg")
//          def status = api.updateStatus(new StatusUpdate(status_text).media(infographics))
//          tweetId = status.id
//        }
//        println("\t tweetId -> ${tweetId}")
//        event.tweetId = tweetId
//      } else {
//        println("Skip Past event: Event = ${event.date} / ${event.theme}")
//      }
//
//      event
//    }
//
//    eventFile.text = dumpJson(updated_events)
//  }
//
//
//  task twitterUpdateMasterData() << {
//    println("twitterUpdateMasterData")
//  }
//
//// TODO: uncomment this to make it publish master data
////task twitterUpdateMasterData(dependsOn: [updateMasterData])
//
//  twitterUpdateMasterData.mustRunAfter notifyTwitter
//
//  task build(dependsOn: [notifyTwitter, twitterUpdateMasterData])

}
