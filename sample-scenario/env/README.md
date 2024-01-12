# Sample Scenario - env

Creates the following elements:
* Claims DB: \<\<Mongo DB>>
* Policies DB: \<\<Mongo DB>>


Environment variables

| Property                  | Description                         | Default Value                     |
|---------------------------|-------------------------------------|-----------------------------------|
| CLAIMS_DB_HOST            | MongoDB Host                        | mongodb.mongodb.svc.cluster.local |
| CLAIMS_DB_PORT            | MongoDB Port                        | 27017                             |
| CLAIMS_DB_DATABASE        | MongoDB Database name               | claimsdb                          |
| CLAIMS_DB_AUTH_DATABASE   | MongoDB Auth Database name          | admin                             |
| CLAIMS_DB_USERNAME        | MongoDB Username                    | user                              |
| CLAIMS_DB_PASSWORD        | MongoDB Password                    | password                          |
| POLICIES_DB_HOST          | MongoDB Host                        | mongodb.mongodb.svc.cluster.local |
| POLICIES_DB_PORT          | MongoDB Port                        | 27017                             |
| POLICIES_DB_DATABASE      | MongoDB Database name               | policiesdb                        |
| POLICIES_DB_AUTH_DATABASE | MongoDB Auth Database name          | admin                             |
| POLICIES_DB_USERNAME      | MongoDB Username                    | user                              |
| POLICIES_DB_PASSWORD      | MongoDB Password                    | password                          |
