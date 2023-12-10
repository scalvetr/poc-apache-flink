package repository

import (
	m "datagen/model"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"math/rand"
)

type PoliciesRepo struct {
	DbConfig MongoDbConfig
}

func BuildPoliciesRepo(dbConfig MongoDbConfig) PoliciesRepo {
	return PoliciesRepo{
		DbConfig: dbConfig,
	}
}

func (r PoliciesRepo) GetPolicy(customerId string) *m.PolicyStruct {
	ctx, _, collection := r.DbConfig.Init()

	resultBson, err := collection.Find(ctx, bson.M{"customer_id": customerId})
	if err != nil {
		panic(err)
	}
	if resultBson == nil {
		return nil
	}
	var result []m.PolicyStruct
	resultBson.All(ctx, result)

	if result != nil && len(result) > 0 {
		return &result[rand.Intn(len(result)-1)]
	}
	// no policies for this customer
	return nil
}

func (r PoliciesRepo) StorePolicy(policyStruct m.PolicyStruct) primitive.ObjectID {
	ctx, _, collection := r.DbConfig.Init()

	if policyStruct.ID.IsZero() {
		res, err := collection.InsertOne(ctx, policyStruct)
		if err != nil {
			panic(err)
		}
		return res.InsertedID.(primitive.ObjectID)
	} else {
		res, err := collection.UpdateByID(ctx, policyStruct.ID, policyStruct)
		if err != nil {
			panic(err)
		}
		return res.UpsertedID.(primitive.ObjectID)

	}

}

func (r PoliciesRepo) Close() error {
	return r.Close()
}
