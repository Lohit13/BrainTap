import argparse
import math, time
from threading import Thread

from pythonosc import dispatcher
from pythonosc import osc_server

import matplotlib.pyplot as plt

gamma = 0
timestamp = 0

def eeg_handler(unused_addr, args, ch1, ch2, ch3, ch4, ch5):
    print("EEG (uV) per channel: ", ch1, ch2, ch3, ch4, ch5)

def gamma_handler(unused_addr, args, ch1):
    print("Absolute per channel: ", ch1)

def ggamma_handler(unused_addr, args, ch1):
    global gamma
    global timestamp
    print("EEG (uV) per channel: ", ch1)
    gamma = ch1
    plt.plot(timestamp, ch1)
    timestamp += 1
    plt.draw()
    plt.pause(0.1)

def acc_handler(unused_addr, args, acc_x, acc_y, acc_z):
    print("ACC values: ", acc_x, acc_y, acc_z)

def blink_handler(unused_addr, args, ch1):
    print("Blink value: ", ch1)

def jaw_handler(unused_addr, args, ch1):
    print("Jaw value: ", ch1)
    time.sleep(5)


def concentration_handler(unused_addr, args, ch1):
    print("Concentration value: ", args, ch1)

def blink_wrapper(dispatcher):
    dispatcher.map("/muse/elements/blink", blink_handler, "BLINK")

def jaw_wrapper(dispatcher):
    dispatcher.map("/muse/elements/jaw_clench", jaw_handler, "JAW")

def main():
    global timestamp
    global gamma
    from pythonosc import dispatcher


    dispatcher = dispatcher.Dispatcher()
    #dispatcher.map("/debug", print)

    #dispatcher.map("/muse/elements/blink", blink_handler, "BLINK")
    #dispatcher.map("/muse/elements/jaw_clench", jaw_handler, "JAW")
    # Get EEG values
    #dispatcher.map("/muse/eeg", eeg_handler, "EEG")

    #Gamma 
    dispatcher.map("/muse/elements/gamma_absolute", gamma_handler, "GAMMA")

    server = osc_server.ThreadingOSCUDPServer(
        (args.ip, args.port), dispatcher)
    #print("Serving on {}".format(server.server_address))

    server.serve_forever()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--ip",
                        default="0.0.0.0",
                        help="The ip to listen on")
    parser.add_argument("--port",
                        type=int,
                        default=5000,
                        help="The port to listen on")
    args = parser.parse_args()

    main()

    
'''

    # Get Accelerometer values
    #dispatcher.map("/muse/acc", acc_handler, "ACC")
    # Blinks
    #dispatcher.map("/muse/elements/blink", blink_handler, "BLINK")

    #dispatcher.map("/muse/elements/experimental/mellow", concentration_handler, "C")

    # Jaw Clench
    #dispatcher.map("/muse/elements/jaw_clench", jaw_handler, "JAW")

'''