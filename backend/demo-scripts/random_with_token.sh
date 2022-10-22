#!/usr/bin/env bash

http -v GET localhost:8080/quote/random Authorization:"Bearer $(./new_token.sh)"
