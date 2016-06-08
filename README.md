# Wireless Pet Containment 

####Background
  Project Members:
  - Jason Yang
  - Jonathan Chang
  - Xiao Cheng
  - Zhenghao Lu
  - Chau Duan
  
Advisor: Wade Trappe
  
Wireless Pet Containment is a mobile app which allows the user to track the location of his pet wearing a specialized collar. 
The user can set a region on the app to generate a virtual boundary for the pet.  If it steps outside of the boundaries, a notification 
is sent to the mobile device, and an alarm is sounded on the physical collar to deter the pet from exiting the region.  This project
could be a useful alternative to traditional physical electrical fences, and is mainly a conditioning tool for pet owners to establish
"no escape zones".  

####Implementation
There are two main components to the system - the integrated pet collar and the mobile application.  The mobile app runs on Android and 
handles the main computations of the systems, including the algorithm to determinine if the pet is inside or outside a region.  On the 
collar itself, a Raspberry Pi equipped with a GPS dongle determines the coordinates of the pet, which polls at a rate of about once per second.

After the Pi receives the GPS coordinates, it sends the information through a socket to the mobile app.  A continuously running service
reads these coordinates and displays the pet's location on the app.  Through the same socket, the app sends a response to the collar, 
with a "1" indicating if the pet is out of bounds and a 0 for in bounds.  This information will prompt the Pi to signal the alarm sound.

The algorithm used to determine if the pet exceeds the boundary is the Ray Casting algorithm, which solves the Point in Polygon problem.

####Project Contributions

- Jason Yang: Mobile app development, app-side socket communication
- Xiao Cheng: Raspberry Pi code
- Zhenghao Lu: Research of Ray-Casting algorithm
- Jonathan Chang: Documentation and Testing
- Chao Duan: Integration of mobile app and Pi

Note: This repository only contains the mobile app part of the project.  The Pi code, as well as the full report and poster can 
be found here: https://drive.google.com/drive/folders/0B_Y8veLaOeP3a3VzNWQzRmdwNUk
  
