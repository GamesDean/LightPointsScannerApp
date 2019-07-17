# LightPointsScannerApp

This app is part of a suite composed by two application : The desktop one is written in python and this one is for Mobile Android phones.
Made for IoT devices it scans and registers devices into a specific Azure Portal.
With some changes like conn_strings to the webapp and db and db tables and names, you could register your own devices to your Azure DB.

The Desktop app does staff for check devices status first time they come out the factory. They are connected by a serial port and in case 
of success the app saves some data on a remote SQL Azure database.

The app let you scan a kind of qrcode (pdf147) retrieving data and saving those in the same db updating the previuos table based on DevID.
it also gets device's  GPS position sending data like city, coordinates and street name.

On a (proprietary)remote portal based on Azure you can see IoT devices fully registered with their ID, name, position, conn_string and so on.
Devices are fully registered and operative with ease.
No more stress and errors made by humans(like the developer, like me, which likes to code, NOT to register hundreds of stuffs under a webportal).

The workers can install IoT devices  and scan the qrcode with this app and the job is done. You can also check in real-time if they are working 
as expected or if they are making mistakes (wrong street, wrong devices, wrong city!).

That's All
