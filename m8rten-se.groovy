@GrabResolver(name="netty snapshots", root="http://clinker.netty.io/nexus/content/repositories/snapshots")
@GrabResolver(name="OJO", root="https://oss.jfrog.org/artifactory/repo")
@Grab("io.ratpack:ratpack-groovy:0.9.13")
import static ratpack.groovy.Groovy.ratpack

/*
 * Loads configuration file for site
 */
def properties = new ConfigSlurper().parse(new File("m8rten-se.properties").text)

/*
 * Defines handlers
 */
ratpack {
    handlers {

		/*
		 * Api calls
		 */
        get("vaxthuset/api/status") {
            response.send new URL("https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?apiKey=${properties.apiKey}").text
        }

    	/*
    	 * Static stuff
    	 */
        assets "public"
    }
}