JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) *.java

TARGETS = rdt_sim

default: .java.class
	@echo 'java Main $$1 $$2 $$3 $$4 $$5 $$6' > rdt_sim
	chmod +x rdt_sim

clean:
	$(RM) *.class
	$(RM) sim/*.class
	$(RM) rdt_sim

run:
	./${TARGETS} 1000 0.1 20 0 0 0

run-reordering:
	./${TARGETS} 1000 0.1 100 0.02 0 0

run-packet-loss:
	./${TARGETS} 1000 0.1 100 0 0.02 0

run-packet-corruption:
	./${TARGETS} 1000 0.1 100 0 0 0.02

run-combined:
	./${TARGETS} 1000 0.1 100 0.02 0.02 0.02