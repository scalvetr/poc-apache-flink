package main

import (
	m "datagen/model"
	r "datagen/repository"
	"github.com/google/uuid"
	"github.com/jaswdr/faker"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"log"
	"math/rand"
	"strings"
	"time"
)

var length = 0

const capacity = 1000

var customers = new([capacity]m.CustomerStruct)

type DataGenerator struct {
	claimsRepo    r.ClaimsRepo
	policiesRepo  r.PoliciesRepo
	kafkaProducer r.KafkaProducer
	random        *rand.Rand
	faker         faker.Faker
}

func BuildDataGenerator(claimsMongoConfig r.MongoDbConfig, policiesMongoConfig r.MongoDbConfig) DataGenerator {
	return DataGenerator{
		claimsRepo:   r.BuildClaimsRepo(claimsMongoConfig),
		policiesRepo: r.BuildPoliciesRepo(policiesMongoConfig),
		random:       rand.New(rand.NewSource(22)),
		faker:        faker.New(),
	}
}

func (g DataGenerator) generateCustomer() m.CustomerStruct {
	faker := faker.New()
	firstName := faker.Person().FirstName()
	lastName := faker.Person().LastName()
	address := faker.Address()

	data := m.CustomerStruct{
		CustomerId: strings.ToLower(firstName + "." + lastName),
		Email:      strings.ToLower(firstName + "." + lastName + "@gmail.com"),
		Name:       firstName,
		Surname:    lastName,
		Telephones: []m.TelephoneStruct{
			{Number: faker.Phone().Number(), Primary: true},
			{Number: faker.Phone().Number(), Primary: false},
		},
		Addresses: []m.AddressStruct{
			{Number: address.BuildingNumber(),
				City:    address.City(),
				Country: address.Country(),
				Street:  address.StreetName(),
				ZipCode: address.PostCode(),
				Default: true,
			},
		},
	}
	return data
}

func (g DataGenerator) NextCustomer() (m.CustomerStruct, bool) {
	// until the array reaches its capacity
	if length < capacity {
		data := g.generateCustomer()
		customers[length] = data
		length++
		return data, true
	} else {
		createNew := rand.Int()%2 == 0
		index := rand.Intn(length - 1)
		if createNew {
			customers[index] = g.generateCustomer()
		}
		return customers[index], true
	}
}

func (g DataGenerator) NextPolicy(customer m.CustomerStruct) m.PolicyStruct {
	createNew := rand.Int()%2 == 0
	if !createNew {
		c := g.policiesRepo.GetPolicy(customer.CustomerId)
		if c != nil {
			log.Printf("return an existing policy with id (%v)\n", customer.CustomerId)
			return *c
		}
	}
	c := m.PolicyStruct{
		PolicyId:          uuid.New().String(),
		CustomerId:        customer.CustomerId,
		Title:             g.faker.Lorem().Sentence(g.random.Intn(15)),
		CreationTimestamp: primitive.NewDateTimeFromTime(time.Now()),
	}
	return c
}

func (g DataGenerator) NextClaim(customer m.CustomerStruct, policy m.PolicyStruct) m.ClaimStruct {
	createNew := rand.Int()%2 == 0
	if !createNew {
		c := g.claimsRepo.GetClaim(customer.CustomerId)
		if c != nil {
			log.Printf("return an existing claim with id (%v)\n", customer.CustomerId)
			return *c
		}
	}
	c := m.ClaimStruct{
		ClaimId:           uuid.New().String(),
		CustomerId:        customer.CustomerId,
		PolicyId:          policy.PolicyId,
		Title:             g.faker.Lorem().Sentence(g.random.Intn(15)),
		CreationTimestamp: primitive.NewDateTimeFromTime(time.Now()),
	}
	return c
}

func (g DataGenerator) Close() {
	err := g.claimsRepo.Close()
	if err != nil {
		log.Fatal(err)
	}
	err = g.policiesRepo.Close()
	if err != nil {
		log.Fatal(err)
	}
}
