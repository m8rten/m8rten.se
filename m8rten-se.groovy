@GrabResolver(name="netty snapshots", root="http://clinker.netty.io/nexus/content/repositories/snapshots")
@GrabResolver(name="OJO", root="https://oss.jfrog.org/artifactory/repo")
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
@Grab("io.ratpack:ratpack-groovy:0.9.13")
@Grab("io.ratpack:ratpack-jackson:0.9.13")   
import static ratpack.groovy.Groovy.ratpack
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.time.TimeCategory
import ratpack.jackson.JacksonModule          
import static ratpack.jackson.Jackson.jsonNode
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.JSON

/*
 * Loads configuration
 */
def mongolabApiKey = System.getenv('MONGOLAB_API_KEY') 
def vaxthusetAdminKey = System.getenv('VAXTHUSET_ADMIN_KEY') 

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
    bindings {                                  
        add new JacksonModule()                   
    } 

    handlers {

		/*
		 * Api calls
		 */
        get("vaxthuset/api/status-hourly") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&s={_id:-1}&q={"minute":0}&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/status-historic") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&s={_id:-1}&q={\$and:[{date:{\$gt:"2015-05-11"}},{minute:0}]}&apiKey=54SF3Z7w9BVVSE7w8C0lNCuzNnMtoPPl""").text
        }        

        get("vaxthuset/api/status-latest") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&s={_id:-1}}&l=1&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/status-latest-5") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&s={_id:-1}}&l=360&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/status-latest-1") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&s={_id:-1}}&l=60&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/highest-temperature") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&l=1&s={temperature:-1}&q={date:{\$gt:"${oneDayAgo()}"}}&apiKey=$mongolabApiKey""").text
        }

        get("vaxthuset/api/lowest-temperature") {
            response.send new URL("""https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?f={_id:0}&l=1&s={temperature:1}&q={date:{\$gt:"${oneDayAgo()}"}}&apiKey=$mongolabApiKey""").text
        }

        post("vaxthuset/api/controller") {                             
            def controller = parse jsonNode()
            if (controller.key.toString() == "\"$vaxthusetAdminKey\"") {
                def http = new HTTPBuilder("http://83.68.246.58:50508/api/controller")
                http.post(body: [key: controller.key.toString(),
                                time: controller.time.toString().toInteger()],
                                requestContentType: JSON ) { resp ->
                                    render resp.status
                }
            } else {
                render "fail"
            }
        }   

    	/*
    	 * Static stuff
    	 */
        assets "public"
    }
}

def oneDayAgo() {
    def currentDate = new Date()
    use(TimeCategory) {
        currentDate = currentDate - 1.day
    }
    currentDate.format("yyyy-MM-dd")
}