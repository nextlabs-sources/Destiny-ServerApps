/**
 *  NextLabs Charts. 
 *  
 *  @author Amila Silva
 * 
 */

/**
 * Line charts.
 * 
 * @param chartData
 *            chart data as JSON
 * @param elementId
 *            element id where should place in page
 */
function lineWithFocusChart(elementId, chartData) {
	return nv.addGraph(function() {
		var chart = nv.models.lineWithFocusChart();

		chart.xAxis.tickFormat(d3.format(',f'));
		chart.x2Axis.tickFormat(d3.format(',f'));

		chart.yAxis.tickFormat(d3.format(',.2f'));
		chart.y2Axis.tickFormat(d3.format(',.2f'));

		d3.select('#' + elementId).datum(chartData).transition().duration(500)
				.call(chart);

		nv.utils.windowResize(chart.update);

		return chart;
	});
}

/**
 * Discrete Bar Chart.
 * 
 * @param chartData
 *            chart data as JSON
 * @param elementId
 *            element id where should place in page
 * @param xAxisLabel
 *            X axis label
 * @param yAxisLabel
 *            Y axis label
 * @param height
 *            height of the chart
 */
function discreteBarChart(elementId, chartData, xAxisLabel, yAxisLabel, height,
		callback) {
	return nv
			.addGraph(function() {
				var chart = nv.models.discreteBarChart().x(function(d) {
					return getChartDisplayText(d.label);
				}).y(function(d) {
					return d.value;
				})
				// .staggerLabels(true)
				.tooltips(true) // Don't show tooltips
				.transitionDuration(350).height(height);

				if (chartData.length > 0 && chartData[0].values
						&& chartData[0].values.length <= 50) {
					chart.showValues(true);
				} else {
					chart.showValues(false);
				}

				// chart.xAxis.axisLabel(xAxisLabel);
				chart.xAxis.rotateLabels(90);
				chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format("d"));

				chart.valueFormat(d3.format('d'));

				chart.tooltipContent(function(key, label, value) {
					return label + ':' + value;
				});

				chart.discretebar.dispatch.on('elementClick', function(e) {
					if (callback !== undefined
							|| typeof callback !== 'undefined') {
						callback(e);
					}
				});

				chart.tooltipContent(function(key, x, y, e, graph) {
							var func = nv.utils.defaultColor();
							var content = '<table class="c3-tooltip"><tbody><tr class="c3-tooltip-name-"><td class="name"><span style="background-color:'
									+ func(e.point, e.pointIndex)
									+ '"></span>'
									+ x
									+ '</td><td class="value">'
									+ y
									+ '</td></tr></tbody></table>';
							return content;
						});

				d3.select('#' + elementId).datum(chartData).call(chart);

				nv.utils.windowResize(chart.update);
				// var xTicks = d3.select('#' + elementId).select('.nv-x.nv-axis > g').selectAll('g');
				// xTicks.selectAll('text').style("text-anchor", "end").attr(
				// 		'transform', function(d, i, j) {
				// 			return 'translate (-10, 25) rotate(-90 0,0)'
				// 		});

				var xTicks = d3.select('#' + elementId).select('.nv-x.nv-axis > g').selectAll('g');
				xTicks.selectAll('text').attr("style","font: normal 14px 'Helvetica'; font-family: 'sans-serif';");
				var xRect = d3.select('#' + elementId).select('.nv-x.nv-axis');
				xRect.attr('transform', function() {return 'translate (7,585)'});

				return chart;
			});
}

function resizeLengthyLabels(label) {
	if (label && label.lastIndexOf("/", label.length) > 0) {
		label = label.substring(label.lastIndexOf("/", label.length) + 1,
				label.length);
	}
	return label;
}

/**
 * Multi Bar chart.
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
 * @param height
 *            height
 */
function multiBarChart(elementId, chartData, showControls, xAxisLabel,
		yAxisLabel, height) {
	return nv
			.addGraph(function() {
				var chart = nv.models.multiBarChart().x(function(d) {
					return getChartDisplayText(d.label);
				}).y(function(d) {
					return d.value;
				}).transitionDuration(500).reduceXTicks(true) // If 'false',
																// every single
																// x-axis tick
				// label will be rendered.
				.rotateLabels(-90) // Angle to rotate x-axis labels.
				.showControls(showControls) // Allow user to switch between
				// 'Grouped' and 'Stacked' mode.
				.groupSpacing(0.2) // Distance between each group of bars.
				.height(height);
				;

				// chart.xAxis.axisLabel(xAxisLabel);
				// chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format("d"));
				// chart.valueFormat(d3.format('d'));

				chart.tooltipContent(function(key, x, y, e, graph) {
							var keyStr = (key || key != '-') ? key : "";
							var content = '<table class="c3-tooltip"><tbody><tr class="c3-tooltip-name-"><td>'
									+ keyStr
									+ '</td><td class="name">'
									+ x
									+ '</td><td class="value">'
									+ y
									+ '</td></tr></tbody></table>';
							return content;
						});

				d3.select('#' + elementId).datum(chartData).call(chart);

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
function pieChart(elementId, chartData, showLegend, callback) {
	var cols = [];

	for (i = 0; i < chartData.length; i++) {
		var object = chartData[i];
		var x = [];
		x.push(object.label);
		x.push(object.value);
		cols.push(x);
	}

	var chart = c3.generate({
		data : {
			columns : cols,
			type : 'pie',
		},
		bindto : '#' + elementId,
		legend : {
			show : showLegend,
			position : 'bottom'
		},
		tooltip : {
			format : {
				value : function(value, ratio, id) {
					return value;
				}
			}
		},
		pie : {
			onclick : function(d, i) {
				if (callback !== undefined || typeof callback !== 'undefined') {
					var dummyEventObj = {
						"point" : {
							"label" : d.id
						}
					};
					callback(dummyEventObj);
				}
			},
		}
	});
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
function lineChart(elementId, chartData, xAxisLabel, yAxisLabel) {
	return nv.addGraph(function() {
		var chart = nv.models.lineChart().useInteractiveGuideline(true);

		chart.xAxis.axisLabel(xAxisLabel).tickFormat(d3.format(',r'));
		chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format('.02f'));

		d3.select('#' + elementId).datum(chartData).transition().duration(500)
				.call(chart);

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
function horizontalBarChart(elementId, chartData, marginLeft, height,
		showControls, tooltip, xAxisLabel, yAxisLabel, showXAxisLabel, callback) {
	
	 switch(arguments.length) {
	     case 9: showXAxisLabel = true;
	     case 10:
	     case 11: break;
	     default: throw new Error('illegal argument count')
    }
	
	
	return nv
			.addGraph(function() {
				var chart = nv.models.multiBarHorizontalChart().x(function(d) {
					return getChartDisplayText(d.label);
				}).y(function(d) {
					return d.value;
				}).margin({
					top : 30,
					right : 20,
					bottom : 50,
					left : marginLeft
				}).showValues(true) // Show bar value next to each bar.
				.tooltips(tooltip) // Show tooltips on hover.
				.transitionDuration(500)
				.showControls(showControls)
				.height(height)
				.showLegend(false)
				.showXAxis(showXAxisLabel);

				//chart.xAxis.axisLabel(xAxisLabel);
				chart.yAxis.axisLabel(yAxisLabel).tickFormat(d3.format("d"));
				chart.valueFormat(d3.format('d'));
				chart.multibar.dispatch.on('elementClick', function(e) {
					if (callback !== undefined
							|| typeof callback !== 'undefined') {
						callback(e);
					}
				});

				chart
						.tooltipContent(function(key, x, y, e, graph) {
							var func = nv.utils.defaultColor();

							var color = chartData[0].color
									|| func(e.point, e.pointIndex);

							var content = '<table class="c3-tooltip"><tbody><tr class="c3-tooltip-name-"><td class="name"><span style="background-color:'
									+ color
									+ '"></span>'
									+ x
									+ '</td><td class="value">'
									+ y
									+ '</td></tr></tbody></table>';
							return content;
						});

				d3.select('#' + elementId).datum(chartData).call(chart);

				nv.utils.windowResize(chart.update);

				return chart;
			});
}
