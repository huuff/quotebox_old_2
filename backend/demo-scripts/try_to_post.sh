#!/usr/bin/env bash

#http -v POST localhost:8080/quote text="test" Authorization:"Bearer $(./new_token.sh)"
http -v POST localhost:8080/quote Content-Type:"application/json" author="test" Authorization:"Bearer $(./new_token.sh)"
