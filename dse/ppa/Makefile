DIR_INC = ./include
DIR_SRC = ./src
DIR_OBJ = ./obj
DIR_BIN = ./bin

SRC = $(wildcard ${DIR_SRC}/*.cpp)  
OBJ = $(patsubst %.cpp,${DIR_OBJ}/%.o,$(notdir ${SRC})) 

TARGET = genPPA

BIN_TARGET = ${DIR_BIN}/${TARGET}

CC = c++

CFLAGS = -O3 -std=c++1y -Wall -m64 -march=native -I${DIR_INC}

MY_LIBS   = -ldl -rdynamic -lreadline -ltermcap -lpthread

LDFLAGS   = -lm

all:${BIN_TARGET}

${BIN_TARGET}:${OBJ}
	$(CC) $(CFLAGS) $(LDFLAGS) $(OBJ) $(MY_LIBS) -o $@
    
${DIR_OBJ}/%.o:${DIR_SRC}/%.cpp
	$(CC) $(CFLAGS) -c  $^ -o $@
.PHONY:clean
clean:
	find ${DIR_OBJ} -name "*.o" -exec rm -rf {} \;
