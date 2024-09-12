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

<img width="748" alt="image" src="https://github.com/user-attachments/assets/b9a65f5a-6415-4ed8-83e4-ab7dd85f0481">




Failed Response :


<img width="727" alt="image" src="https://github.com/user-attachments/assets/b3c1a6f5-0752-4c7d-8024-56ba3bea1f33">


<img width="717" alt="image" src="https://github.com/user-attachments/assets/2e4ea8e9-28de-4150-9c27-44886d032f1f">


