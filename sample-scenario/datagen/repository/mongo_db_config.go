package repository

import (
	"context"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"time"
)

type MongoDbConfig struct {
	DbUri          string
	DbUser         string
	DbPassword     string
	DbName         string
	CollectionName string
}

func (r MongoDbConfig) Init() (context.Context, mongo.Database, mongo.Collection) {
	client, err := mongo.NewClient(options.Client().SetAuth(options.Credential{
		AuthSource: r.DbName,
		Username:   r.DbUser,
		Password:   r.DbPassword,
	}).ApplyURI(r.DbUri))

	if err != nil {
		panic(err)
	}
	ctx, _ := context.WithTimeout(context.Background(), 10*time.Second)
	err = client.Connect(ctx)
	if err != nil {
		panic(err)
	}
	db := client.Database(r.DbName)

	collectionList, err := db.ListCollections(ctx, bson.M{"name": r.CollectionName})
	if err != nil {
		panic(err)
	}
	if !collectionList.Next(ctx) {
		err = db.CreateCollection(ctx, r.CollectionName)
		if err != nil {
			panic(err)
		}
	}
	collection := db.Collection(r.CollectionName)

	return ctx, *db, *collection
}

func (r MongoDbConfig) Close() error {
	//err := r.client.Disconnect(context.TODO())
	//if err == nil {
	//	fmt.Println("Connection to MongoDB closed.")
	//}
	//return err
	return nil
}
