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

<img width="738" alt="image" src="https://github.com/user-attachments/assets/1b597c9e-03fc-4b2f-8951-faa6a604184b">


Failed Response :


<img width="744" alt="image" src="https://github.com/user-attachments/assets/efe173a1-b4d8-45fb-a364-880cfadd3e9e">

