# Imports
import argparse, math, time, asyncio, requests
from threading import Thread
from datetime import datetime, timedelta
import pyautogui as pa
from collections import deque

parser = argparse.ArgumentParser()
parser.add_argument("--ip",
					default="0.0.0.0",
					help="The ip to listen on")
parser.add_argument("--port",
					type=int,
					default=5000,
					help="The port to listen on")
args = parser.parse_args()

# Global declaration
pa.FAILSAFE = False

direction = 0

gamma = 0.0

jaw_history = deque(maxlen=10)

blink_history = deque(maxlen=10)

scroll_threshold = 0.2


# CURSOR DIRECTION FUNCTIONS - START
def jaw_handler(unused_addr, args, ch1):
	global direction
	direction += 1
	if direction == 4:
		direction = 0
	print('JAW')
# CURSOR DIRECTION FUNCTIONS - END


# CURSOR CLICK FUNCTIONS - START
def blink_handler(unused_addr, args, ch1):
	global blink_history
	blink_history.append(1)

def check_blink():
	global blink_history
	if sum(blink_history) >= 3:
		for i in range(10):
			blink_history.append(0)
		return True
	else:
		return False

def manageBlink():
	global blink_history
	while True:
		if check_blink():
			pa.click(clicks=2)
			time.sleep(3)
		else:
			blink_history.append(0)
			time.sleep(0.2)
# CURSOR CLICK FUNCTIONS - END


# CURSOR MOVE FUNCTIONS - START
def gamma_handler(unused_addr, args, ch1):
	global gamma
	gamma = ch1

def manageCursor():
	global direction
	global gamma
	global scroll_threshold
	while(True):
		print(str(gamma)+' | '+str(scroll_threshold))
		if gamma >= scroll_threshold:
			if direction == 0:
				pa.moveRel(0,-5)
			if direction == 1:
				pa.moveRel(5,0)		
			if direction == 2:
				pa.moveRel(0,5)
			if direction == 3:
				pa.moveRel(-5,0)
# CURSOR MOVE FUNCTIONS - END


# Main
def main():
	from pythonosc import osc_server
	from pythonosc import dispatcher
	
	# Start server
	dispatcher = dispatcher.Dispatcher()
	dispatcher.map("/muse/elements/gamma_absolute", gamma_handler, "CONCENTRATION")
	dispatcher.map("/muse/elements/jaw_clench", jaw_handler, "JAW")
	dispatcher.map("/muse/elements/blink", blink_handler, "BLINK")
	
	server = osc_server.ThreadingOSCUDPServer((args.ip, args.port), dispatcher)

	serverThread = Thread(target=server.serve_forever)
	serverThread.start()

	# Start blink service
	clickThread = Thread(target=manageBlink)
	clickThread.start()

	# Start cursor
	cursorThread = Thread(target=manageCursor)
	cursorThread.start()
	cursorThread.join()


if __name__=='__main__':
	main()