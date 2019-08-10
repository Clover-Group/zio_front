# Top level makefile for the CloverGroup frontend

cov:
	@sbt clean coverage test 
	@sbt coverageReport

clean:
	@find . -name "target" | xargs rm -rf {} \;

allclean: clean
	@rm -rf .bloop .idea .metals project/.bloop

