google.load('visualization', '1', {packages: ['corechart', 'line']});

$(document).ready(function() {

    google.setOnLoadCallback(updateHistoricData);
    google.setOnLoadCallback(update24hourData);

    updateCurrentTemperature();
    updateHighestTemperature();
    updateLowestTemperature();

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

    var nrOfDays = Math.floor(historicData.length/24);

    var currentDate = new Date();
    currentDate.setHours(0,0,0,0);
    for (var i = 0; i < historicData.length; i+=1) {
        var temp = historicData[i];
        var thenDate = new Date(temp.date);
        thenDate = new Date(thenDate.getTime()+(thenDate.getTimezoneOffset()*60000));
        thenDate.setHours(0,0,0,0);
        var diff =  Math.floor(( currentDate - thenDate ) / 86400000)
        data.addRow([diff, temp.hour, temp.temperature]);
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

function updateHighestTemperature(){
    $.ajax({
      dataType: "json",
      url: 'api/highest-temperature',
      type: "GET",
      success: function( response ) {
            $("#highest-temperature").text(parseInt(response[0].temperature));

        }   
    });
}

function updateLowestTemperature(){
    $.ajax({
      dataType: "json",
      url: 'api/lowest-temperature',
      type: "GET",
      success: function( response ) {
            $("#lowest-temperature").text(parseInt(response[0].temperature));

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

    for (var minute = 0; minute < 360; minute+=1) {
        var status = historicData[minute];
        data.addRow([[status.hour, status.minute, 0], status.temperature-0.7*status.ventilation, status.temperature]);
    }

    var options = {
        legend: {
            position : "none"
        },
        width: "300px",
        backgroundColor: "#E5DED1",
        height: "300px",
        series: {
            0: {color: '#913730',
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