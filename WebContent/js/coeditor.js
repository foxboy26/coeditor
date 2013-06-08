	var coeditor = {};

	coeditor.socket = null;

	coeditor.connect = (function(host) {
            if ('WebSocket' in window) {
                coeditor.socket = new WebSocket(host);
            } else if ('MozWebSocket' in window) {
                coeditor.socket = new MozWebSocket(host);
            } else {
                Console.log('Error: WebSocket is not supported by this browser.');
                return;
            }

            coeditor.socket.onopen = function () {
                Console.log('Info: WebSocket connection opened.');
                document.getElementById('coeditor').onkeydown = function(event) {
                 //   if (event.keyCode == 13) {
                        coeditor.sendMessage();
                 //   }
                };
            };

            coeditor.socket.onclose = function () {
                document.getElementById('coeditor').onkeydown = null;
                Console.log('Info: WebSocket closed.');
            };

            coeditor.socket.onmessage = function (message) {
                Console.log(message.data);
                
            };
        });

        coeditor.initialize = function() {
            if (window.location.protocol == 'http:') {
                coeditor.connect('ws://' + window.location.host + '/coeditor/coeditor');
            } else {
                coeditor.connect('wss://' + window.location.host + '/coeditor/coeditor');
            }
        };

        coeditor.sendMessage = (function() {
        	var message = {
        			clientId: "1",
        			action: "open",
        			contentType: 0,
        			content: "1"      			
        	};
        	
        	if (message != '') {
                coeditor.socket.send(message);
            }
        	
        	
           /* var message = document.getElementById('coeditor').value;
            if (message != '') {
                coeditor.socket.send(message);
                document.getElementById('coeditor').value = '';
            }*/
        });

        var Console = {};

        Console.log = (function(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.innerHTML = message;
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        });

        coeditor.initialize();