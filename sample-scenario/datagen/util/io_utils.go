package util

import (
	"encoding/json"
	"io/ioutil"
	"log"
	"os"
)

func GetEnv(key, fallback string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}
	return fallback
}
func ReadFile(schemaName string) string {
	avroSchemaBytes, err := ioutil.ReadFile(schemaName)
	if err != nil {
		log.Fatal(err)
	}
	// Convert []byte to string and print to screen
	avroSchema := string(avroSchemaBytes)
	//fmt.Println(avroSchema)
	return avroSchema
}

func ToMap(dto interface{}) map[string]interface{} {
	log.Printf("readData: ioutil.ReadFile\n")
	jsonStr, err := json.Marshal(dto)
	if err != nil {
		panic(err)
	}
	var data map[string]interface{}

	log.Printf("readData: json.Unmarshal\n")
	err = json.Unmarshal(jsonStr, &data)
	if err != nil {
		log.Fatal(err)
	}
	return data
}
