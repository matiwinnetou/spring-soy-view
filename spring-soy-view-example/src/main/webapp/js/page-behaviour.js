var APP = {}

APP.words = ['hi!']

APP.pushNewWord = function() {
	var newWord = $('#newWordTextField').val();
	APP.words.push(newWord);
	$('#clientWords').html(soy.example.clientWords({ words: APP.words }));
}

$(document).ready(function() {
	$('#getServerTimeLink').click(function() {
		$.ajax({
		  url: "/spring-soy-view-example/app/server-time",
		  context: document.body,
		  success: function(data) {
		    $('#serverTime').html(data);
		  }
		});
	});
	
	$('#submitButton').click(APP.pushNewWord);
	
	$('#wordsForm').submit(function() {
		APP.pushNewWord();
		return false;
	});
});