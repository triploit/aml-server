all:
	@rm -rf od
	@mkdir od

	@javac -cp "lib/AML.jar:lib/gson-2.6.2.jar" src/io/github/triploit/amlserver/*.java src/io/github/triploit/amlserver/settings/*.java -d od
	@jar cvfm od/aml_server.jar src/META-INF/MANIFEST.MF -C od .

	@rm -rf tmp
	@mkdir tmp

	@cp -r od/io tmp/
	@cp -r src/META-INF tmp/

	@cd tmp; jar xvf ../lib/AML.jar
	@cd tmp; jar xvf ../lib/gson-2.6.2.jar

	@jar cvfm aml_server.jar src/META-INF/MANIFEST.MF -C tmp .
