package main

import (
	"encoding/json"
	"net/http"
)

type Node struct {
	Id    int64  `json:"id"`
	Label string `json:"label"`
}

type Edge struct {
	From  int64  `json:"from"`
	To    int64  `json:"to"`
	Label string `json:"label"`
}

type Graph struct {
	Nodes []Node `json:"nodes"`
	Edges []Edge `json:"edges"`
}

func main() {
	graph := Graph{
		Nodes: []Node{
			Node{1, "one"},
			Node{2, "two"},
			Node{3, "three"},
			Node{4, "four"},
			Node{5, "five"},
		},
		Edges: []Edge{
			Edge{1, 2, "three"},
			Edge{1, 4, "five"},
			Edge{1, 5, "six"},
			Edge{2, 4, "six"},
			Edge{3, 5, "eight"},
			Edge{2, 5, "saven"},
		},
	}

	fs := http.FileServer(http.Dir("./public/"))
	http.Handle("/public/", http.StripPrefix("/public/", fs))
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/" {
			http.Error(w, "Not Found", http.StatusNotFound)
			return
		}
		http.ServeFile(w, r, "./public/index.html")
	})

	http.HandleFunc("/graph", func(w http.ResponseWriter, r *http.Request) {
		graph_json, err := json.MarshalIndent(&graph, "", " ")
		if err != nil {
			http.Error(w, "Can't marshal the graph", http.StatusInternalServerError)
		}
		w.Write(graph_json)
	})

	http.ListenAndServe(":3000", nil)
}
