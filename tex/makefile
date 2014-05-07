all: clean compile pdf

compile:	
	latexmk main.tex
	makeglossaries main
	latexmk -bibtex -pdflatex=pdflatex -view=pdf main.tex	

clean:
	latexmk -C
	
pdf: 
	evince main.pdf