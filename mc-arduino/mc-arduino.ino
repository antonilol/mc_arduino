/*
 * Copyright (c) 2021 Antoni Spaanderman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// #include <FastLED.h>

// send types
#define _7SEG 0
#define LEDSTRIP 1
// ...

#define freq 120
// too low frequency will cause delayMicroseconds to be overflowed
// for frequencies lower than 15.2597 Hz, set microTime to false                   1000000 / 16383 / DISPLAYS = 15.2597
#define microTime true

// amount of displays
#define DISPLAYS 4

// pin numbers of negative (ground) pins
const uint8_t displayPins[DISPLAYS] = {5,A3,6,A2}; // 5=the most left display, A2=the most right one

// pin numbers of positive pins of segments
const uint8_t segmentPins[8] = {A0,8,2,4,A4,A1,7,3}; // a-g, dp

uint8_t disps[DISPLAYS] = {0};



void setup() {
  Serial.begin(9600);

  for (uint8_t i=0;i<8;i++) {
    pinMode(segmentPins[i], OUTPUT);
    digitalWrite(segmentPins[i], 0);
  }
  for (uint8_t i=0;i<DISPLAYS;i++) {
    pinMode(displayPins[i], OUTPUT);
    digitalWrite(displayPins[i], 1);
  }
}

bool tr = 0; // type received
uint8_t type[1];

void loop() {
  if (!tr && Serial.available() > 0) {
    Serial.readBytes(type, 1);
    tr = 1;
  }
  if (tr) {
    if (type[0] == _7SEG) {
      if (Serial.available() > 3) {
        uint8_t buf[4];
        Serial.readBytes(buf, 4);
        // also possible with memcpy or pointers, right?
        for (uint8_t i=0; i<sizeof(buf); i++) {
          disps[i] = buf[i];
        }
        tr = 0;
      }
    } else if (type[0] == LEDSTRIP) {
      if (Serial.available() > 3) {
        uint8_t buf[4];
        Serial.readBytes(buf, 4);
        // TODO when i connect my ledstrip
        //if (buf[0] == 0xff) {
        //  FastLED...
        //} else if (buf[0] < LEDS) {
        //  FastLED...
        //}
        tr = 0;
      }
    } else {
      tr = 0;
    }
  }

  // update displays
  for (uint8_t i=0;i<DISPLAYS;i++) {
    digitalWrite(displayPins[i], LOW);
    for (uint8_t j=0;j<8;j++) {
      //if (mat[j+plen*i]) {
      if (disps[i] & (1 << j)) {
        digitalWrite(segmentPins[j], HIGH);
      }
    }
#if microTime == true
    delayMicroseconds(1000000 / freq / DISPLAYS);
#else
    delay(1000 / freq / w);
#endif
    for (uint8_t j=0;j<8;j++) {
      digitalWrite(segmentPins[j], LOW);
    }
    digitalWrite(displayPins[i], HIGH);
  }
}
