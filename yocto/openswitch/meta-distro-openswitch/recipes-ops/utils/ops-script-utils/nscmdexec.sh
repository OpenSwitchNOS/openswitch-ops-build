#!/bin/bash

ns=$1
buffer=$2

sudo ip netns exec $ns $buffer
