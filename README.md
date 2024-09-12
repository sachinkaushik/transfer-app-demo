Sample Transfer app

Created a new API **/transfer** in **AccountController**. It is thread-safe and will handle dead lock condition as well using ReentrantLock. 

And, we can also make transferMoney() as Tranactional just putting @Transactional annotation while working with relation db .

payload : 

{
    "accountFromId" : "10001",
    "accountToId" : "10002",
    "balance" : 20000012
}

Success Response : 

<img width="629" alt="image" src="https://github.com/user-attachments/assets/4fda79e0-ad74-442b-9478-10c094836e24">

Failed Response :


<img width="694" alt="image" src="https://github.com/user-attachments/assets/c5a07f2f-b653-435d-9c88-bbfa6f254e1f">
