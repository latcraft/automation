package lv.latcraft.event.tasks

import org.junit.Test

class CopyContactsFromEventBriteToSendGridTest {

  @Test
  void testContactSync() {
    new CopyContactsFromEventBriteToSendGrid().execute()
  }

}
