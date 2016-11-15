//require pkg express and johnny five in node_moduels
var express = require('express');

//**********************************
//****** Requires Arduino **********
var jfive = require('johnny-five');

//instantiate a new instance of express
var app = express();

//**********************************
//****** Requires Arduino **********
//instantiate a new instance of johnny-five
var board = new jfive.Board();
var turnServo;
var driveServo;

//**********************************
//****** Requires Arduino **********
board.on("ready", function() {
    //Creating the Steering Servo
    turnServo = new jfive.Servo(9);
    turnServo.min();
    turnServo.max();
    turnServo.center();

    //Creating the drive motor (is actually a servo)
    driveServo = new jfive.Servo({
        pin: 10,
        startAt: 94

        //Possibly implement continuous instead of step?
        // , type: "continuous"
    });

    //Center both drive and steering servos
    driveServo.to(90);
    turnServo.to(90);


    //Establish repl commands
    this.repl.inject({
        turnServo: turnServo
    });
    this.repl.inject({
        driveServo: driveServo
    });

});

//Set up which port express will be listening on and printing a console log
app.listen(2300, function() {
    console.log('Port 2300 in use. Used for CSC 230 RC Car');
});

//**********************************
//****Requires static Directory*****
//Link express to directory containing our html files
app.use(express.static('static'));

//handeling URL backslash
app.get('/', function(req, res) {
    res.send('Welcome to CSC 230 RC Car!');
});


//***********************************
//********Turning Logic**************
//maxTurn is used to keep steering position less then max turn radius
var maxTurn = 0;

//turnRadius is used keep a var that holds the current turn position in degrees
var turnRadius = 90;

//Setting up the Steering HTTP Req from HTML
app.get('/steer/:dir', function(req, res) {
    var steeringDriver = req.params.dir;
    //Checking direction right and if we are able to still move the servo
    if (steeringDriver == 'Right' && maxTurn <= 13) {
        turnRadius = turnRadius + 3.07;
        turnServo.to(turnRadius);
        maxTurn = maxTurn + 1;
    }

    //Checking direction left and if we are able to still move the servo
    else if (steeringDriver == 'Left' && maxTurn >= -13) {
        turnRadius = turnRadius - 3.07;
        turnServo.to(turnRadius);
        maxTurn = maxTurn - 1;
    }

    //Assume if recived a req and is not left or right it is center command
    else {
        turnRadius = 90;
        turnServo.to(turnRadius);
        maxTurn = 0;
    }

    //Lines for debugging in repl
    console.log(req.params);
    console.log("Turn Radius " + turnRadius);
    console.log("MaxTurn " + maxTurn);
    res.send('');
});


//*****************************************
//*************Drive Logic*****************
//driveSpeed is a var that holds the cars current speed
// 94-88 driveSpeed is dead zone where car wont move
var driveSpeed = 94;

//Setting upt the Drive HTTP Req from HTML
app.get('/drive/:dir', function(req, res) {
    //Setting the direction of drive from http req
    var driveDirection = req.params.dir;
    if (driveDirection == 'Forward') {
        driveSpeed = driveSpeed + 1;
        //Check to see if driveSpeed is in dead zone while trying to go forward
        if (driveSpeed == 95 || driveSpeed == 91) {
            driveSpeed = 102;
        }
        driveServo.to(driveSpeed);
    }
    //Check to see if driveSpeed is in dead zone while trying to go reverse
    else if (driveDirection == 'Reverse') {
        driveSpeed = driveSpeed - 1;
        if (driveSpeed == 93 || driveSpeed == 98) {
            driveSpeed = 87;
        }
        driveServo.to(driveSpeed);

    }
    //Assume if not forward or reverse recived a 'Stop' command
    else {
        driveServo.to(94);
        driveSpeed = 94;

    }
    //Lines for debugging in repl
    console.log(req.params);
    console.log(driveSpeed);
    res.send('');
});


//***************************************************
//*************Calibration Logic*********************
app.get('/cal/:dir', function(req, res) {
    var driveDirection = req.params.dir;
    if (driveDirection == 'Max') {
        turnServo.to(180);
    } else if (driveDirection == 'Min') {
        turnServo.to(0);

    } else {
        turnServo.to(90);

    }
    console.log(req.params);
    console.log(driveSpeed);
    res.send('');
});
