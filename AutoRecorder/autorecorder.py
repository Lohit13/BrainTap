# Imports
import argparse, math, time
from threading import Thread

from sklearn import neighbors

import numpy as np

from flask import Flask

# Arguments for script
parser = argparse.ArgumentParser()
parser.add_argument("--ip",
					default="0.0.0.0",
					help="The ip to listen on")
parser.add_argument("--port",
					type=int,
					default=5000,
					help="The port to listen on")
args = parser.parse_args()

f = open('calibration_start','w')
f.write(str(0))
f.close()

# GLOBAL VARS

# 1 -> Concentration | 0 -> Relaxation
final_value = 1

# Current gamma
current_gamma = 0

# Duration of calibration in seconds
c_duration = 60

# Notifies when to start calibration
calibration_toggle = 0

# Notifies when calibration is done
cal_ready = 0

# Signal to stop session
stop_signal = 0

# Gamma threshold
threshold = 0.35

# OSC Handler
def gamma_handler(unused_addr, args, ch1):
	f = open('current_gamma','w')
	f.write(str(ch1))
	f.close()


# FLASK FUNCTIONS - START
def flaskServer():
	global final_value
	global calibration_toggle
	global cal_ready
	global stop_signal

	app = Flask(__name__)

	# Serves the final decision of concentration vs relaxation
	@app.route("/final")
	def finalBool():
		return str(final_value)

	#@app.route("/absolute_gamma")
	#def abs_gamma():
	#	return str(current_gamma)

	# Starts the session
	@app.route("/calibration_start")
	def start_cal():
		calibration_toggle = 1
		f = open('calibration_start','w')
		f.write(str(1))
		f.close()
		return str(1)

	# Notifies (1) when app is ready after calibration
	@app.route("/ready")
	def ready():
		return str(cal_ready)

	# Signal to stop the session
	@app.route("/stop_session")
	def stop():
		stop_signal = 1
		return str(1)

	# Run flask server
	app.run(host='0.0.0.0',port=8080, use_reloader=False)

# FLASK FUNCTIONS - END

def get_gamma():
	global threshold
	try:
		gamma = float(open('current_gamma','r').read())
	except:
		gamma = 0
	if gamma >= threshold:
		label = 1
	else:
		label = 0
	return gamma, label

# SESSION FUNCTIONS - START
# Class encapsulating one session of prediction
class Session:
	def __init__(self):
		self.calibration_data = []
		self.calibration_labels = []
		self.session_data = []
		self.session_labels = []
		self.model=neighbors.KNeighborsClassifier()

	def calibrate(self):
		global c_duration, cal_ready

		print("Starting calibration\n")
		
		time.sleep(1)

		# Collect relaxation data points
		print("Try to relax your mind and body.\n")
		t_end = time.time() + (c_duration/2)
		while time.time() < t_end:
			gamma, label = get_gamma()
			print(gamma)
			self.calibration_data.append([gamma])
			self.calibration_labels.append(label)
			time.sleep(0.1)

		# Collect concentration data points
		print("Try to concentrate on something specific. You may do some quick mental maths etc.\n")
		t_end = time.time() + (c_duration/2)
		while time.time() < t_end:
			gamma, label = get_gamma()
			self.calibration_data.append([gamma])
			self.calibration_labels.append(label)
			time.sleep(0.01)

		# Train model
		self.model.fit(self.calibration_data, self.calibration_labels)
		cal_ready = 1
		print("Calibration complete")

	def live_session(self):
		global stop_signal
		global final_value
		global current_gamma
		global threshold

		time.sleep(1)

		while not stop_signal:
			gamma, label = get_gamma()

			label = self.model.predict(np.array([[gamma]]))
			
			print (label)

			if label[0] == 0:
				final_value = 0
				print("Relaxed...")
			else:
				final_value = 1
				print("Concentrated!!!")

			if final_value == 1 :
				time.sleep(5)
			else:
				time.sleep(0.5)

	def start_session(self):
		self.calibrate()
		self.live_session()
# SESSION FUNCTIONS - END


def startSession():
	session = Session()

	calibration_toggle = float(open('calibration_start','r').read())

	while not calibration_toggle:
		print(calibration_toggle)
		time.sleep(1)
		calibration_toggle = float(open('calibration_start','r').read())

	print("Starting calibration")
	session.calibrate()
	session.live_session()


def main():
	# Have to import it locally, don't know why
	from pythonosc import osc_server
	from pythonosc import dispatcher

	# OSC Server thread
	print("Starting OSC dispatcher")
	dispatcher = dispatcher.Dispatcher()
	dispatcher.map("/muse/elements/gamma_absolute", gamma_handler, "GAMMA_HANDLER")
	
	server = osc_server.ThreadingOSCUDPServer((args.ip, args.port), dispatcher)

	serverThread = Thread(target=server.serve_forever)
	serverThread.start()

	# Web server thread
	print("Starting web server")
	flaskThread = Thread(target=flaskServer)
	flaskThread.start()

	# Session thread
	print("Starting session thread")
	sessionThread = Thread(target=startSession)
	sessionThread.start()

	sessionThread.join()

if __name__=='__main__':
	main()