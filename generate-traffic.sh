#!/bin/bash

while true; do
  curl http://localhost:8081/api/v1/external
  sleep 5
done