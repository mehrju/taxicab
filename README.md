**TAXI CAB!**

**Taxi cab** is an Android application using which one can book Taxi’s for their ride from place A to place B using interface similar to Uber. Goal is to empower users to choose between Taxi’s or Uber like services. Definitely, there are users who would prefer Taxi’s over Uber like services due to various reasons and this app is focused towards serving such users.

*Required User Stories:*
* [x] User can create account/register with Taxi Cab service
* [x] User can login to his account
* [x] Driver can create account/register with Taxi Cab service
* [x] Drive can login to his account
* [x] User once logged in is presented with a Map view.
* [x] User can see nearby drivers
* [x] User can book a Taxi
* [x] Once booked, user will be shown with the position of approaching driver
* [x] Once driver picks up the customer, user app will show customers current position on the map as they approach to their destination
* [x] Upon reaching the destination user pays for the trip using cash payment
* [x] When user books a ride, all nearby drivers will be notified of the incoming reservation.
* [x] Driver is asked to accept the reservation and will be navigated to users location using Google Map
* [x] Once driver picks up the user, he changes the state to “Enroute” to destination
* [x] Driver is then navigated to users destination
* [x] Upon reaching destination, driver accepts the cash payment and ends the trip

*Optional User Stories:*
* [ ] Verify user phone number during registration process
* [ ] Verify driver’s details and his profile during driver registration process
* [ ] Allow user to enter his destination place through the app
* [ ] Send message to customer’s cellphone when drivers arrives at the user’s location
* [ ] User can determine the approximate fare for the travel
* [ ] User can pay for the trip through credit card
* [ ] User can see his ride history
* [ ] User is credited with “Taxi Miles” for each trip that he can use for future rides
* [ ] User can reserve a taxi well in advance
* [ ] User is allowed to rate the driver
* [ ] Driver is allowed to rate user 

Link to Trello agile dashboard: <a href="https://trello.com/b/eeiqsYbE/taxicab">here</a>.
We are using free version of Trello for project management which doesn't allow to share the board publicly. Please use <a href="https://trello.com/invite/taxicabteam/4623523c13fed5d9b05fd845c8026f0f">this link</a> to add yourself as a member to our Trello group and view our progress.

Wireframes: <a href="https://popapp.in/w/projects/56db318a183b508d3ab23dcc/preview/56dd3e99027b5403541f2a89">here</a>.

Taxi cab driver side app is hosted in <a href="https://github.com/xc0ffeelabs/taxicab-driver">this github</a>.

*Walkthrough of all user stories:*

![Video Walkthrough](demo.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

Sprint 1 progress:
-----------------
* [x] Completed setting up parse
* [x] Completed creating user model
* [x] Completed creating driver model
* [x] Completed sign in flow for user and driver
* [x] Completed sign up flow for user and driver

*Walkthrough of all user stories in User app:*

![Video Walkthrough](demo-user.gif)

*Walkthrough of all user stories in Driver app:*

![Video Walkthrough](demo-driver.gif)

Sprint 2 progress:
-----------------
* [x] User can see nearby drivers
* [x] User can book a Taxi
* [x] Once booked, user will be shown with the position of approaching driver
* [x] Once driver picks up the customer, user app will show customers current position on the map as they approach to their destination
* [x] Upon reaching the destination user pays for the trip using cash payment
* [x] When user books a ride, all nearby drivers will be notified of the incoming reservation.
* [x] Driver is asked to accept the reservation and will be navigated to users location using Google Map
* [x] Once driver picks up the user, he changes the state to “Enroute” to destination
* [x] Driver is then navigated to users destination
* [x] Upon reaching destination, driver accepts the cash payment and ends the trip
* [x] Used Google directions api to compute the approximate time
* [x] Push notifications to let user know of drivers arrival and driver know of user request


*Walkthrough of all user stories*

* <a href="https://youtu.be/xPutdQ1TL4k">User app</a>.
* <a href="https://youtu.be/LGfYxMor-b4">Driver app app</a>.
