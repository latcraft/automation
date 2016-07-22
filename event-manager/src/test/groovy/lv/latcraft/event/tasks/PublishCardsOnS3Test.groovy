package lv.latcraft.event.tasks

import org.junit.Test

class PublishCardsOnS3Test {

  @Test
  void testGeneration() {
     new PublishCardsOnS3().execute()
  }

}
