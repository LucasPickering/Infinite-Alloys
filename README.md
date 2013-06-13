## Welcome to Infinite Alloys
Infinite Alloys is all about using natural metals to create millions of different alloys. While a very large majority
of these alloys won't have a purpose, many still will. These alloys will then be used to create machines, tools,
upgrades, etc.  
If you want to help, look through the issues tab for unresolved issues. If you don't know how to
use github, get help [here](https://help.github.com/). Join the [#InfiniteAlloys IRC channel on EsperNet](http://webchat.esper.net/?channels=infinitealloys) to talk and ask questions.

### Source Setup
1. Download the correct version of the forge source
2. Install forge using the instructions there
3. Download the correct version of the UE source (listed below)
4. Copy the universalelectricity folder to mcp/src/minecraft/
5. Download the correct version of the BC source (listed below)
6. Copy the src/basiccomponents folder to mcp/src/minecraft/ and the resources folder to mcp/
7. Clone this repo to your mcp folder
8. Open the mcp/eclipse folder as your workspace in eclipse
9. Right click project folder, and click New -> Folder
10. Make sure Minecraft is the parent folder, and name the folder 'common'
11. Click Advanced, select Linked Folder, and set the location to MCP_LOC/common
12. Click Finish, then right click the new common folder, and select Build Path -> Use as Source Folder
13. Repeat 9-13 with the 'resources' folder

### Dependencies
Minecraft Forge  

### Versions
MC: 1.5.2  
Forge: #722  
UE: [Commit 12271b55a6f4bb67b1a512b5a40fe5f9b2ae987e](https://github.com/calclavia/Universal-Electricity/tree/12271b55a6f4bb67b1a512b5a40fe5f9b2ae987e)  
BC: [Commit 2a52d4415f4d45b1307900e9427bd817f48f8f4e](https://github.com/calclavia/Basic-Components/tree/2a52d4415f4d45b1307900e9427bd817f48f8f4e)
