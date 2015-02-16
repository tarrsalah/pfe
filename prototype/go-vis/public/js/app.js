(function(document, vis, m) {
    var app = {};

    var Node = function(data) {
	this.id = data.id;
	this.label = data.label;
    };

    var Edge = function(data) {
	this.from = data.from;
	this.to = data.to;
    };

    var Graph = function(data) {
	this.nodes = m.prop(data.nodes);
	this.edges = m.prop(data.edges);
    };

    Graph.prototype.get = function() {
	return m.request({
	    method:"GET",
	    url:"/graph",
	    type:Graph
	});
    };

    app.vm = {};
    app.vm.init = function() {
	var graph = new Graph({});
	app.vm.graph = graph.get().then(graph);
    };

    app.controller = function() {
	app.vm.init();
    };

    function draw(element, isInitialized) {
	if (!isInitialized) {
	    var data = {
		nodes: app.vm.graph().nodes(),
		edges: app.vm.graph().edges()
	    };
	     var options = {
		 width: '800px',
		 height: '400px'
	     };
	    new vis.Network(element, data, options);
	}
    }

    app.view = function(controller) {
	return m("div", {
	    id: "graph",
	    config: draw
	});
    };
    m.module(document.getElementById("app"), app);
    window.app = app;
}(document, vis, m));
