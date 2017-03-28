
Automation tasks for event publishing and support.

## Event publishing

> **NOTE:** For running automation tasks locally, copy contents of the `gradle.properties` file from the `latcraft/passwords` project to the `local.propeties` file.

Event publishing process consists of the following steps: 
 
0. **Manual:** Create data record for the event inside `events.json` (https://github.com/latcraft/website/blob/master/data/events.json) and push to Git.

   > **WARNING:** make sure that you removed EventBrite's eventId from the new event record. Otherwise, there is a risk of data corruption.
   
1. **Automated:** Publish event on EventBrite using the following task: 

        gradlew publishEventOnEventBrite

   It is safe to rerun this task several times after the first publication if there are any changes in the event description.

2. **Automated:** Generate event cards using the following task:
   
        gradlew publishCardsOnS3
        
    Cards will be generated on AWS S3 share. Links to the cards will be published inside `#craftbot` channel in Slack.    
    
    > **WARNING:** It is responsibility of the event lead to share cards at given times on social networks. 
    
    Different cards have different purposes (check the *Event cards* section below). 
    
    It is safe to run this task several times if there are any updates to the event data.     
        
3. **Automated:** Copy all contacts from EventBrite to SendGrid using the following task.

        gradlew copyContactsFromEventBriteToSendGrid

4. **Automated:** Create invitation e-mail on SendGrid using the following task:
  
        gradlew publishCampaignOnSendGrid
        
    It is safe to run this task several times.    
        
5. **Automated:** Send the invitation letter using the following task:
    
        gradlew sendCampaignOnSendGrid

    > **WARNING:** This task can executed only once and is not reversible.
    
6. **Manual:** Tweet about the event and speakers using LatCraft Twitter account and cards generated above. Pin the tweet about event.

7. **Manual:** Create Lanyrd event.

8. **Manual:** Create Facebook event.

9. **Manual:** Create LinkedIn post.

## Event cards

- normal_event_card_v1
- normal_event_card_v2
- normal_event_card_v3
- normal_event_facebook_background_v1
- normal_event_facebook_background_v2
- workshop_event_card_v1
- workshop_event_card_v2
- workshop_facebook_background_v1


