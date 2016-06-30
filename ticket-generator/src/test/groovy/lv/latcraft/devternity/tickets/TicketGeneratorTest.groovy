package lv.latcraft.devternity.tickets

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import groovy.mock.interceptor.MockFor
import org.junit.Test

class TicketGeneratorTest {

  @Test
  void testGenerator() {
    TicketGenerator.generate([
      name: 'Andrey Adamovich',
      company: 'Aestas/IT',
      email: 'andrey@aestasit.com',
    ], context)
  }

  private static Context getContext() {
    def context = new MockFor(Context)
    def logger = new MockFor(LambdaLogger)
    logger.demand.log { String message -> System.out.println message }
    context.demand.getLogger { logger.proxyInstance() }
    (Context) context.proxyInstance()
  }

}
