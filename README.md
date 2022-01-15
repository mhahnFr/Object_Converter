# Welcome to the Object_Converter!
This repository contains a Java application that can parse 3D-Worlds in the
Wavefront format as well as in its proper format, called *GWO*. An opened world
can be written in both file formats. It requires a Java runtime environment in
version 9 or higher.

## Idea
The initial idea for this project was to create a file format for 3D-Worlds
which is platform indepent and which can be read by multiple threads at the
same time. The easy to parse, text based Wavefront format served as a base
for that file format. Additionally, some individually specified informations
should as well be saved in the format.

## Approach
The approach for this project is quite straight forward: In a graphical user
interface, the user should be able to see all 3D-Objects of the opened world.
For each object, one can select a profile (class), and select the appopriate
values. The values of these profiles are saved in the
_**G**raphic**W**orld**O**bject_ format at a special position as so called
_**S**pecial **BY**tes_. The profiles can also be edited, an editor is
available from the menubar of the main application. Additionally, a second
world can be compared with the currently loaded one. The special properties
can be written to the *GWO* format only.

### *SBY*
The special properties are stored in a special format, called *SBY*. The editor
for the profiles has fields for the basic values such as float. Each 3D-Object
can be assigned to one profile.

### Final notes
Although this project is not under active developement, it can be used as a
base for an object-orientied 3D file format. Also, the Wavefront parser might
be useful for other projects.

© 2017 [mhahnFr](https://www.github.com/mhahnFr)
