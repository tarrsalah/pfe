all: latex

latex:
	cd ./tex; make

go-vis:
	cd ./prototype/go-vis; make
