package lv.latcraft.event.lambda

import com.amazonaws.services.lambda.runtime.LambdaLogger
import groovy.util.logging.Log4j

@Log4j
class InternalLogger implements LambdaLogger {
  void log(String message) {
    log.info(message)
  }
}
