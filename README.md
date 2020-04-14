Simple Spring boot application.<br/>
Basicaly a hobby project that i use to learn new skills.

Stack: Spring boot, Spring Security, Hibernate, PostgreSQL, Maven, JSP, Rest API, JUnit, slf4j.

Migrated to Hibernate from Spring data.<br/>
**Last stable Spring data implemenation: commit 2d90a9e339f3daeffa444ce2359fe57460b506d5.** <br/>
Deployed application: https://lotteryhit.herokuapp.com

**DOCUMENTATION**
----

**Code validation rules**
----
The code consists 3 parts: lottery start date, email letter count, 8 random numbers. <br/>
Lottery start date format: DDMMYY <br/>
Email letter count format: <10 letters - 0X, >10 letters XX <br/>
Random number format: XXXXXXXX 

* **Example**: <br/>
  * Lottery started at 13.04.20 18:07.**
  * Email: as@as.com
  * 8 Random numbers: 12345678
  * The code will be: 1304200912345678

**Start new lottery**
----
  Creates a lottery and returns JSON data of successful/unsuccessful creation.
* **URL**
  /rest/admin/start-registration
* **Method:**
  `POST`
* **Authentication:** required
* **Params:** <br />
  * **title:** String [Not empty]<br />
    **limit:** int [Not null]
* **Success Response:**
  * **Code:** 200 <br />
    **Content:** <br />
     status : "OK" 
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** <br/>
status : "FAIL"<br/>
reason: "Lottery title already exists"<br/>
 
**Stop lottery registration**
----
  Stops new code registration for the lottery and returns JSON data status of request.
* **URL**
  /rest/admin/stop-registration
* **Method:**
  `POST`
* **Authentication:** required
* **Params:** <br />
  * **lotteryId:** Long [Not null]<br />
* **Success Response:**
  * **Code:** 200 SUCCESS <br />
    **Content:** <br />
    status : "OK" 
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** <br/>
    status : "FAIL"<br/>
    reason: "Registration is inactive"<br/>    

**Choose lottery winner**
----
  Chooses the winner trough registered and returns JSON data status of request.
* **URL**
  /rest/admin/stop-registration
* **Method:**
  `POST`
* **Authentication:** required
* **Params:** <br />
  * **lotteryId:** Long [Not null]<br />
* **Success Response:**
  * **Code:** 200 SUCCESS <br />
    **Content:** <br />
     status : "OK"<br />
     winnerCode: "sample"
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** <br/>
     status : "FAIL"<br/>
     reason: "Registration is active"  
     OR<br/>
     status : "FAIL"<br/>
     reason: "Lottery is finished"<br/>
     OR<br/>
     status : "FAIL"<br/>
     reason: "No participants in lottery"<br/> 
     
**Register new participating code**
----
  Registers a new participating code and returns JSON data status of request.
* **URL**
  /rest/register
* **Method:**
  `POST`
* **Authentication:** not required
* **Params:** <br />
  * **lotteryId:** Long [Not null]<br />
  * **email:** String [Not empty, email]<br />
  * **age:** Byte [=>21, <=127, Not null]<br />
  * **code:** String [Not empty]<br />
* **Success Response:**
  * **Code:** 200 SUCCESS <br />
    **Content:** <br />
     status : "OK"<br />
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** <br/>
     status : "FAIL"<br/>
     reason: "Registration is inactive"  
     OR<br/>
     status : "FAIL"<br/>
     reason: "Too many participants"<br/>
     OR<br/>
     status : "FAIL"<br/>
     reason: "Code already exists"<br/>
     OR<br/>
     status : "FAIL"<br/>
     reason: "Code already exists"<br/> 
     
**Check win status of registered code**
----
  Check status if the code is a winner one and returns JSON data status of request.
* **URL**
  /rest/status
* **Method:**
  `GET`
* **Authentication:** required
* **Params:** <br />
  * **lotteryId:** Long [Not null]<br />
  * **email:** String [Not empty, email]<br />
  * **code:** String [Not empty]<br />
* **Success Response:**
  * **Code:** 200 SUCCESS <br />
    **Content:** <br />
     status : "WIN"<br />
     OR<br />
     status : "LOSE"<br />
     OR<br />
     status : "PENDING"<br />
* **Error Response:**
  * **Code:** 400 BAD REQUEST <br />
    **Content:** <br/>
     status : "FAIL"<br/>
     reason: "The code is not yours"<br />
     
**All lottery statistics**
----
  Check status if the code is a winner one and returns JSON data status of request.
* **URL**
  /rest/status
* **Method:**
  `GET`
* **Authentication:** not required
* **Params:** NO
* **Success Response:**
  * **Code:** 200 SUCCESS <br />
    **Content:** <br />
     id: 0,<br />
     title: "example",<br />
     startTimestamp: "12.04.20 19:41",<br />
     endTimestamp: "13.04.20 12:20",<br />
     participants: 0<br />
