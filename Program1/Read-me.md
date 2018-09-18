/* Location of server: C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\Server\ */
Server:
(default port=8000)
java -cp  Program1.jar FileServer start <port number> <new directory for server>
Client:	
set PA1_Server=127.0.0.1:8000
1. java -cp Program1.jar Client upload <ClientPath> <filename for upload>
2. java -cp Program1.jar Client download <ClientPath> <filename for download>
3. java -cp Program1.jar Client dir <ServerPath of file> 
4. java -cp Program1.jar Client mkdir <new directory in the server directory>
5. java -cp Program1.jar Client rmdir <removing directory in the server directory>
6. java -cp Program1.jar Client remove <directory for removing existing file on server>
7. java -cp Program1.jar Client shutdown 

/*  Testing Example    */
Server:
java -cp Program1.jar FileServer start 8000 
Client:
set PA1_Server=127.0.0.1:8000
java -cp Program1.jar Client upload C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\ a123.txt
java -cp Program1.jar Client download C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\ a123.txt
java -cp Program1.jar Client mkdir C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\Server\tt\
java -cp Program1.jar Client dir C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\Server\
java -cp Program1.jar Client rmdir C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\Server\tt\
java -cp Program1.jar Client remove C:\Users\LarrySu\eclipse-workspace\Program_Assignment1\src\Server\a123.txt
java -cp Program1.jar Client shutdown  


