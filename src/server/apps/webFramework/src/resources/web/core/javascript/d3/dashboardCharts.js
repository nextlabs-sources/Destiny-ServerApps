/*
 * All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */

/**
 * Line charts.
 * 
 * @param chartData
 *            chart data as JSON
 * @param elementId
 *            element id where should place in page
 */
function lineWithFocusChart(elementId, chartData, height, width) {
 return nv.addGraph(function() {
  var chart = nv.models.lineWithFocusChart().width(width)
       .height(height);

  chart.xAxis.tickFormat(d3.format(',f'));
  chart.x2Axis.tickFormat(d3.format(',f'));

  chart.yAxis.tickFormat(d3.format(',.2f'));
  chart.y2Axis.tickFormat(d3.format(',.2f'));

  d3.select('#' + elementId).datum(chartData).transition().duration(500)
    .call(chart).attr('width', width).attr('height', height);

  nv.utils.windowResize(chart.update);

  return chart;
 });
}

/*
 * labelProperty and valueProperty are optional parameters. default values are label and value respectively
 */
function discreteBarChart(elementId, chartData, xAxisLabel, yAxisLabel, height, width, labelProperty, valueProperty, showXAxisLabel, clickHandler) {
 
 switch(arguments.length) {
     case 6: labelProperty = 'label';
     case 7: valueProperty = 'value';
     case 8: showXAxisLabel = false;
     case 9: 
     case 10: break;
     default: throw new Error('illegal argument count')
 }
 
    return nv.addGraph(function() {
   var chart = nv.models.discreteBarChart()
       .x(function(d) { return d[labelProperty]; })
       .y(function(d) { return d[valueProperty]; })
           //Too many bars and not enough room? Try staggering labels.
       .tooltips(true)         //show tooltips
       .showValues(false)      //instead, show the bar value right on top of each bar.
       .transitionDuration(350)
       .showXAxis(showXAxisLabel);
//       .width(width)
//       .height(height);
   
   //chart.xAxis.axisLabel(xAxisLabel);
   chart.xAxis.rotateLabels(-45);
   chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format("d"));
   chart.valueFormat(d3.format('d'));
   /*chart.tooltipContent( function(key, label, value){ 
    return label + ' : ' + value; 
   });*/

   
   chart.tooltipContent(function(key, x, y, e, graph) {
	   var func = nv.utils.defaultColor();
	   var content = '<table class="c3-tooltip"><tbody><tr class="c3-tooltip-name-"><td class="name"><span style="background-color:' + func(e.point, e.pointIndex) + '"></span>' + x+  '</td><td class="value">' + y + '</td></tr></tbody></table>';
	   return content;
 });
   
   
   d3.select('#' + elementId)
       .datum(chartData)
       .call(chart);//.attr('width', width).attr('height', height);

      chart.discretebar.dispatch.on('elementClick', function(e){
       if (clickHandler !== undefined || typeof clickHandler !== 'undefined')
       {
    	   clickHandler(e);
       }
      });
   
   nv.utils.windowResize(chart.update);
   
   return chart;
 });
}


/*
 * labelProperty and valueProperty are optional parameters. default values are label and value respectively
 */
function discreteBarChart_c3(elementId, chartData, xAxisLabel, yAxisLabel, height, width, labelProperty, valueProperty, clickHandler) {
  
  switch(arguments.length) {
      case 6: labelProperty = 'label';
      case 7: valueProperty = 'value';
      case 8: 
      case 9: break;
      default: throw new Error('illegal argument count')
  }
  
  var cats = [];
  var data = [yAxisLabel];
  for (i = 0; i< chartData.length; i++)
  {
   var object = chartData[i];
   cats.push(object[labelProperty]);
   data.push(object[valueProperty]);
  }
   
  var chart = c3.generate({
      bindto: '#' + elementId,
      size:{
       height:height,
       width:width
      },
      data: {
        columns: [
           data
        ],
     type:'bar'
      },
   axis: {
    x: {
     type:'categorized',
  categories:cats,
    tick: {
                 rotate: 75,
     culling: {
                    max: 0 // the number of tick texts will be adjusted to less than this value
                }
             },
             height: 40
    }
   },
       legend: {
         show: false
     },
      tooltip: {
         show: true
     }
  });
}

function resizeLengthyLabels(label) {
 /*if (label.lastIndexOf("/", label.length) > 0) {
  label = label.substring(label.lastIndexOf("/", label.length) + 1,
    label.length);
 }*/
 
 if (label.length > 15) {
  label = label.substring(0,7) + "..."
    + label.substring(label.length - 5, label.length);
 }
 
 return label;
}

/**
 * Bar charts.
 * 
 * @param chartData
 *            chart data as JSON
 * @param elementId
 *            element id where should place in page
 * @param showControls
 *            Allow user to switch between 'Grouped' and 'Stacked' mode
 * @param xAxisLabel
 *            x axis label
 * @param yAxisLabel
 *            y axis label
 */
function multiBarChart(elementId, chartData, showControls, xAxisLabel,
  yAxisLabel, height, width, labelProperty, valueProperty) {
 
 /*
  * labelProperty and valueProperty are optional parameters. default values are label and value respectively
  */
 
 switch(arguments.length) {
     case 7: labelProperty = 'label';
     case 8: valueProperty = 'value';
     case 9: 
     case 10: break;
     default: throw new Error('illegal argument count')
    }
 
 return nv.addGraph(function() {
  var chart = nv.models.multiBarChart()
     .x(function(d) { return resizeLengthyLabels(d[labelProperty]); })
     .y(function(d) { return d[valueProperty]; })
          .transitionDuration(500)
    .reduceXTicks(true) // If 'false', every single x-axis tick
    // label will be rendered.
    .rotateLabels(0) // Angle to rotate x-axis labels.
    .showControls(showControls) // Allow user to switch between
    // 'Grouped' and 'Stacked' mode.
    .groupSpacing(0.2) // Distance between each group of bars.
    .width(width)
    .height(height);

  chart.xAxis.axisLabel(xAxisLabel).tickFormat(d3.format(',f'));
  chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format(',.1f'));

  d3.select('#' + elementId).datum(chartData).call(chart).attr('width', width).attr('height', height);

  nv.utils.windowResize(chart.update);

  return chart;
 });
}

/**
 * Pie charts.
 * 
 * @param chartData
 *            chart data as JSON
 * @param elementId
 *            element id where should place in page
 * 
 */
function pieChart(elementId, chartData, height, width, labelThreshold, showLegend, labelProperty, valueProperty, clickHandler) {
 
 /*
  * labelProperty and valueProperty are optional parameters. default values are label and value respectively
  */
 
 switch(arguments.length) {
  case 4: labelThreshold = 0.2;
  case 5: showLegend = true;
     case 6: labelProperty = 'label';
     case 7: valueProperty = 'value';
     case 8: 
     case 9: break;
     default: throw new Error('illegal argument count')
    }
 
 return nv.addGraph(function() {
  var chart = nv.models.pieChart().x(function(d) {
   return d[labelProperty];
  }).y(function(d) {
   return d[valueProperty];
  }).showLabels(true).showLegend(showLegend).labelThreshold(labelThreshold)
  .width(width)
  .height(height);
  
  d3.select('#' + elementId).datum(chartData).transition().duration(1500)
    .call(chart).attr('width', width).attr('height', height);

  nv.utils.windowResize(chart.update);

        chart.pie.dispatch.on('elementClick', function(e){
         if (clickHandler !== undefined || typeof clickHandler !== 'undefined')
      {
          clickHandler(e);
      }
        });
  
  return chart;
 });
}


function pieChart_c3(elementId, chartData, height, width, labelThreshold, showLegend, labelProperty, valueProperty, clickHandler) {
 
 /*
  * labelProperty and valueProperty are optional parameters. default values are label and value respectively
  */
 
 switch(arguments.length) {
  case 4: labelThreshold = 0.2;
  case 5: showLegend = true;
     case 6: labelProperty = 'label';
     case 7: valueProperty = 'value';
     case 8: 
     case 9: break;
     default: throw new Error('illegal argument count')
    }
 
 var cols = [];

 for (i = 0; i< chartData.length; i++)
 {
  var object = chartData[i];
  var x = [];
  x.push(object[labelProperty]);
  x.push(object[valueProperty]);
  cols.push(x);
 }
 
 
 var chart = c3.generate({
  data: {
         // iris data from R
         columns: cols,
         type : 'pie',
     },     
     bindto: '#' + elementId,
     /*size:{
      height:height,
      width:width
     },*/
  legend: {
         show: false
     },
  tooltip: {
        format: {
            value: function (value, ratio, id) {
             return value;
            }
//            value: d3.format(',') // apply this format to both y and y2
        }
    }
 }); 
 
 return chart;
}


/**
 * line charts.
 * 
 * @param chartData
 *            chart data as JSON
 * @param elementId
 *            element id where should place in page
 * @param xAxisLabel
 *            x axis label
 * @param yAxisLabel
 *            y axis label
 * 
 */
function lineChart(elementId, chartData, height, width, xAxisLabel, yAxisLabel) {
 return nv.addGraph(function() {
  var chart = nv.models.lineChart().useInteractiveGuideline(true)
  .width(width)
  .height(height);

  chart.xAxis.axisLabel(xAxisLabel).tickFormat(d3.format(',r'));
  chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format('.02f'));

  d3.select('#' + elementId).datum(chartData).transition().duration(500)
    .call(chart).attr('width', width).attr('height', height);

  nv.utils.windowResize(chart.update);

  return chart;
 });
}

/**
 * Horizontal Bar Chart.
 * 
 * @param elementId
 *            element id where should place in page
 * @param chartData
 *            chart data as JSON
 * @param showControls
 *            Allow user to switch between 'Grouped' and 'Stacked' mode
 * @param xAxisLabel
 *            x axis label
 * @param yAxisLabel
 *            y axis label
 */

function horizontalBarChart(elementId, chartData, showControls, xAxisLabel, yAxisLabel, height, width, labelProperty, valueProperty, showXAxisLabel, clickHandler) {
 
 switch(arguments.length) {
     case 7: labelProperty = 'label';
     case 8: valueProperty = 'value';
     case 9: showXAxisLabel = true;
     case 10:
     case 11: break;
     default: throw new Error('illegal argument count')
 }
 
  return nv.addGraph(function() {
      var chart = nv.models.multiBarHorizontalChart()
          .x(function(d) { return d[labelProperty]; })
          .y(function(d) { return d[valueProperty]; })
          .showValues(true)           //Show bar value next to each bar.
          .tooltips(true)             //Show tooltips on hover.
          .transitionDuration(500)
          .showControls(showControls)
          .showLegend(false)
          .showXAxis(showXAxisLabel);
//          .width(width)
//          .height(height);
      
      //chart.xAxis.axisLabel(xAxisLabel);
   
      chart.yAxis.tickFormat(d3.format('d'));
      chart.valueFormat(d3.format('d'));
      chart.tooltipContent(function(key, x, y, e, graph) {
   	   var func = nv.utils.defaultColor();
   	   
   	   var color = chartData[0].color || func(e.point, e.pointIndex);
   	   
   	   var content = '<table class="c3-tooltip"><tbody><tr class="c3-tooltip-name-"><td class="name"><span style="background-color:' + color + '"></span>' + x+  '</td><td class="value">' + y + '</td></tr></tbody></table>';
   	   return content;
    });
      
      // Gonna "wrap" this d3 formatter
      var formatter = d3.format('d');
      
      chart.valueFormat(formatter);

      d3.select('#' + elementId)
          .datum(chartData)
          .call(chart);//.attr('width', width).attr('height', height);

         chart.multibar.dispatch.on('elementClick', function(e){
          if (clickHandler !== undefined || typeof clickHandler !== 'undefined')
          {
           clickHandler(e);
          }
         });
          
      nv.utils.windowResize(chart.update);
      return chart;
  });
}


function horizontalBarChart_c3(elementId, chartData, showControls, xAxisLabel, yAxisLabel, height, width, labelProperty, valueProperty, clickHandler) {
 
 switch(arguments.length) {
     case 7: labelProperty = 'label';
     case 8: valueProperty = 'value';
     case 9: 
     case 10: break;
     default: throw new Error('illegal argument count')
 }
 
  var x = [xAxisLabel];
  var data = [yAxisLabel];
  for (i = 0; i< chartData.length; i++)
  {
   var object = chartData[i];
   //x.push(object[labelProperty]);
   x.push(i + "");
   data.push(object[valueProperty]);
  }
   
  var chart = c3.generate({
      bindto: '#' + elementId,
      size:{
       height:height,
       width:width
      },
      data: {
       x:xAxisLabel,
        columns: [
           x, data
        ],
     type:'bar'
      },
   axis: {
   rotated:true,
    x: {
     type:'categorized',
    tick: {
            //type: 'categorized',
                culling: {
                    max: 0 // the number of tick texts will be adjusted to less than this value
                }
        }
    },
    y: {
            tick: {
                culling: {
                    max: 0 // the number of tick texts will be adjusted to less than this value
                }
                // for normal axis, default on
                // for categorized axis, default off
            }
        }
   },
       legend: {
         show: false
     },
      tooltip: {
         show: true
     }
  });
}
