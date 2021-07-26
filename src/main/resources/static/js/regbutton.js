function exec() {


var leaveHomeTime = document.querySelector('#spanleaveHomeTime').textContent;

leaveHomeTime = leaveHomeTime.slice( 0, -3 ) ;
var timeControl = document.querySelector('input[type="time"]');
timeControl.value = leaveHomeTime;
};