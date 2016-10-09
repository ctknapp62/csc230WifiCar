var express = require('express');
var app = express();

// var statusLED = new statusLED();
// statusLED.init();

var five = require('johnny-five');
var board = new five.Board();
var steeringServo;
var driveServo;

board.on("ready", function() {
        steeringServo = new five.Servo(9);
        steeringServo.min();
        steeringServo.max();
        steeringServo.center();

        driveServo = new five.Servo(10);
        driveServo.center();

        this.repl.inject({steeringServo: steeringServo});
});

// setup directory for static files
app.use(express.static('static'));

app.get('/', function (req, res) {
  res.send('Hello World!');
});

app.get('/steer/:direction', function (req,res) {
  var cw = (req.params.direction == 'Right' ? 1 : -1);
  steeringServo.step(20 * cw);
  console.log(req.params);
  res.send('');
});

app.get('/drive/:fr', function (req,res)  {
  var direction = req.params.fr;
  if (direction == 'Forward') {
    driveServo.step(1);
  }
  else if (direction == 'Reverse') {
    driveServo.to(87);
  }
  else if(direction == 'Stop') {
    driveServo.center(); 
  }
  console.log(req.params);
  res.send('');

});

app.listen(2300, function () {
  console.log('Example app listening on port 2300!');
});
