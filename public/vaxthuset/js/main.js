var data = null;
var graph = null;

google.load("visualization", "1");

// Set callback to run when API is loaded
google.setOnLoadCallback(fetchHistoricData);

function custom(x, y) {
    return (Math.sin(x/50) * Math.cos(y/50) * 50 + 50);
}

// Called when the Visualization API is loaded.
function drawVisualization(historicData) {
    // Create and populate a data table.
    data = new google.visualization.DataTable();
    data.addColumn('number', 'Dag');
    data.addColumn('number', 'Klockan');
    data.addColumn('number', 'C');

    //for (var hour =)

    // create some nice looking data with sin/cos
    var steps = 50;  // number of datapoints will be steps*steps
    var axisMax = 314;
    var axisStep = axisMax / steps;

    var nrOfDays = Math.floor(historicData.length/24);

    for (var day = 0; day < nrOfDays; day+=1) {
        for (var hour = 0; hour < 24; hour+=1) {
            var temp = historicData[day*24+hour]
            data.addRow([day-nrOfDays, hour, temp.temperature]);
        }
    }

    // specify options
    var options = {
        backgroundColor: {
            stroke: '#FFFFFF'
        },
        width:  "600px",
        height: "600px",
        style: "surface",
        showPerspective: true,
        showGrid: true,
        showShadow: false,
        keepAspectRatio: true,
        verticalRatio: 0.5
    };

    // Instantiate our graph object.
    graph = new links.Graph3d(document.getElementById('mygraph'));

    // Draw our graph with the created data and options
    graph.draw(data, options);
}

function fetchHistoricData(){
    $.ajax({
      dataType: "json",
      url: 'api/status-hourly',
      type: "GET",
      success: function( response ) {
            drawVisualization(response);
        }   
    });
}