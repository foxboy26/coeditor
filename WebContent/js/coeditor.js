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
                var doc = $("li[name = file]");
                var length = doc.length;
               
                if(length != 0){
                	for(var i = 0; i < length; ++i){
                		doc[i].onclick = function(event) { 
		                	var message = {
			        			clientId: $('#userid').val(),
			        			action: "open",
			        			contentType: 0,
			        			content: this.text      			
		                	};
				        	var jmessage = JSON.stringify(message);
				            Coeditor.socket.send(jmessage); 				            				          				            
				            getUserList(this);
		                };
                	}
                };
                
                $('#coeditor').keypress(function(event) {
                	
                	var message = computeChangeSet(event);
		            //Coeditor.socket.send(message);  
                });
                
                
                
            };

            Coeditor.socket.onclose = function () {
                document.getElementById('coeditor').onkeydown = null;
                Console.log('Info: WebSocket closed.');
            };

            Coeditor.socket.onmessage = function (message) {
            	Console.log(message.data);

            	message = message.data;
            	message = JSON.parse(message);
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
        	var changeset = JSON.parse(message);
        	var oldText = $('#coeditor').value;
        	//alert(oldText);
        	var changeList = changeset.changeList;
        	var length = changeList.length;
        	var newText = "";
        	
        	if(changeset.oldLength == 0){
        		A = changeset;
        	}
        	
        	
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
        	oldLength = newtext.length();
        	$('#coeditor')[0].value = newText;
        };
        
        var Console = {};
        Console.log = (function(message) {
            	
            var console = $('#console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.innerHTML = message;
            console.append(p);
            while (console.children().length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        });

        Coeditor.initialize();
        
        
        function getUserList(link){
    		var docName = link.text;
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
        
        function computeChangeSet(event){
        	//TODO: keycode
        	var keycode = (event.keyCode ? event.keyCode : event.which);
        	var value = String.fromCharCode(keycode);
        	var oldLength = $('#coeditor').val().length;
        	alert("oldL:" + oldLength);
        	var cursorPosition = $('#coeditor').getSelectionStart();	
        		
        		//$('#coeditor').getSelectionStart();
        	alert("cursorPosition:" + cursorPosition);
        	var changeList = new Array();
        	if(cursorPosition == 0){
        		var change = {
            			"type" : 0,
                    	"length" : 1,
                    	"content": value
            	};
        		changeList[0] = change; 
        		change = getLastChange(oldLength, cursorPosition, false);
        		if(change != null)
        			changeList[1] = change;
        	} else if(cursorPosition == 1) {       		
        		var change = {
            			"type" : 1,
                    	"length" : 1,
                    	"content": 0
            	};
        		changeList[0] = change;
        		change = {
            			"type" : 0,
                    	"length" : 1,
                    	"content": value
            	};
        		changeList[1] = change;
        		change = getLastChange(oldLength, cursorPosition, false);
        		if(change != null)
        			changeList[2] = change;
        	} else{
        		var change = {
        				"type" : 1,
                    	"length" : cursorPosition,
                    	"content": "0-" + (cursorPosition - 1)
            	};
        		changeList[0] = change;
        		change = {
            			"type" : 0,
                    	"length" : 1,
                    	"content": value
            	};
        		changeList[1] = change;
        		change = getLastChange(oldLength, cursorPosition, false);
        		if(change != null)
        			changeList[2] = change;
        	}
        	
        	var changeset = {
        			"oldLength" : oldLength,
        			"newLength": oldLength + 1,
        			"changeList": changeList
        	};
        	
        	
        	var message = {
        			"clientid": $('#userid').value,
        			"action" : "newChange",
        			"changeList": JSON.stringify(changeset)
        	};
        	Y = changeList;
        	var jmessage = JSON.stringify(message);
        	alert("new message:" + jmessage);
        	return jmessage;
        }
        
        
        function getLastChange(oldLength, curPos, isDelete){
        	var change = null;
        	if(!isDelete){
        		if(curPos < oldLength - 1){
        			change = {
                			"type" : 1,
                        	"length" : oldLength - curPos,
                        	"content": curPos + "-" + (oldLength - 1)
                	};
        		} else if(curPos == oldLength - 1){
        			change = {
                			"type" : 1,
                        	"length" : 1,
                        	"content": oldLength - 1
                	};
        		}       		
        	}
        	return change;
        }
        
        
        
        $.fn.getSelectionStart = function(){
        	if(this.lengh == 0) return -1;
        	input = this[0];

        	var pos = input.value.length;

        	if (input.createTextRange) {
        		var r = document.selection.createRange().duplicate();
        		r.moveEnd('character', input.value.length);
        	if (r.text == '') 
        		pos = input.value.length;
        		pos = input.value.lastIndexOf(r.text);
        	} else if (typeof(input.selectionStart)!="undefined"){
        		pos = input.selectionStart;

        		return pos;
        	}
        };
