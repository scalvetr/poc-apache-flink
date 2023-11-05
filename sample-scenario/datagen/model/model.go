package model

import (
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type CustomerStruct struct {
	CustomerId string            `json:"customer_id,omitempty"`
	Name       string            `json:"name,omitempty"`
	Surname    string            `json:"surname,omitempty"`
	Email      string            `json:"email,omitempty"`
	Telephones []TelephoneStruct `json:"telephones,omitempty"`
	Addresses  []AddressStruct   `json:"addresses,omitempty"`
}

type AddressStruct struct {
	Street  string `json:"street,omitempty"`
	Number  string `json:"number,omitempty"`
	City    string `json:"city,omitempty"`
	Country string `json:"country,omitempty"`
	ZipCode string `json:"zip_code,omitempty"`
	Default bool   `json:"default"`
}
type TelephoneStruct struct {
	Number  string `json:"number,omitempty"`
	Primary bool   `json:"primary"`
}

type ClaimStruct struct {
	ID                primitive.ObjectID `bson:"_id,omitempty"`
	ClaimId           string             `bson:"claim_id,omitempty"`
	CustomerId        string             `bson:"customer_id,omitempty"`
	PolicyId          string             `bson:"policy_id,omitempty"`
	Title             string             `bson:"title,omitempty"`
	CreationTimestamp primitive.DateTime `bson:"creation_timestamp,omitempty"`
}

type PolicyStruct struct {
	ID                primitive.ObjectID `bson:"_id,omitempty"`
	PolicyId          string             `bson:"policy_id,omitempty"`
	CustomerId        string             `bson:"customer_id,omitempty"`
	Title             string             `bson:"title,omitempty"`
	CreationTimestamp primitive.DateTime `bson:"creation_timestamp,omitempty"`
}
