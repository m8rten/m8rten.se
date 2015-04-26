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
 * The photo catalog
 */
def photoCatalog = []


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
 * Updates daily foto two times every 
 */
def dailyFotoUpdater = Thread.start {

    /*
     * Load all photos at startup
     */
    def photos = new JsonSlurper().parseText(new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/dagliga-fotot?apiKey=$mongolabApiKey""").text)
    for(i=0; i < photos.size(); i++){
        photoCatalog.add(photos[i].date)
        new File("public/vaxthuset/img/daily-photo${i}.jpg").bytes = photos[i].base64.decodeBase64()
    }

    /*
     * Adds latest photos to catalog if new
     */
    while(true){
        def latestPhoto = new JsonSlurper().parseText(new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/dagliga-fotot?s={_id:-1}&l=1&apiKey=$mongolabApiKey""").text)
        if(!latestPhoto[0].date.equals(photoCatalog.last())) {
            photoCatalog.add(latestPhoto[0].date)
            new File("public/vaxthuset/img/daily-photo${photoCatalog.size()-1}.jpg").bytes = latestPhoto[0].base64.decodeBase64()
        }

        sleep(60000)
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

        get("vaxthuset/api/daily-photos") {

            def json = new JsonBuilder()

            json {
                dates(photoCatalog)
            }

            response.send json.toPrettyString()
        }        

    	/*
    	 * Static stuff
    	 */
        assets "public"
    }
}