package lv.latcraft.event.tasks

import org.junit.Test

class CopyContactsFromEventBriteToSendGridTest extends BaseLambdaTaskTest {

  @Test
  void testContactSync() {
    new CopyContactsFromEventBriteToSendGrid().execute([:], context)
  }

}
