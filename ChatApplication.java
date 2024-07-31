import java.io.*; 
import java.util.Scanner; 
 
public class ChatApplication  
{ 
    static class Chat 
 { 
        private String message; 
 
        public synchronized void sendMessage(String message) 
       { 
            this.message = message;             notify(); 
        } 
 
        public synchronized String receiveMessage()  
       { 
            while (message == null) 
           {                 try                  { 
                    wait(); 
                } 
 catch (InterruptedException e) 
 { 
                    e.printStackTrace(); 
                } 
            } 
            String receivedMessage = message; 
            message = null; 
            return receivedMessage; 
        } 
    } 
 
    static class User1 implements Runnable 
   { 
        private final Chat chat; 
        private final PrintWriter writer; 
 
        public User1(Chat chat, PrintWriter writer)  
       {             this.chat = chat;             this.writer = writer; 
        } 
 
        @Override 
        public void run()  
        { 
            Scanner scanner = new Scanner(System.in); 
            Try            { 
                while (true)  
                { 
                    System.out.print("User1: ");                     String message = scanner.nextLine();                     chat.sendMessage("User1: " + message);                     writer.println("User1: " + message);                     writer.flush(); 
                    if (message.equalsIgnoreCase("exit")) 
                    {                         break; 
                    } 
                    synchronized (chat)  
                   { 
                        chat.wait(); 
                    } 
                } 
            }  
               catch (InterruptedException e)  
             { 
                e.printStackTrace(); 
            }              finally  
            { 
                System.out.println("User1 left the chat."); 
            } 
        } 
    } 
 
    static class User2 implements Runnable  
    { 
        private final Chat chat; 
        private final PrintWriter writer; 
 
        public User2(Chat chat, PrintWriter writer)  
       { 
            this.chat = chat;             this.writer = writer; 
        } 
 
        @Override 
        public void run()  
      { 
            Scanner scanner = new Scanner(System.in);             try             { 
                while (true) 
                { 
                    synchronized (chat)  
                    { 
                        chat.wait(); 
                    } 
                    String receivedMessage = chat.receiveMessage();                     System.out.println(receivedMessage);                     writer.println(receivedMessage); 
                    writer.flush(); 
                    if (receivedMessage.equalsIgnoreCase("User1: exit")) 
                   {                         break; 
                    } 
                    System.out.print("User2: ");                     String message = scanner.nextLine();                     chat.sendMessage("User2: " + message);                     writer.println("User2: " + message); 
                    writer.flush(); 
                    synchronized (chat)  
                   { 
                        chat.notify(); 
                    } 
                } 
            } 
              catch (InterruptedException e) 
               { 
                e.printStackTrace(); 
            }           finally  
         
{ 
                System.out.println("User2 left the chat."); 
            } 
        } 
    } 
 
    public static void main(String[] args) throws IOException  
   { 
        Chat chat = new Chat(); 
 
        File file = new File("chat_history.txt"); 
        file.createNewFile(); 
        PrintWriter writer = new PrintWriter(new FileWriter(file, true)); 
 
        Thread user1Thread = new Thread(new User1(chat, writer)); 
        Thread user2Thread = new Thread(new User2(chat, writer)); 
 
        user1Thread.start();         user2Thread.start(); 
         try  {             user1Thread.join();             user2Thread.join(); 
        } 
   catch (InterruptedException e)  
    { 
            e.printStackTrace(); 
        } 
 
        writer.close(); 
    } 
}  
 
