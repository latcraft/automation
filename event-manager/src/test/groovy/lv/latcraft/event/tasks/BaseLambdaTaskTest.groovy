package lv.latcraft.event.tasks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import groovy.mock.interceptor.StubFor

class BaseLambdaTaskTest {

  static Context getContext() {
    def context = new StubFor(Context)
    def logger = new StubFor(LambdaLogger)
    logger.demand.log(0..10) { String message -> System.out.println message }
    context.demand.getLogger(0..10) { logger.proxyInstance() }
    (Context) context.proxyInstance()
  }

}
