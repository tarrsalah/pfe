all: clean compile pdf	

compile:
	latexmk main.tex
	makeglossaries main
	latexmk -bibtex -pdf main.tex	

clean:
	latexmk -c 
	
pdf: 
	evince main.pdf