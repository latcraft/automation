package lv.latcraft.event.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.AutoClone
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY

@Canonical
@JsonInclude(NON_EMPTY)
@AutoClone
@JsonIgnoreProperties(ignoreUnknown = true)
@CompileStatic
@TypeChecked
class Event {

  @JsonProperty
  String venueId


}
