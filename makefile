SRC_DIR=./proto

build-pb-go:
	go get google.golang.org/protobuf/cmd/protoc-gen-go
	protoc -I=${SRC_DIR} --go_out=. --plugin=${GOPATH}/bin/protoc-gen-go ${SRC_DIR}/person.proto 
	protoc -I=${SRC_DIR} --go_out=. --plugin=${GOPATH}/bin/protoc-gen-go ${SRC_DIR}/person_service.proto 

build-grpc-go:
	go get google.golang.org/grpc/cmd/protoc-gen-go-grpc
	protoc -I=${SRC_DIR} --go-grpc_out=. --plugin=${GOPATH}/bin/protoc-gen-go-grpc ${SRC_DIR}/person_service.proto

build-pb-python:
	protoc -I=${SRC_DIR} --python_out=./python ${SRC_DIR}/person.proto

build-grpc-python:
	pip3 install grpcio-tools
	python3 -m grpc_tools.protoc -I=${SRC_DIR} --python_out=./python --grpc_python_out=./python ${SRC_DIR}/person_service.proto

build-pb-java:
	protoc -I=${SRC_DIR} --java_out=./java ${SRC_DIR}/person.proto



