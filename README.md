# Latcraft's automation

## LatCraft's community automation tasks:

- `badge-generator` - utility script to generate conference badges from an SVG template and CSV file with attendee names.
- `aws-logo-generator` - utility script to generate AWS service logos for AWS day dashboard.
- `event-manager` - automation tasks for event publishing and support.
  - twitter

    ```console
    $ cd event-manager/tasks/notify-twitter
    $ ./gradlew clean  notifyTwitter

    :clean
    :getMasterData
    :getTwitterDataTask
    :notifyTwitter
    Tweet detected: Event = 7 July, 2016 / Docker 101 -> 748069500218662912 (https://twitter.com/latcraft/status/748069500218662912)
    Skip Past event: Event = 7 June, 2016 / Soft Skills
    ..
    BUILD SUCCESSFUL
    ```

## Preparation

Copy `gradle.properties` from `latcraft/passwords` to project root folder.
