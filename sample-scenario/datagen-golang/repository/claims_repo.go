package repository

import (
	m "datagen/model"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"math/rand"
)

type ClaimsRepo struct {
	DbConfig MongoDbConfig
}

func BuildClaimsRepo(dbConfig MongoDbConfig) ClaimsRepo {
	return ClaimsRepo{
		DbConfig: dbConfig,
	}
}

func (r ClaimsRepo) GetClaim(customerId string) *m.ClaimStruct {
	ctx, _, collection := r.DbConfig.Init()

	resultBson, err := collection.Find(ctx, bson.M{"customer_id": customerId})
	if err != nil {
		panic(err)
	}
	if resultBson == nil {
		return nil
	}
	var result []m.ClaimStruct
	resultBson.All(ctx, result)

	if result != nil && len(result) > 0 {
		return &result[rand.Intn(len(result)-1)]
	}
	// no cases for this customer
	return nil
}

func (r ClaimsRepo) StoreClaim(claimStruct m.ClaimStruct) primitive.ObjectID {
	ctx, _, collection := r.DbConfig.Init()

	if claimStruct.ID.IsZero() {
		res, err := collection.InsertOne(ctx, claimStruct)
		if err != nil {
			panic(err)
		}
		return res.InsertedID.(primitive.ObjectID)
	} else {
		res, err := collection.UpdateByID(ctx, claimStruct.ID, claimStruct)
		if err != nil {
			panic(err)
		}
		return res.UpsertedID.(primitive.ObjectID)

	}

}

func (r ClaimsRepo) Close() error {
	return r.Close()
}
