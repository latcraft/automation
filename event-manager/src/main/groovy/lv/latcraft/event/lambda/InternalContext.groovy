package lv.latcraft.event.lambda

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

class InternalContext implements Context {

  String getAwsRequestId() {
    return null
  }

  String getLogGroupName() {
    return null
  }

  String getLogStreamName() {
    return null
  }

  String getFunctionName() {
    return null
  }

  String getFunctionVersion() {
    return null
  }

  String getInvokedFunctionArn() {
    return null
  }

  CognitoIdentity getIdentity() {
    return null
  }

  ClientContext getClientContext() {
    return null
  }

  int getRemainingTimeInMillis() {
    return 0
  }

  int getMemoryLimitInMB() {
    return 0
  }

  LambdaLogger getLogger() {
    return null
  }

}
