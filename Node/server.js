var express = require('express');
var app = express();

// var statusLED = new statusLED();
// statusLED.init();

var five = require('johnny-five');
var board = new five.Board();
var servo;

board.on("ready", function() {
        servo = new five.Servo(9);
        servo.min();
        servo.max();
        servo.center();

        this.repl.inject({servo: servo});
});

// setup directory for static files
app.use(express.static('static'));

app.get('/', function (req, res) {
  res.send('Hello World!');
});

app.get('/steer/:direction', function (req,res) {
  var cw = (req.params.direction == 'Right' ? 1 : -1);
  servo.step(20 * cw);
  console.log(req.params);
  res.send('');
});

app.listen(2300, function () {
  console.log('Example app listening on port 2300!');
});
