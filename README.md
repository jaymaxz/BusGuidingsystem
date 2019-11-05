# BusGuidingsystem

## Methodology

The driver should have a GPS and mobile data or Wi-Fi enabled Android Smartphone. When the driver start the journey he have to start the tracking of his bus. Then his location will feed into the Firebase Real Time Database with minimum two seconds to maximum four seconds, whenever a location change is identified. The location change is obtained through Google Services Location API. It’s more accurate than obtaining the location coordinates using devices GPS module directly. Whenever a location change is capture it will be uploaded t Firebase Real Time Database under the particular buses record. Whenever a data change is happen in Firebase Real Time Database the connected devices are listening to get the data change. So the location uploaded to database is getting by all the connected devices. Then the device proceed its requirements given by the user and only display the relevant buses locations. To display buses we use Google Maps. The buses will show with a clickable icon. When the user click the bus icon it’ll display the details like name, number and speed. The user’s icon will display in a different color icon compared to other buses icons to identify easily.
The big second step is the machine learning part with the collected data from the app. The speed and location of the bus will be recorded with the time. Using the recorded data the program will able to make decisions with the predicted results. This part is to be implemented and under research.

## Implementation

Android device with GPS, Mobile data or Wi-Fi with android Version 5.0(Lollypop) or higher with Updated Google Services can run this app. User can be logged into the app using his Google account / Email address and password or the phone number. These logins and registrations are powered through Firebase Authentication Services.
You can see the implemented prototype with running bus with the name jagathmax2@gmail and 35KmPh speed in Figure 1. Which is currently tracking your own bus. We also can see the other buses with green color icons through swiping the map or use the filters/search to find relevant buses.
The implementation of phase two is still under developing a system to store data to support the learning system. And once the schema is done data collection needed to be done for a long period of time to provide best results.
