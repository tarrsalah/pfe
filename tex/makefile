all: clean compile pdf

compile:	
	latexmk main.tex
	makeglossaries main
	latexmk -bibtex -view=pdf  -pdf -pdflatex="pdflatex --shell-escape %O %S" main.tex	

clean:
	latexmk -C
	
pdf: 
	evince main.pdf