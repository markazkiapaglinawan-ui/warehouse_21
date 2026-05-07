# WarehouseManagementSystem

## Run

Compile with Java 17-compatible output:

```powershell
javac --release 17 -cp "lib\mysql-connector-j-8.4.0.jar" -d "out\production\WarehouseManagementSystem" src\*.java
```

Open the app:

```powershell
java -cp "out\production\WarehouseManagementSystem;lib\mysql-connector-j-8.4.0.jar" LoginForm
```

MySQL must be running on `127.0.0.1:3306` with database `login_1`, username `root`, and password `root1234`.

## Default Accounts

| Username | Password | Role |
| --- | --- | --- |
| admin | 1234 | ADMIN |
| eboy | 1234 | ADMIN |
| nathan | 1234 | ADMIN |
| receiver | 1234 | RECEIVER |
| starzy | 1234 | CUSTOMER |

## Collaboration

Use Git so each person can work on separate files and combine changes safely:

```powershell
git status
git add .
git commit -m "Describe the change"
git pull
git push
```

Suggested split:

| Person | Area |
| --- | --- |
| Admin owner | Login, users, database setup |
| Eboy | Inventory module |
| Nathan | Orders, receiver, reports |
