package lv.latcraft.event.lambda

import com.amazonaws.services.lambda.runtime.Client

class InternalClient implements Client {

  String getInstallationId() {
    ""
  }

  String getAppTitle() {
    ""
  }

  String getAppVersionName() {
    ""
  }

  String getAppVersionCode() {
    ""
  }

  String getAppPackageName() {
    ""
  }

}
