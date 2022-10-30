	var monthIndexMap = {
		"JAN" : "01",
		"FEB" : "02",
		"MAR" : "03",
		"APR" : "04",
		"MAY" : "05",
		"JUN" : "06",
		"JUL" : "07",
		"AUG" : "08",
		"SEP" : "09",
		"OCT" : "10",
		"NOV" : "11",
		"DEC" : "12"
	};

	var iChars = "^&*{}";
	
	function checkForiChars(value)
	{
		for (var i = 0; i < value.length; i++) {
			if (iChars.indexOf(value.charAt(i)) != -1) {
				return true;
			}
		}
		return false;
	}
	
	function getDateFromStr(dateStr) {
		var a=dateStr.split(" ");
		var d=a[0].split("-");
		var t=a[1].split(":");
		var date = new Date(d[0],(d[1]-1),d[2],t[0],t[1],t[2]);
		
		return date;
	}

	function getDatesInBetweenWithData(fromDate, toDate, chartData, sortOrder,
		groupingMode, maxRows, isDashboard) {
		
		var sDate = getDateFromStr(fromDate);
		var eDate = getDateFromStr(toDate);

		var datesArr = [];
		if (groupingMode == "GROUP_BY_DAY") {
			datesArr = getDatesInBetween(sDate, eDate, sortOrder);
		} else if (groupingMode == "GROUP_BY_MONTH") {
			datesArr = getMonthsInBetween(sDate, eDate, sortOrder);
		}
		var dataSet = chartDataForFullDateRange(chartData, datesArr, isDashboard);
	
		if (maxRows == "ALL" || dataSet.length == 0) {
			return dataSet;
		} else {
			var limitedDataArr = [];
			if (dataSet.length < maxRows) {
				return dataSet;
			} else {
				for (i = 0; i < maxRows; i++) {
					limitedDataArr.push(dataSet[i]);
				}
	
				return limitedDataArr;
			}
		}
	}

	function getMonthsInBetween(fromDate, toDate, sortOrder) {
	
		var months = [ 'JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG',
				'SEP', 'OCT', 'NOV', 'DEC' ];
		var noOfMonths = monthDiff(fromDate, toDate);
	
		var datesArr = [];
	
		if (sortOrder == "asc") {
			for (i = 0; i <= noOfMonths; i++) {
				datesArr.push(months[fromDate.getMonth()] + "-" + fromDate.getFullYear());
				fromDate.setMonth(fromDate.getMonth() + 1);
			}
			
		} else {
			for (i = 0; i <= noOfMonths; i++) {
				datesArr.push(months[toDate.getMonth()] + "-" + toDate.getFullYear());
				toDate.setMonth(toDate.getMonth() - 1);
			}
		}
		return datesArr;
   }
	
	function monthDiff(fromDate, toDate) {
	    var months = 0;
	    months = (toDate.getFullYear() - fromDate.getFullYear()) * 12;
	    months -= fromDate.getMonth() + 1;
	    months += toDate.getMonth() +1 ;
	    return months <= 0 ? 0 : months;
	}
	
	function getDatesInBetween(fromDate, toDate, sortOrder) {
		
		var DAY = 1000 * 60 * 60 * 24;
		var months = [ 'JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG',
				'SEP', 'OCT', 'NOV', 'DEC' ];
		var days = Math.floor((toDate.getTime() - fromDate.getTime()) / DAY)
	
		var datesArr = [];
	
		if (sortOrder == "asc") {
	
			for (i = 0; i <= days; i++) {
				var dayOfMonth = getTwoDigitDate(fromDate.getDate());			
				datesArr.push(dayOfMonth + "-" + months[fromDate.getMonth()]
						+ "-" + fromDate.getFullYear());
				fromDate.setDate(fromDate.getDate() + 1);
			}
	
		} else {
	
			for (i = 0; i <= days; i++) {
				var dayOfMonth = getTwoDigitDate(toDate.getDate());
				datesArr.push(dayOfMonth + "-" + months[toDate.getMonth()]
						+ "-" + toDate.getFullYear());
				toDate.setDate(toDate.getDate() - 1);
			}
	
		}
		return datesArr;
   }
	

	function getTwoDigitDate(dateVal) {
		if (dateVal < 10) {
			return "0" + dateVal;
		} else {
			return dateVal;
		}
	}
	

	function chartDataForFullDateRange(chartData, fullDatesArr, isDashboard) {
		var values = [];
	
		if(isDashboard) {
			for ( var x in fullDatesArr) {
				var row = {};
				row.dimension = fullDatesArr[x];
				row.resultcount = 0;
				
				for ( var i in chartData) {
					var dateLabel = chartData[i].dimension;
					var count = chartData[i].resultcount;
					
					if (dateLabel.toUpperCase() == row.dimension.toUpperCase()) {
						row.resultcount = count;
						break;
					}
				}
		
				values.push(row);
			};
			
			
		} else {
			for ( var x in fullDatesArr) {
				var row = {};
				row.label = fullDatesArr[x];
				row.value = 0;
				
				for ( var i in chartData) {
					var count = chartData[i].value;
					var dateLabel = chartData[i].label;
					
					if (dateLabel.toUpperCase() == row.label.toUpperCase()) {
						row.value = count;
						break;
					}
				}
		
				values.push(row);
			};
		}
		return values;
     }
	
	
	/*
	 * date could be a date string or a date object
	 */
	function getYYYYMMDD(input) {
		var yyyy = input.getFullYear();
		var mm = "" + (input.getMonth() + 1);
		var dd = "" + input.getDate();
		return yyyy + "-" + ((mm.length == 2) ? mm : "0" + mm[0]) + "-"
				+ ((dd.length == 2) ? dd : "0" + dd[0]);
	}

	/*
	datestr is of form 01-AUG-2014
	 */
	function formDate(datestr) {
		datestr = datestr.toUpperCase();
		var parts = datestr.split("-");
		return parts[2] + "-" + monthIndexMap[parts[1]] + "-"
				+ ((parts[0].length == 2) ? parts[0] : "0" + parts[0]);
	}

	function daysInMonth(year, month) {
		var m = [31,28,31,30,31,30,31,31,30,31,30,31];
		if (month != 2) return m[month - 1];
		if (year%4 != 0) return m[1];
		if (year%100 == 0 && year%400 != 0) return m[1];
		return m[1] + 1;
	}

	
	
	function formatDate(d){
	   return d.getFullYear() + "-" +  addZero(d.getMonth()+1) + "-" +  addZero(d.getDate()) + " 00:00:00";
	}
	
    function addZero(n){
       return n < 10 ? '0' + n : '' + n;
    }
	
	var entityMap = {
		      "&": "&amp;",
		      "<": "&lt;",
		      ">": "&gt;",
		      "\"": '&quot;',
		      "'": '&#39;',
		      "/": '&#x2F;'
		    };

	function escapeHTML(string) {
	   return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	}

	var escapeElement = document.createElement('textarea');
	
	function unescapeHTML(html) {
		escapeElement.innerHTML = html;
	    return escapeElement.value;
	}
	
	function escapeChar(text) {
		return text.replace(/\\/g, "\\\\");
	} 
	
	function unescapeChar(text) {
		var text = text.replace(/\\\\/g, "\\");
		//console.log("unesacaped char :" + text)
		return text;
	} 

	function shortenString(string) {
		var text = string.length > 30? string.substr(0,30) + '...': string;
		return text;
	}

	function escapeClass(string) {
	   return string.replace(/\W/g, '_');
	}
	
	function isValidDate(formdate) {
        var valid = true;
        var date = formdate.split('-');
        var year  = parseInt(date[0],10);
        var month = parseInt(date[1],10);
        var day   = parseInt(date[2],10);
        
        if(isNaN(year) || isNaN(month) ||  isNaN(day)) valid = false;
        else if(date[0].length != 4) valid = false;
		else if(date[1].length != 2) valid = false;
		else if(date[2].length != 2) valid = false;
        else if((month < 1) || (month > 12)) valid = false;
        else if(((month == 4) || (month == 6) || (month == 9) || (month == 11)) && (day > 30)) valid = false;
        else if((month == 2) && (((year % 400) == 0) || ((year % 4) == 0)) && ((year % 100) != 0) && (day > 29)) valid = false;
        else if((month == 2) && ((year % 4) != 0) && (day > 28)) valid = false;
		else if((day < 1) || (day > 31)) valid = false;

     return valid;
   }
	
	function loadActions(actionsProviderURL, targetSelectElementId) {
		var post_data = {};
		var action = "LOAD_ACTIONS_DATA";

		$.ajax({
			type : 'POST',
			url : actionsProviderURL,
			data : {
				"data" : JSON.stringify(post_data),
				"action" : action
			},
			dataType : 'json',
			success : function(response) {
				populateActions(targetSelectElementId, response.actions);
			},
			error : function(xhr, status) {
				handleError(xhr, 'Error encoutered in loading actions ');  
			},
			async : false
		});
	}
	
	function populateActions(selectElementId, data) {
		for ( var i in data) {
			var name = data[i].name;
			var label = data[i].label;
			var option = "<option value='" + name + "' >" + label  + "</option>";
			$("#"+selectElementId).append(option);
		}
	}
	
	function validateEmail(email) { 
	    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return !re.test(email);
	} 

	function getMapKey(map, value) {
		for ( var key in map) {
			if (map[key] == value) {
				return key;
			}
		}
		return null;
	}	
	
	
	function getChartHeight(dataSetSize) {
		var height = 300;
	
		if (dataSetSize >= 50) {
			height = 1200;
		} else if (dataSetSize >= 20) {
			height = 600;
		}
		return height;
	}
	
	function getPieChartHeight(dataSetSize) {
		var height = 450;
	
		if (dataSetSize >= 50) {
			height = 850;
		} else if (dataSetSize >= 20) {
			height = 600;
		}
		return height;
	}
	
	function getChartWidth(dataSetSize) {
		var width = 500;
		
		if (dataSetSize >= 50) {
			width = 1200;
		} else if (dataSetSize >= 20) {
			width = 850;
		}
		return width;
	}
	
	function getBarChartHeighWithLabel(maxLabelLength) {
		//console.log( " BarChartHeigh :" + (650 + maxLabelLength));

		return 650 + (maxLabelLength * 1.3);
	}
	
	function getMaxLabelLenght(dataSet) {
		var max_label_length = 0;
		jQuery.each(dataSet, function (i,val) {
				max_label_length = Math.max(getCharacterLength(val.label), max_label_length);
		});
		
		return max_label_length;
	}

	function getHorizontalChartWidth(maxLabelLength) {
		//console.log( " HorizontalChartWidth :" + (850 + (maxLabelLength * 1.5)));
		
		return 650 + (maxLabelLength * 1.5);
	}
	
	function getCharacterLength(value) {
		   var charLength = $('<span id="characterWidthSpan"></span>').css({display:'none',whiteSpace:'nowrap'}).appendTo($('body')).text(value).width();
		   $("#characterWidthSpan").remove();
		   
		   //console.log( " CharacterLength :" + charLength);
		   return charLength;
	}

	function getLeftMarginForChart(maxLabelLenght) {
		//console.log(": MAX LABLE : " +  maxLabelLenght + ":: " + (maxLabelLenght * 1.5));
		return (maxLabelLenght * 1.5);
	}
	
	function getPolicyDisplayText(s) {
		if(typeof s === 'string') {
			// Policy full name pattern
			if(s.startsWith("/")) {
				var beginIndex = s.lastIndexOf("/");
				var endIndex = s.length;
				
				return s.substring(beginIndex + 1, endIndex);
			}  		
		}
		
		return s;
	}
	
	function getChartDisplayText(s) {
		if(typeof s === 'string') {
			// Policy full name pattern
			if(s.startsWith("/")) {
				var beginIndex = s.lastIndexOf("/");
				var endIndex = s.length;
				
				return s.substring(beginIndex + 1, endIndex);
			}  		
		}
		
		return s;
	}
	
	/*
	 * needs jquery
	 */
	$.extend({
	    confirm: function(message, title, okAction) {
	    	$("body").append("<div id='dummy' style='display:none'/>");
	        $("div#dummy").dialog({
	            open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); }, 
	            buttons: {
	                "Ok": function() {
	                    $(this).dialog("close");
	                    okAction();
	                },
	                "Cancel": function() {
	                    $(this).dialog("close");
	                }
	            },
	            close: function(event, ui) { $("div#dummy").remove(); $(this).remove(); },
	            resizable: false,
	            title: title,
	            modal: true
	        }).text(message);
	    }
	});
	
	
	$.extend({
	    alert: function(message, title, okAction) {
	    	$("body").append("<div id='dummy' style='display:none'/>");
	        $("div#dummy").dialog({
	            open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); }, 
	            buttons: {
	                "Ok": function() {
	                    $(this).dialog("close");
	                    okAction();
	                }
	            },
	            close: function(event, ui) { $("div#dummy").remove(); $(this).remove(); },
	            resizable: false,
	            title: title,
	            modal: true
	        }).text(message);
	    }
	});

    var CSRF_PROTECTED_HTTP_METHODS = ['DELETE', 'POST', 'PUT', 'GET'];
    $(document).ready(function () {
        var csrfToken = $('meta[name="csrfToken"]').attr('content');
        if (csrfToken) {
            $.ajaxSetup({
                beforeSend: function (xhr, settings) {
                    if (CSRF_PROTECTED_HTTP_METHODS.indexOf(settings.type.toUpperCase()) !== -1) {
                        xhr.setRequestHeader("X-CSRF-TOKEN", csrfToken);
                    }
                }
            });
        }
    });
