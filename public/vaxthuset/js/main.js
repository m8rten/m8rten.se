google.load('visualization', '1', {packages: ['corechart', 'line']});

$(document).ready(function() {

    google.setOnLoadCallback(updateHistoricData);
    google.setOnLoadCallback(update24hourData);

    updateCurrentTemperature();

    setInterval(function() {
        updateCurrentTemperature();
        updateImage();
        update24hourData();
    }, 60000);

    $(".title").hide();


    $("#help").click(function(){
        $(".title").toggle();
    });
});

function updateHistoricData(){
    $.ajax({
      dataType: "json",
      url: 'api/status-historic',
      type: "GET",
      success: function( response ) {
            drawHistoricData(response);
        }   
    });
}
// Called when the Visualization API is loaded. 
function drawHistoricData(historicData) {
    var data = null;
    var graph = null;

    // Create and populate a data table.
    data = new google.visualization.DataTable();
    data.addColumn('number', 'Dag');
    data.addColumn('number', 'Klockan');
    data.addColumn('number', 'C');

    console.log(historicData.length);
    var nrOfDays = Math.floor(historicData.length/24);

    for (var day = 0; day < nrOfDays; day+=1) {
        for (var hour = 0; hour < 24; hour+=1) {
            var temp = historicData[day*24+hour]
            data.addRow([day, hour, temp.temperature]);
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
        backgroundColor: "#E5DED1",
        height: "300px",
        keepAspectRatio: true,
        verticalRatio: 0.5
    };

    // Instantiate our graph object.
    graph = new links.Graph3d(document.getElementById('mygraph'));

    // Draw our graph with the created data and options
    graph.draw(data, options);
}

function updateCurrentTemperature(){
    $.ajax({
      dataType: "json",
      url: 'api/status-latest',
      type: "GET",
      success: function( response ) {
            $("#current-temperature").text(parseInt(response[0].temperature));

        }   
    });
}

function updateImage(){
    d = new Date();
    $("#status-foto").attr("src", "img/foto.jpg?"+d.getTime());
}

function update24hourData(){
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
    data.addColumn('timeofday', 'Minut');
    data.addColumn('number', 'Temp');
    data.addColumn('number', 'Ventilation');

    for (var minute = 0; minute < 240; minute+=1) {
        var status = historicData[minute];
        console.log(status)
        data.addRow([[status.hour, status.minute, 0], status.temperature, status.ventilation*5]);
    }

    var options = {
        legend: {
            position : "none"
        },
        width: "300px",
        backgroundColor: "#E5DED1",
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