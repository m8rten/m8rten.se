var data = null;
var graph = null;

google.load('visualization', '1', {packages: ['corechart', 'line']});

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
        style: "surface",
        showPerspective: true,
        showGrid: true,
        showShadow: false,
        width: "300px",
        backgroundColor: "#ECE9C6",
        height: "300px",
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


function fetchStatus(){
    $.ajax({
      dataType: "json",
      url: 'api/status-latest',
      type: "GET",
      success: function( response ) {
            $("#current-temperature").text(response[0].temperature);

        }   
    });
}

function updateImage(){
    d = new Date();
    $("img.foto").attr("src", "img/foto.jpg?"+d.getTime());
}

$(document).ready(function() {

    // Set callback to run when API is loaded
    google.setOnLoadCallback(fetchHistoricData);
    google.setOnLoadCallback(fetch24HourData);
    fetchStatus();

    setInterval(function() {
        fetchStatus();
        updateImage();
    }, 60000);
});     



function fetch24HourData(){
    $.ajax({
      dataType: "json",
      url: 'api/status-latest-24',
      type: "GET",
      success: function( response ) {
            draw24Hour(response);
        }   
    });
}

function draw24Hour(historicData) {
    var data = new google.visualization.DataTable();
    data.addColumn('number', 'Minut');
    data.addColumn('number', 'Temp');
    data.addColumn('number', 'Ventilation');

    for (var minute = 0; minute < 60; minute+=1) {
        var status = historicData[minute];
        data.addRow([minute, status.temperature, status.ventilation*5]);
    }

    var options = {
        legend: {
            position : "none"
        },
        width: "300px",
        backgroundColor: "#ECE9C6",
        height: "300px",
        series: {
            0: {color: '#424242',
                curveType: 'function'
            },
            1: {color: '#424242',
                curveType: 'function'
            }
        }
    };

      var chart = new google.visualization.LineChart(document.getElementById('24h-graph'));
      chart.draw(data, options);
}