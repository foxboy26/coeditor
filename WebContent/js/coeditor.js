	var A;
	var X;
	var Y = null;


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
                
                $('#coeditor').keypress(function(event) {
                	
                	var changeSet = computeChangeSet(event);
                	
                	/*var changeset = {
                			"oldLength" : oldLength,
                			"newLength": oldLength + 1,
                			"changeList": Y
                	};
                	
                	
                	var message = {
                			"clientid": $('#userid').val(),
                			"action" : "newChange",
                			"content": JSON.stringify(changeset)
                	};*/
                               
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
            	var clientId = message.clientId;
            	if(action == "response" && clientId == "server")
            		Textarea.update(message.content);
            	else if (action == "activeUsers" && clientId == "server"){
            		var activeUsers = JSON.parse(message.content);
            		for (var i = 0; i < activeUsers.length; i++) {
            			updateUserStatus(activeUsers[i], 'online');
            		}
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
        	oldLength = newText.length;
        	$('#coeditor')[0].value = newText;
        };
        
        var Console = {};
        Console.log = (function(message) {
            	
            var console = $('#console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.innerHTML = message;
            console.append(p);
        });

        Coeditor.initialize();
        
        

        function openDocument(docId) {
          
          getUserList(docId);

          //$('#' + docId).addClass('active');
          
          $('#title').text(docId);

          /* send open request */
          var message = {
            clientId: $('#userid').val(),
            action: "open",
            content: docId      			
          };

          var jmessage = JSON.stringify(message);

          Console.log('Info: [open request]' + jmessage);

          Coeditor.socket.send(jmessage); 				            				          				            

          /*setInterval(
            function() {
              getActiveUsers(docId);
            },
            2000
          );
          
          setInterval(
            function() { 
              saveDocument(docId);
            }, 
            60000
          );*/
        }

        function getActiveUsers(docName) {
        	var message = {
                    clientId: $('#userid').val(),
                    action: "getActiveUsers",
                    content: docName      			
                  };

                  var jmessage = JSON.stringify(message);

                  Console.log('Info: [getActiveUsers request]' + jmessage);

                  Coeditor.socket.send(jmessage); 				 
        }
        
        
        function createDocument() {
          var docName = $('input[name=title]').val()
          var userId = $('input[name=userid]').val()

          alert(docName + userId);
          $.getJSON(
            'createDocument.jsp?docName=' + docName + '&userId=' + userId,
            function(data) {
              if (data.trim() == "success") {

                var message = {
                  clientId: $('#userid').val(),
                  action: "create",
                  content: docName      			
                };

                var jmessage = JSON.stringify(message);

                Console.log('Info: [create request]' + jmessage);

                Coeditor.socket.send(jmessage); 				            				          				            

                getUserList(docName);

                updateFileList(docName);
              }
            }
          );
        }

        function saveDocument(docId) {
          var message = {
            clientId: $('#userid').val(),
            action: "save",
            content: docId      			
          };

          var jmessage = JSON.stringify(message);

          Console.log('Info: [save request]' + jmessage);

          //Coeditor.socket.send(jmessage); 				            				          				            
          
          $('#info').text('Last time saved at ' + new Date().toTimeString());
        }

        function deleteDocument(docName) {
        	alert(docName);
        	$.getJSON('deleteDocument.jsp?docName=' + docName, function(data) {
        		if (data == 'success') {
            	var message = {
        				clientId : $('#userid').val(),
        				action : "delete",
        				content : docName
        			};
        		
        			var jmessage = JSON.stringify(message);
        		
        			Console.log('Info: [delete request]' + jmessage);
        			
        			Coeditor.socket.send(jmessage);
        			
        			$('#' + docName).remove();
        		}
            });
		}
        
        function shareDocument() {
        	
        	var userName = $('input[name=shareUserName]').val();
        	var docName = $('#title').text();
        	$.getJSON('shareDocument.jsp?docName=' + docName + '&userName=' + userName, function(data) {
        		if (data == 'success') {
        			alert('Successfully added ' + userName + ' to coeditor!');
                	
                	getUserList(docName);
        		}
            });
        }        
        function updateFileList(docId) {
          var newli = document.createElement("li");  // Create with DOM
          newli.innerHTML= '<a href="#" id="' + docId + '" onclick="openDocument("' + docId + '">' + docId  +'</a>'
          $('#filelist').append(newli);
          $('#' + docId).addClass('active');
        }
        
        function getUserList(docName){
          $.getJSON('userlist.jsp?docName=' + docName, function(data) {
            $('#userlist').empty();
            var head = "<li class='nav-header'>Userlist</li>";
            $('#userlist').append(head);
            
            for (var i = 0; i < data.length; i++) {
              var newli = document.createElement("li");  // Create with DOM
              newli.setAttribute("id",data[i].userid);
              newli.innerHTML= "<span class=\"label\">" + data[i].username + "</span>";
              $('#userlist').append(newli);
            }
            
          });
        }

        function updateUserStatus(clientId, state) {
          if (state == 'online') {
        	  $('#' + clientId + ' span').removeClass().addClass('label label-success');
          } else {
        	  $('#' + clientId + ' span').removeClass('label-success');
          }
        }
        
        function computeChangeSet(event){
        	//TODO: keycode
        	var keycode = (event.keyCode ? event.keyCode : event.which);
        	var value = String.fromCharCode(keycode);
        	var oldLength = $('#coeditor').val().length;
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
        	
        	if(Y == null)
        		Y = changeset;
        	else
        		Y = combine(Y, changeset);
        	
        	var jset = JSON.stringify(Y);
        	Console.log(jset);
        }
        
        
        function combine(fset, sset){
        	var lastIndex = 0;
        	var fnew = fset.newLength;
        	var sold = sset.oldLength;
        	var flist = fset.changeList;
    		var slist = sset.changeList;
    		var newlist = new Array();
        	var newset = {};
        	var count = 0;
        	if(fnew != sold){
        		alert("cannot combine the two sets!");       		
        	} else {
        		var i = 0;
        		var j = 0;
        		var slength = slist.length;
        		var str = "";
        		while(j < slength){       			
					var scontent = slist[j].content;
        			var fstart, sstart, fend, send;
        			
        			if(slist[j].type == 1){              				
    					if(slist[j].length == 1){
    						sstart = send = parseInt(scontent);        						 
    					} else {
    						sstart = parseInt(scontent.charAt(0));
    						send = parseInt(scontent.charAt(2));
    					}
        				while(lastIndex < sstart){
        					var fcontent = flist[i].content;
        					if(flist[i].type == 1){
        						if(str != ""){
                					var newchange = {
        									"type" : 0,
        									"length" : str.length,
        									"content" : str
        								};
        								
                					newlist[count++] = newchange;
        								str = "";	
                				}
        						if(flist[i].length <= sstart - lastIndex){
									++i;
									lastIndext += flist[i].length;								
								} else if(flist[i].length == sstart - lastIndex + 1) {
									flist[i].content  = fcontent.charAt[2];
									flist[i].length = 1;
									lastIndex += (sstart - lastIndex);
								} else {
									flist[i].content  = (parseInt(fcontent.charAt[0]) + sstart - lastIndex + 1) + "-" + fcontent.charAt[2];
									flist[i].length = flist[i].length - (sstart - lastIndex);
									lastIndex += (sstart - lastIndex);
								}       		
        					} else {
        						if(flist[i].length <= sstart - lastIndex){
									++i;
									lastIndext += flist[i].length;								
								} else {
									flist[i].content  = fcontent.substring(sstart - lastIndex);
									flist[i].length = flist[i].length - (sstart - lastIndex);
									lastIndex += (sstart - lastIndex);
								}
        					}
        				}
        				
        					while(lastIndex <= send){
        						fcontent = flist[i].content;       						
        					
        						if(flist[i].type == 0){
    								if(flist[i].length <= send - lastIndex + 1){
    									str += fcontent;
    									lastIndex += fcontent.length;
    									++i;    																
    								} else {
    									 
    									str += fcontent.substring(0, send - lastIndex + 1);
    									flist[i].content = fcontent.substring(send - lastIndex + 1);
    									lastIndex += (send - lastIndex + 1);
    								}        								        						
        						} else {
/*        							if(flist[i].length == 1){
                						fstart = fend = parseInt(fcontent);        						 
                					} else {
                						fstart = parseInt(fcontent.charAt(0));
                						fend = parseInt(fcontent.charAt(2));
                					}*/
        							if((lastIndex + flist[i].length) < send){
        								newlist[count++] = flist[i++];       														
        							} else {
        								lastIndex = send + 1;
        								var newchange = {
        										"type" : 1,
        										"length" : send - lastIndex + 1,
        										"content" : lastIndex + "-" + send
        								};
        								newlist[count++] = flist[i++];        									
        							}
        						}
        						
        					}
        				} else {
        					str += slist[j].content;
        				}
        			++j;
        		}
        		if(str != ""){
					var newchange = {
							"type" : 0,
							"length" : str.length,
							"content" : str
						};
						
					newlist[count++] = newchange;
				}
        	}
        	newset = {
        		"oldLength" : fset.oldLength,
        		"newLength" : sset.newLength,
        		"changeList"  : newlist
        	};
        	return newset;
        }
           
        
        /*function mergeSet(fset, sset){
        	var lastIndex = 0;
        	var fold = fset.oldLength;
        	var sold = sset.oldLength;
        	var newset = {};
        	 
        	if(fold != sold){
        		alert("cannot merge the two sets!");       		
        	} else {
        		newset.oldLength = fold;
        		var newLength = oldLength;
        		var i = 0, j = 0;
        		var flist = fset.changeList;
        		var slist = sset.changeList;
        		var flength = flist.length;
        		var slength = slist.length;
        		var list = new Array();
        		var index = 0;
        		while(i < flength && j < slength){
        			if(flist[i].type == 0){
        				list[index++] = flist[i++];    				
        			} else {
        				if(slist[j].type == 0){
        					list[index++] = slist[j++]; 
        				} else {
        					var fcontent = flist[i].content;
        					var scontent = slist[j].content;
        					var fstart, sstart, fend, send;
        					if(fcontent.length == 1){
        						fstart = fend = parseInt(fcontent);        						 
        					} else {
        						fstart = parseInt(fcontent.charAt(0));
        						fend = parseInt(fcontent.charAt(2));
        					}
        					if(scontent.length == 1){
        						sstart = send = parseInt(scontent);        						 
        					} else {
        						sstart = parseInt(scontent.charAt(0));
        						send = parseInt(scontent.charAt(2));
        					}
        					
        					if(fend < sstart){
        						if(fstart )
        						list[index++] = flist[i++]; 
        					} else if(send < fstart){
        						list[index++] = flist[j++]; 
        					} else if(fend > sstart){
        						var newlist = {
        							"type" : 1,
        							"length" : (sstart - fend + 1),
        							"content" : sstart + "-" + fend
        						};
        						list[index++] = newlist;
        					}
        				}
        			}
        		}
        	}
        }*/
        
        
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
        }
