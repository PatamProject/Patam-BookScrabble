// package project.client.model;

// import project.client.Error_Codes;
// import project.client.model.assets.GameModel;

// import java.io.InputStream;
// import java.io.OutputStream;
// import java.io.PrintWriter;
// import java.util.Scanner;

// public class GameHandler implements ClientHandler {
//     Scanner in;
//     PrintWriter out;
//     GameModel game;
    
//     public GameHandler(){}

//     @Override
//     public void handleClient(boolean sentToHost, InputStream inFromClient, OutputStream outToClient) {
//         in = new Scanner(inFromClient);
//         out = new PrintWriter(outToClient);
//         String line = in.next();
//         String[] name_body_split = line.split("&");
//             if(name_body_split.length != 2)
//             {
//                 out.println(Error_Codes.MISSING_ARGS); //Missing arguments
//                 out.flush();
//                 return;
//             }

//         String[] args = name_body_split[1].split(",");
//         if(args == null || args.length < 1)
//         {
//             out.println(Error_Codes.UNKNOWN_CMD); //No arguments
//             out.flush();
//             return;
//         }
//         else 
//         { //Refer to communication protocol!!! 
//             if(sentToHost) //Client to host ("0,1" are handled in --- class)
//             {
//                 switch (args[0]) {      
//                     case "2":
//                         String[] wordArgs = args[1].split("-");
//                         if(wordArgs.length != 4)
//                         {
//                             out.println(Error_Codes.MISSING_ARGS); //Missing arguments
//                             break;
//                         }

//                     case "3":
//                     case "Q":
//                     case "C":
//                         break;
                
//                     default:
//                         break;
//                 }


//             }
//             else //Host to client
//             {
//                 switch (args[0]) {
                    
                        
                        
                
//                     default:
//                         break;
//                 }

//             }
//         }
//         out.flush();
//     }
    
//     private void createGame() {game = new GameModel();}
    
//     @Override
//     public void close() {
//         in.close();
//         out.close();
//     }
// }

// // switch (args[1]) {
// //     case "G":
// //         switch (args[2]) {
// //             case "N":
// //                 createGame();
// //                 out.println(Error_Codes.OK);
// //                 break;
// //             case "S":
// //                 //out.println(game.startGame());
// //                 break;
// //             default:
// //                 out.println(Error_Codes.INVALID_ARGS+",2"); //Unknown argument for G at agrs[2]
// //                 break;
// //         }
// //         break;
// //     case "P":
// //         if(args.length < 3)
// //         {
// //             out.println(Error_Codes.MISSING_ARGS); //Missing arguments
// //             break;
// //         }  
// //         switch (args[2]) {
// //             case "N":
// //                 if(game.addNewPlayer(args[3]))
// //                     out.println(Error_Codes.OK+",True");
// //                 else
// //                     out.println(Error_Codes.OK+"False");
// //                 break;
// //             case "S":
// //                 out.println(Error_Codes.OK+game.getPlayer(args[3]).getScore());
// //                 break;
// //             case "T":
// //                 out.println(Error_Codes.OK+game.tilesToString(args[3]));
// //                 break;
// //             case "W": // Example: P,W,pName,word-row-col-true/false
// //                 String[] wordArgs = args[3].split("-");
// //                 if(wordArgs.length != 4)
// //                 {
// //                     out.println(Error_Codes.MISSING_ARGS); //Missing arguments
// //                     break;
// //                 }
// //                 out.println(Error_Codes.OK+game.placeWord(args[3], game.getWordFromString(args[3] ,wordArgs[0], Integer.parseInt(wordArgs[1]) , Integer.parseInt(wordArgs[2]), Boolean.parseBoolean(wordArgs[3]))));
// //                 break;
// //             case "L":
// //                 out.println(Error_Codes.OK+game.playerLeftGame(args[3]));
// //                 break;
// //             default:
// //                 out.println(Error_Codes.INVALID_ARGS+",2"); //Invalid argument for P at agrs[2]
// //                 break;
// //         }
// //         break;
// //     case "T":
// //         String response = game.getPlayer(args[2]).getRack().takeTileFromBag();
// //         if (response.equals("0"))
// //             out.println(game.getWinner());
// //         else
// //             out.println(Error_Codes.OK+game.getPlayer(args[2]).getRack().takeTileFromBag());
// //         break;
// //     default:
// //         out.println(Error_Codes.INVALID_ARGS+",1"); //Invalid argument at args[1]
// //         break;
// // }    