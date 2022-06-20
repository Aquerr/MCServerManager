# MCSM (Minecraft Server Manager)

MCSM is a self-hosted web-application for rapid creation and easy management of Minecraft servers.
You can think of it as a WEB-GUI for your Minecraft Servers.
It was mainly created for fun and for my personal need for managing my own servers.

Basically, MCSM allows you to host Minecraft servers for different platforms like Forge, Spigot etc...
Servers installed by MCSM are installed on the same machine that the MCSM is running on.
When you log in, you can create new accounts for your friends and other users. Every account has its own list of servers.
You can view server console, settings, directory and files.

Keep in mind that this project is still in early stage! :)

### Features

 * Supported Minecraft platforms:
   * Forge
   * Spigot
   * More yet to come...
 * Accounts, where each account can have its own servers
 * Account Roles
 * Viewing server console
 * Sending commands to server
 * Browsing server files
 * More yet to come...

### How to

**IMPORTANT! If you want to use Forge or any other modded platforms then you need to have your own CurseForge API KEY.**

Currently, MCSM does not have any prebuilt runnable file. Because of that, you need to build it itself.

How to do it?
 * Download/Clone the repository
 * Open project in favourite Java IDE.
 * Run gradle `bootJar` task provided by Spring Boot or run `gradlew bootJar` from command line while in project root.
 * Run built jar:
   * With modded platforms support `java -jar mcsm.jar -DCURSEFORGE_API_KEY=<YOUR CURSEFORGE API KEY HERE>`
   * Without modded platforms support `java -jar mcsm.jar`

### Development

This project is being created mainly for fun and for practice with programming tools.
If you see a way for how this project could be improved, or you noticed a bug then feel free to open an issue ticket or make a PR. 

### License

MIT