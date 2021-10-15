// original: https://raw.githubusercontent.com/arduino/Arduino/cea203e0a977145774ab334c58c4f3dfaac8cdb8/arduino-core/src/processing/app/Serial.java
// modified for use in my project

/*
  PSerial - class for serial port goodness
  Part of the Processing project - http://processing.org

  Copyright (c) 2004 Ben Fry & Casey Reas

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

package com.antonilol.mc_arduino;

import java.io.IOException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Serial implements SerialPortEventListener {
  /**
   * General error reporting, all corraled here just in case
   * I think of something slightly more intelligent to do.
   */
  private static void errorMessage(String where, Throwable e) {
    System.err.println("Error inside Serial." + where);
    e.printStackTrace();
  }

  private SerialPort port;

  public Serial(String iname) throws Exception {
    try {
      port = new SerialPort(iname);
      port.openPort();
      boolean res = port.setParams(9600, 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, true, true);
      if (!res) {
        System.err.println("Error while setting serial port parameters");
      }
      port.addEventListener(this);
    } catch (SerialPortException e) {
      if (e.getPortName().startsWith("/dev") && SerialPortException.TYPE_PERMISSION_DENIED.equals(e.getExceptionType())) {
        throw new Exception("Error opening serial port " + iname + ". Try consulting the documentation at http://playground.arduino.cc/Linux/All#Permission");
      }
      throw new Exception("Error opening serial port " + iname, e);
    }

    if (port == null) {
      throw new Exception("Serial port " + iname + " not found");
    }
  }

  public void dispose() throws IOException {
    if (port != null) {
      try {
        if (port.isOpened()) {
          port.closePort();
        }
      } catch (SerialPortException e) {
        throw new IOException(e);
      } finally {
        port = null;
      }
    }
  }

  @Override
  public synchronized void serialEvent(SerialPortEvent serialEvent) {
    if (serialEvent.isRXCHAR()) {
      try {
        @SuppressWarnings("unused")
		byte[] buf = port.readBytes(serialEvent.getEventValue());
        
        // do something with `buf` here
        
      } catch (SerialPortException e) {
        errorMessage("serialEvent", e);
      }
    }
  }

  public void write(byte[] bytes) {
    try {
      port.writeBytes(bytes);
    } catch (SerialPortException e) {
      errorMessage("write", e);
    }
  }
}
