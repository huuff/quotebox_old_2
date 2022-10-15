#!/usr/bin/env bash

http -v POST localhost:8080/quote text="test" Authorization:"Bearer $(./new_token.sh)"
