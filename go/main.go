package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"time"

	pb "mbier/tutorial/grpc"

	"github.com/bxcodec/faker/v3"
	"google.golang.org/grpc"
)

func main() {
	lis, err := net.Listen("tcp", ":8001")
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	pb.RegisterPersonServiceServer(s, &server{})
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}

type server struct {
	pb.UnimplementedPersonServiceServer
}

func (s *server) ListStream(listRequest *pb.ListRequest, listResponse pb.PersonService_ListStreamServer) error {

	t := time.Tick(time.Second * 2)

	for range t {
		randomInt, err := faker.RandomInt(1, 4)
		if err != nil {
			return err
		}

		var pp = []*pb.Person{}

		for range randomInt {
			p, err := randomPerson()
			if err != nil {
				return err
			}
			pp = append(pp, p)
		}
		response := &pb.ListResponse{Person: pp}

		fmt.Println("Send stream message")

		err = listResponse.Send(response)
		if err != nil {
			return err
		}
	}

	return nil
}

func (s server) List(context.Context, *pb.ListRequest) (*pb.ListResponse, error) {
	p, err := randomPerson()
	if err != nil {
		return nil, err
	}

	fmt.Println("Send message")

	return &pb.ListResponse{Person: []*pb.Person{p}}, nil
}

func randomPerson() (*pb.Person, error) {
	var p  = &pb.Person{}

	p.Name = faker.Name()
	p.Id = int32(faker.RandomUnixTime())
	p.Email = faker.Email()
	p.Phones = []*pb.Person_PhoneNumber{
		{
			Number: faker.Phonenumber(),
			Type:   pb.Person_WORK,
		},
	}
	return p, nil
}
