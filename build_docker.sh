#!/bin/bash
NAMESPACE="${1:-codebase_b1298_app}"
docker build -t "$NAMESPACE" .