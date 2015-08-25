# Rules for writing a makefile that cross-compile fine
# - Do not assume the file system path, use a prefix variable
# - Do not assume the name of the compiler, use CC variable (same for CXX, LD, AR)
# - Do not assume CFLAGS, let them get from the environment
# - When installing add the DESTDIR ahead of the prefix

prefix?=/usr/local

.PHONY: all install clean

all: hello

hello: hello.o
	${CC} ${CFLAGS} -o $@ $^

install: hello
	install -d $(DESTDIR)${prefix}/bin
	install -d $(DESTDIR)${prefix}/include
	install -m 0755 hello $(DESTDIR)${prefix}/bin
	install -m 0644 hello.h $(DESTDIR)${prefix}/include

clean:
	rm -f hello *.o
