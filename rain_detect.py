import asyncio
import configparser
import requests
import urllib.parse

config = configparser.ConfigParser()
time_dry = 99
async def rain_check():
    while True:
        check_rain()
        await asyncio.sleep(600)

def check_rain():

    screen_open =  is_screen_open()
    if(not screen_open):
        return
    
    if(will_rain()):
        close_screen()

#Function that checks if the sunscreen is opened by making an call to the api
def is_screen_open():
    encoded_device_url = urllib.parse.quote_plus(config["TaHoma"]["device_url"])
    url = f'https://gateway-{config["TaHoma"]["hub_pin"]}.local:{config["TaHoma"]["hub_port"]}/enduser-mobile-web/1/enduserAPI/setup/devices/{encoded_device_url}/states'
    headers = {"Authorization": f'Bearer {config["TaHoma"]["api_key"]}'}
    response = requests.get(url, headers=headers, verify=False)

    json_response = response.json()
    return json_response[5]["value"] == "open"

def close_screen():
    url = f'https://gateway-{config["TaHoma"]["hub_pin"]}.local:{config["TaHoma"]["hub_port"]}/enduser-mobile-web/1/enduserAPI/exec/apply'
    headers = {"Authorization": f'Bearer {config["TaHoma"]["api_key"]}'}
    body = {
        "label": "string",
        "actions": [
            {
            "commands": [
                {
                "name": "open",
                "parameters": [
                    0
                ]
                }
            ],
            "deviceURL": config["TaHoma"]["device_url"]
            }
        ]
        }
    requests.post(url, json=body, headers=headers, verify=False)

#function that checks if it will rain within 5 minutes
def will_rain():
    global time_dry
    time_dry -= 1
    url= f'https://gadgets.buienradar.nl/data/raintext/?lat={config["Weather"]["lat"]}&lon={config["Weather"]["lon"]}'
    response = requests.get(url)
    if(response.status_code == 200):
        update_time_dry(response.text)

    return time_dry <= 0

#function that counts how many times 5 minutes can pass before it will rain
def update_time_dry(data):
    global time_dry
    time_dry = 0
    rain_times = data.split('\n')

    for time in rain_times:
        rain_expectation = time.split('|')[0]

        if(rain_expectation == "000"):
            time_dry += 1
        else:
            return


config.read('config.ini')
will_rain()
asyncio.run(rain_check())