	var A;
	var X;
	var Y;



	var Coeditor = {};

	Coeditor.socket = null;

	Coeditor.connect = (function(host) {
            if ('WebSocket' in window) {
                Coeditor.socket = new WebSocket(host);
            } else if ('MozWebSocket' in window) {
                Coeditor.socket = new MozWebSocket(host);
            } else {
                Console.log('Error: WebSocket is not supported by this browser.');
                return;
            }

            Coeditor.socket.onopen = function () {
                Console.log('Info: WebSocket connection opened.');
                
                
                //open doc
                var doc = document.getElementsByName('file');
                var length = doc.length;
               
                if(length != 0){
                	for(var i = 0; i < length; ++i){
                		//alert(doc[i]);
                		doc[i].onclick = function(event) {
             
                			//alert(this.text);               			
		                	var message = {
		        			clientId: $('#userid').value,
		        			action: "open",
		        			contentType: 0,
		        			content: this.text      			
		                	};
				        	var jmessage = JSON.stringify(message);
				        	alert(jmessage);
				            Coeditor.socket.send(jmessage); 
				            
				            getUserList(this);
		                };
                	}
                };
                
                document.getElementById('coeditor').onkeydown = function(event) {
                	var message = {
	        			clientId: document.getElementById('userid').value,
	        			action: "open",
	        			contentType: 0,
	        			content: "0"      			
	                	};
		        	var jmessage = JSON.stringify(message);
		        	alert(jmessage);
		            Coeditor.socket.send(jmessage);  
                };
                
                
                
            };

            Coeditor.socket.onclose = function () {
                document.getElementById('coeditor').onkeydown = null;
                Console.log('Info: WebSocket closed.');
            };

            Coeditor.socket.onmessage = function (message) {
            	message = message.data;
            	message = JSON.parse(message);
            	Console.log(message);
            	var action = message.action;
            	if(action == "response")
            		Textarea.update(message.content);
            	else{
            		
            	}
            	
                
            };
        });

        Coeditor.initialize = function() {
            if (window.location.protocol == 'http:') {
                Coeditor.connect('ws://' + window.location.host + '/coeditor/coeditor');
            } else {
                Coeditor.connect('wss://' + window.location.host + '/coeditor/coeditor');
            }
        };
        
        

        var Textarea = {};
        Textarea.update = function(message) {
        	var changeset = message;
        	var oldText = document.getElementById('coeditor').value;
        	//alert(oldText);
        	var changeList = changeset.changeList;
        	var length = changeList.length;
        	var newText = "";
        	for(var i = 0; i < length; ++i){
        		var change = changeList[i];
        		alert("type:" + change.type);
        		//new
        		if(change.type == 0){
        			newText += change.content;
        		} else {
        			alert("content length:" + change.content.length);
        			if(change.content.length == 1){
        				newText += oldText.charAt(parseInt(change.content));
        			}
        			else{
        				var begin = parseInt(change.content[0]);
        				var end = parseInt(change.content[2]) + 1;
        				newText += oldText.substring(begin, end);
        			}
        		}
        	}
        	alert("newtext:" + newText);
        	document.getElementById('coeditor').value = newText;
        };
        
        
        
        
        
        
        
        
        
        var Console = {};
        Console.log = (function(message) {
        	
        	alert(message);
        	
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

        Coeditor.initialize();
        
        
        function getUserList(link){
    		var docName = link.text;
    		alert(docName);
    		$.getJSON('userlist.jsp?docName=' + docName, function(data) {
    			$('#userlist').empty();
    			var head = "<li class='nav-header'>Userlist</li>";
    			$('#userlist').append(head);
    			
    			for (var i = 0; i < data.length; i++) {
    				var newli = document.createElement("li");  // Create with DOM
    				newli.innerHTML= data[i].username;
    				$('#userlist').append(newli);
    			}
    		});
    	}