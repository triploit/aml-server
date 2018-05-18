user triploit
name aml-server
ver 0.0.0.1
dep triploit aml 0.1.4.0

func install {
	sudo make all
	sudo chmod +x aml_server

	sudo cp aml_server.jar /usr/bin/AML_server.jar
	sudo cp aml_server /usr/bin/aml_server

	sudo rm -rf od
	sudo rm -rf tmp
	sudo rm aml_server.jar
}

func remove {
	sudo rm /usr/bin/AML_server.jar
	sudo rm /usr/bin/aml_server
}

func update {
	&install
}
