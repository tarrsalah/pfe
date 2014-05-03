all:
	latexmk main.tex
	makeglossaries main
	latexmk -bibtex -pdf main.tex	
clean:
	latexmk -c 
	# rm main.alg main.gls main.glo main.glsdefs main.log main.acn main.acr main.fls main.blg main.run.xml main.fdb_latexmk
view:
	evince main.pdf
