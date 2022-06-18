var gateway = `ws://${window.location.hostname}/ws`;
var websocket;
var yStepCurrentAnim = 0;
var canMoveX = new Boolean(false);
var canMoveY = new Boolean(true);

window.addEventListener('load', onload);

function onload(event) {
    initWebSocket();
}

function initWebSocket() {
    console.log('Trying to open a WebSocket connection');
    websocket = new WebSocket(gateway);
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onmessage = onMessage;
}

function onOpen(event) {
    console.log('Connection opened');
}

function onClose(event) {
    console.log('Connection closed');
    setTimeout(initWebSocket, 2000);
}

function onMessage(event) {
    console.log('onMessage');
    console.log('event.data' + event.data);
    let str = event.data;
    let command = str.substring(0, str.indexOf(":"));
    let value = str.substring(str.indexOf(":") + 1, str.lenght);
    console.log('command' + command);
    console.log('value' + value);

    if (command == "X") {
        updateXInput(parseInt(value));
    } else if (command == "Y") {
        updateYInput(parseInt(value));
    } else if (command == "S") {
        let ssid = value.substring(0, value.indexOf("|"));
        let pass = value.substring(value.indexOf("|") + 1, value.lenght);
        updateSWifiInput(ssid, pass);
    } else if (command == "MX") {
        if (value == "1") {
            canMoveX = true;
        } else if (value == "0") {
            canMoveX = false;
        }
    } else if (command == "MY") {
        if (value == "1") {
            canMoveY = true;
        } else if (value == "0") {
            canMoveY = false;
        }
    }
   
}

function submitForm() {
    let ssid = document.getElementById("ssid").value;
    let pass = document.getElementById("pass").value;
    websocket.send("LOGIN" + "|" + ssid + ":" + pass);
}

function submitXAxis(val) {
    let degree = val;
    websocket.send("ROTATE" + "|" + "X" + ":" + degree);
}

function submitYAxis(val) {
    let degree = val;
    websocket.send("ROTATE" + "|" + "Y" + ":" + degree);
}

function updateYInput(val) { 
    document.getElementById('y_input').value = val;
    document.getElementById('range_input_y').value = val;
}

function updateXInput(val) {  
    document.getElementById('x_input').value = val;
    document.getElementById('range_input_x').value = val;  
}

function updateSWifiInput(ssid, localIp) {
    let staSsidElem         = document.getElementById("wifiStaSsid");
    let staIpElem           = document.getElementById("wifiStaIp");

    console.log(ssid);
    console.log(localIp);

    staSsidElem.innerHTML  += String(ssid);
    staIpElem.innerHTML    += String(localIp);
    
    document.getElementById("wifi_credentials_id").style.display = "none";
    document.getElementById("wifi_status_id").style.display = "flex";
}

function submitSWifiReset() {
    websocket.send("RESET" + "|true");
}

function submitXInput(val) {
    if (val >= -180 && val <= 180 && canMoveX) {
        canMoveX = false;
        updateXInput(val)
        submitXAxis(val);
        canMoveX = true;
    }
}

function submitYInput(val) {
    if (val >= -90 && val <= 90 && canMoveY) {
        canMoveY = false;
        updateYInput(val)
        submitYAxis(val);
        moveTeleSvg(val);
        canMoveY = true;
    }
}

async function moveTeleSvg(val) {
    if (val >= -90 && val <= 90) {
        document.getElementById("svg_img_tele").getSVGDocument("tele_top").getElementById("tele_top").style.transform = 'rotate(' + -val + 'deg)';
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
