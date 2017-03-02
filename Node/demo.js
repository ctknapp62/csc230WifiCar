var WebSocketServer = require('ws').Server,
  wss = new WebSocketServer({ port: 4000 });

var express = require('express');
var app = express();
var clientWS;

  app.listen(2300, function() {
      console.log('2300 port for express');
  });
  //handeling URL backslash
  app.get('/', function(req, res) {
      res.send('Welcome to CSC 230 RC Car!');
  });

app.use(express.static('static'));


  //Setting up the Steering HTTP Req from HTML
  app.get('/steer/:dir', function(req, res) {
      var steeringDriver = req.params.dir;
        clientWS.send(steeringDriver);
        //Lines for debugging in repl
        console.log(req.params);
        res.send('');
  });

  wss.on('connection', function connection(ws) {
    console.log("Testing connection");
    clientWS = ws;
    clientWS.on('message', function incoming(message) {
      console.log('received: %s', message);
    });
  });

  //*****************************************
  //*************Drive Logic*****************
  //driveSpeed is a var that holds the cars current speed
  // 94-88 driveSpeed is dead zone where car wont move


  //Sends a speed and direction of the drive motor
  app.get('/drive/Forward/:speed', function(req, res){
    var newSpeed = req.params.speed;
    clientWS.send("Forward");
    clientWS.send(newSpeed);
    console.log(req.params);
    res.send('');
  });

  //Sends a speed and direction of the drive motor
  app.get('/drive/Reverse/:speed', function(req, res){
    var newSpeed = req.params.speed;
    clientWS.send("Reverse");
    clientWS.send(newSpeed);
    console.log(req.params);
    res.send('');
  });
