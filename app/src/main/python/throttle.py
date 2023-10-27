from pyfirmata import Arduino, util

board = Arduino('COM5')  # Select the correct port
it = util.Iterator(board)
it.start()
while True:
    board.analog[0].enable_reporting()
    print(board.analog[0].read())
time.sleep(0.5)
