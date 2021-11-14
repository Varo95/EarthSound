# ¡EarthSound App!

## What do you need?
You only need Java [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

Some Spanish words to understand the GUI

## Freatures

- [x] You can register and login into the app. Also you can change your profile details.

- [x] You can access AdminMenu on LoginScreen with user "root" and password "toor". On AdminMenu you have the entire control of the song library

- [x] Passwords are encrypted with MD5

- [x] You can see all Library on MainScreen, PlayLists too.

- [x] You can create PlayLists, edit them and remove it (if you are the creator).

- [x] You can sub/unsub to another users PlayLists

- [x] ¡THIS IS A REAL MUSIC PLAYER!

## How it works

Just double click on .jar file or .exe file.

**NOTE**: That data will missing because the generated H2 DataBase. File is loaded with the Xml configuration file.

### XML Configuration
1. Tag host = Indicates the url of the SQL, for example "192.168.1.100".

2. Tag db = Indicates the database name on the server.

3. Tag user = Indicates the user who can get access to the previous mentioned DataBase.

4. Tag password = Password of the user database.

5. Tag type = It can be mySQL OR H2, if not specified, the connection will be H2 by default.