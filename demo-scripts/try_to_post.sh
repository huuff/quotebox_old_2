#!/usr/bin/env bash

http -v POST localhost:8080/quote text="test" tags:='[ "tag" ]' Authorization:"Bearer $(./new_token.sh)"
