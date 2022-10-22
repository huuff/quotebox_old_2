#!/usr/bin/env bash

http --form http://localhost:8180/auth/realms/quotebox/protocol/openid-connect/token client_id=quotebox-client username=admin password=testuser grant_type=password | jq -r .access_token
#http --verbose --form http://localhost:8180/auth/realms/quotebox/protocol/openid-connect/token client_id=quotebox-client username=admin password=testuser grant_type=password
