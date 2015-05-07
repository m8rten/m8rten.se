@GrabResolver(name="netty snapshots", root="http://clinker.netty.io/nexus/content/repositories/snapshots")
@GrabResolver(name="OJO", root="https://oss.jfrog.org/artifactory/repo")
@Grab("io.ratpack:ratpack-groovy:0.9.13")
import static ratpack.groovy.Groovy.ratpack
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
/*
 * Loads configuration
 */
def mongolabApiKey = System.getenv('MONGOLAB_API_KEY') 

/*
 * Updates image every 60 s
 */
def imageUpdater = Thread.start {
    while(true){
        def url = new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/bilder?q={"_id":"foto"}&apiKey=$mongolabApiKey""")
        new File('public/vaxthuset/img/foto.jpg').bytes = new JsonSlurper().parseText(url.text)[0].base64.decodeBase64()        
        sleep(60000)
    }
}

/*
 * Updates image every 60 s
 */
def gifUpdater = Thread.start {
    while(true){
        def url = new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/bilder?q={"_id":"gif"}&apiKey=$mongolabApiKey""")
        new File('public/vaxthuset/img/animation.gif').bytes = new JsonSlurper().parseText(url.text)[0].base64.decodeBase64()        
        sleep(3600000)
    }
}

/*
 * Defines handlers
 */
ratpack {
    handlers {

		/*
		 * Api calls
		 */
        get("vaxthuset/api/status-hourly") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?q={"minute":0}&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/status-latest") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?s={_id:-1}}&l=1&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/status-latest-24") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?s={_id:-1}}&l=60&apiKey=$mongolabApiKey""").text
        }       

    	/*
    	 * Static stuff
    	 */
        assets "public"
    }
}