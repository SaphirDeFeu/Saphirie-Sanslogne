CC = g++
CCFLAGS = -Wall -Werror
CLIARGS = -t 18000

main: src/*
	$(CC) $(CCFLAGS) -o mctc src/main.cpp

run:
	make && mctc.exe $(CLIARGS)