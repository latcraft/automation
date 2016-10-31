package lv.latcraft.event.integrations

import org.junit.Test

class GitHubTest {

  @Test
  void testGitHub() {
    println(new GitHub().getChecksum('/repos/latcraft/website/contents/data/events.json'))
  }
}
