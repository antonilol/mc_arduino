# mc_arduino

todo: write some more here



## Install and use

This mod runs on [Fabric](https://fabricmc.net/), so make sure you have that installed.

Download the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) if you don't have it already. Put it in your mods folder.

Go to [Releases](https://github.com/antonilol/mc_arduino/releases) and download the latest release. Also put it in your mods folder.

The `mods` folder can be found in [.minecraft](https://minecraft.fandom.com/wiki/.minecraft#Locating_.minecraft).

Start Minecraft and open a world or join a server.

Have an arduino connected (or connect in now) with the [program](https://github.com/antonilol/mc_arduino/blob/master/mc-arduino/mc-arduino.ino) loaded.

Type `/mc_arduino serial list` to list connected serial devices.

Type `/mc_arduino serial connect <device>` to start to Minecraft clock on the Arduino.

Type `/mc_arduino serial disconnect` to stop it and make the serial port available again. (You can't upload any programs to an Arduino when the serial port is busy)

`/mc_arduino serial` is also available as `/serial`.

I will be adding a ledstrip to it later for the XP bar or smth

## Compiling

#### Linux users:

```bash
# clone
git clone git@github.com:antonilol/mc_arduino.git

# compile
./compile
```

If you download zip instead don't forget to `chmod +x gradlew compile`.

#### Windows users:

sorry, more info [here](https://github.com/antonilol/mc_arduino/blob/master/compile#L5).

#### Mac OS users:

Maybe the `./compile` script works...

## Developing

Clone just like mentioned above.

To get completions in your IDE (if applicable) run `./gradlew genSources` (unix) or `gradlew.bat genSources` (windows).

More on that [here](https://fabricmc.net/wiki/tutorial:setup#generating_sources).
