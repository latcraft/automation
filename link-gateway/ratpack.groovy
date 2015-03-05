@Grab("org.slf4j:slf4j-simple:1.7.5")
@Grab("io.ratpack:ratpack-groovy:0.9.11")
import static ratpack.groovy.Groovy.ratpack

ratpack {
  handlers {
    get("crowd") {
      response.send new Date().toString()
    }
  }
}


