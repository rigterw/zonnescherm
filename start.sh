#!/bin/bash
git commit -am "bullshit"
git pull origin main
pip install --no-cache-dir -r requirements.txt
python rain_detect.py