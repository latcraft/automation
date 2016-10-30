package lv.latcraft.event.lambda

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

class InternalContext implements Context {

  String getAwsRequestId() {
    null
  }

  String getLogGroupName() {
    null
  }

  String getLogStreamName() {
    null
  }

  String getFunctionName() {
    null
  }

  String getFunctionVersion() {
    null
  }

  String getInvokedFunctionArn() {
    null
  }

  CognitoIdentity getIdentity() {
    null
  }

  ClientContext getClientContext() {
    new InternalClientContext()
  }

  int getRemainingTimeInMillis() {
    0
  }

  int getMemoryLimitInMB() {
    0
  }

  LambdaLogger getLogger() {
    new InternalLogger()
  }

}
