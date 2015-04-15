@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
import groovy.time.TimeCategory
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import java.util.Random  

def http = new HTTPBuilder( 'https://api.mongolab.com/api/1/databases/vaxthuset/collections/status?apiKey=54SF3Z7w9BVVSE7w8C0lNCuzNnMtoPPl' )

Random rand = new Random() 



def currentDate = new Date()

currentDate.clearTime()

for(day = 0; day < 10; day++){

	for(hour=0; hour <= 23; hour++) {
		
		for(minute=0; minute <= 59; minute++) {

			http.request( POST, JSON ) { req ->
			    body = [temperature: 5 + Math.abs(hour-12) + rand.nextInt(3),
			    		date:currentDate.format("MM/dd/yyyy'T'HH:mm:ss.SSS'Z'"),
			    		hour: hour,
			    		minute: minute]

			     response.success = { resp, json ->
			        // response handling here
			    }
			}

			use(TimeCategory) {
				currentDate = currentDate + 1.minute
			}
		}

		use(TimeCategory) {
			currentDate = currentDate + 1.hour
		}

	}

	use(TimeCategory) {
		currentDate = currentDate + 1.day
	}
}