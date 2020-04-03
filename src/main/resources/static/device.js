
var stompClient = null;

var sessionId = null;
function setConnected(connected) {
    if (connected) {
        $("#connected").show();
        $("#disconnected").hide();
    }
    else {
        $("#connected").hide();
        $("#disconnected").show();
    }
}

function connect() {
    var socket = new SockJS('/aws-iot-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/topic/bulbstatus', function (messagePayload) {
        	var obj = JSON.parse(messagePayload.body);
        	reportDeviceBulb(obj.name, obj.value);
        });
    });
}

function reportDeviceBulb(deviceName, value) {
	if(value == 'true'){
		/* on the device.*/
		$('#'+deviceName).removeClass("off-device");
		$('#'+deviceName+'-text').text('On');
	} else {
		/* off the device.*/
		$('#'+deviceName).addClass("off-device");
		$('#'+deviceName+'-text').text('Off');
		
	}
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function getSessionID(){
	$.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/sessionid",
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
        	sessionId = data.name;        	
        },
        error: function (e) {
           
        }
	});
}

function getDeviceStatus() {
	$.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/device/getLastStatus",
        dataType: 'json',
        timeout: 600000,
        success: function (data) {
        	//"red-bulb": false, "green-bulb": false, "blue-bulb": false 
        	setDefaultDeviceStatus('red',data['red-bulb']);
        	setDefaultDeviceStatus('green',data['green-bulb']);
        	setDefaultDeviceStatus('blue',data['blue-bulb']);
        },
        error: function (e) {
            console.log(e);
        }
	});
}


function setDefaultDeviceStatus(deviceName, value) {
	if(value == 'true'){
		/* on the device.*/
		$('#'+deviceName).removeClass("off-device");
		$('#'+deviceName+'-text').text('On');
	} else {
		/* off the device.*/
		$('#'+deviceName).addClass("off-device");
		$('#'+deviceName+'-text').text('Off');
	}
}

function sendBulbStatus(bulbType, status){
	var bulbTypeUrl = '/device/'+bulbType+'BulbStatus';
	$.ajax({
        type: "GET",
        contentType: "application/json",
        url: bulbTypeUrl,
        dataType: 'json',
        data : "status="+encodeURIComponent(status),
        timeout: 600000,
        success: function (data) {
        	console.log('test');
        	sessionId = data.name;        	
        	console.log('test');
        },
        error: function (e) {
        	console.log(e);
        }
	});
}



function onReportButton(obj){
	var deviceName = $(obj).attr('id');
	var deviceStatus = $(obj).hasClass("off-device");
	var status = deviceStatus ? 'Off' : 'On'; 
	var status_b = deviceStatus ? 'false' : 'true'; 
	console.log(deviceName + ' is on/off :'+ deviceStatus+ ' ---- '+status)
	sendBulbStatus(deviceName,status_b);
	$('#'+deviceName+'-text').text(status);
}



$(function () {
	
	connect();
	$( "#red").click(function() { onReportButton(this); });
    $( "#green").click(function() { onReportButton(this); });
    $( "#blue").click(function() { onReportButton(this); });
    
    getDeviceStatus();
});
