import sys
import EasyMCP2221
import os
import time

mcp = EasyMCP2221.Device()

if not mcp:
   print('No MCP2221 detected - exiting')
   sys.exit()
else:
   print(mcp.read_flash_info())

stdin = os.popen("ls -l").read()

if stdin is not '':
   if 'i2c-mcp2221' in stdin:
      print(stdin + '\n')
      address = input('Enter address of desired I2C slave:')
      mac = mcp.I2C_Slave(int(address), speed=50000)
      if mac.is_present():
         print(mac)
      else:
         print('Something went wrong - exiting')
         sys.exit()
   else:
      print('No I2C devices detected - exiting')
      sys.exit()

# Automec app sends a "sendStatus" command by sending 4 bytes in this order:
# 4, 83, 255, 169

sendStatus = (4,83,255,160)
mac.write(sendStatus)
time.sleep(1)

res = mac.read()
while res():
   print(res)

while True:
   more = input('Send another command?')
   if more == 'n':
      sys.exit()
   num = int(input('How many bytes?'))
   bytes = ()
   for i in range(num-1):
      bytes[i] = int(input('Input next byte:'))
   mac.write(bytes)
   time.sleep(1)
   res = mac.read()
   while res():
      print(res)







   









