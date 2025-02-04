import asyncio
import configparser
import requests
import urllib.parse
import warnings

config = configparser.ConfigParser()
time_dry = 99
screen_open = False

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

    global screen_open
    response = getRequest(url, headers, False)

    if(response == "failed"):
        print(f"request to hub failed, keep last value for screen_open: {screen_open}")
    else:
        screen_open = response[5]["value"] == "open"
    
    return screen_open

def close_screen():
    print("Closing Screen")
    url = f'https://gateway-{config["TaHoma"]["hub_pin"]}.local:{config["TaHoma"]["hub_port"]}/enduser-mobile-web/1/enduserAPI/exec/apply'
    headers = {"Authorization": f'Bearer {config["TaHoma"]["api_key"]}'}
    body = {
        "label": "string",
        "actions": [
            {
            "commands": [
                {
                "name": "close",
                "parameters": [
                    0
                ]
                }
            ],
            "deviceURL": config["TaHoma"]["device_url"]
            }
        ]
        }
    try: 
        with warnings.catch_warnings():
            warnings.simplefilter("ignore")
            requests.post(url, json=body, headers=headers, verify=False)
    except Exception as e:
        print(e)

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
    time_dry = -3
    rain_times = data.split('\n')

    for time in rain_times:
        if(time == ''):
            continue
        rain_expectation = time.split('|')
        if(rain_expectation[0] == "000"):
            time_dry += 1
        else:
            print(f"rain expected at {rain_expectation[1]}")
            return

def getRequest(url, headers, verify):
    try: 
        with warnings.catch_warnings():
            warnings.simplefilter("ignore")
            response = requests.get(url, headers=headers, verify=verify)
            return response.json()
    except Exception as e:
        return "failed"

def __main__():
    config.read('config.ini')
    will_rain()
    print("starting rain detector")
    asyncio.run(rain_check())