package main

import (
	"net/http"
)

func main() {
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
		w.Write([]byte("hello graph"))
	})

	http.ListenAndServe(":3000", nil)
}
