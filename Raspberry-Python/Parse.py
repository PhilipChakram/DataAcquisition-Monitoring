import time
import os
os.environ["PARSE_API_ROOT"] = "http://ec2-34-209-57-130.us-west-2.compute.amazonaws.com:80/parse"

from sense_hat import SenseHat
from parse_rest.datatypes import Function, Object, GeoPoint
from parse_rest.connection import register
from parse_rest.query import QueryResourceDoesNotExist
from parse_rest.connection import ParseBatcher
from parse_rest.core import ResourceRequestBadRequest, ParseError
from parse_rest.datatypes import Object as ParseObject


APPLICATION_ID = 'd98609f10cbb6361f5427922c98cd8a7689b7778'
REST_API_KEY = 'RaspberryPi'
MASTER_KEY = '097b6ce40eeae2b40306a686d39bb51e1ce65287'

register(APPLICATION_ID, REST_API_KEY, master_key=MASTER_KEY)

sense = SenseHat()
class RaspberryPi(Object):
        pass
class RaspberryPiControl(Object):
        pass
rspcnt=RaspberryPiControl(Start=False)
rspcnt.save()
while 1:
        query=RaspberryPiControl.Query.get(objectId=rspcnt.objectId)
        if(query.Start == True):
                print("True")
                humidity = sense.get_humidity()
                temp = sense.get_temperature()
                pressure = sense.get_pressure()
                raspberryPi = RaspberryPi(Temperature=temp,Humidity=humidity,Pressure=pressure,Day=time.localtime().tm_mday,Year=time.localtime().tm_year,Month=time.localtime().tm_mon,Min=time.localtime().tm_min,Hr=time.localtime().tm_hour,Sec=time.localtime().tm_sec)
                raspberryPi.save()
                print(temp)

                time.sleep(10)
        else:
                print("False")
